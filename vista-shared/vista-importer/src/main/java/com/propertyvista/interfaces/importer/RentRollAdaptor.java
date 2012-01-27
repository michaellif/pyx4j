/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 17, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class RentRollAdaptor implements ImportAdapter {

    public static Set<String> strings = new HashSet<String>(Arrays.asList("misc", "roof", "strg", "sign1", "nonrespk", "nrp01", "nrp02", "nrp03", "nrp04",
            "nrp05", "nrp06", "current/notice/vacant residents", "nrp-02", "roof1", "roof2", "roof3", "roof4")); // add erroneous "apt numbers" here (lower case)

    private static List<BuildingIO> buildings = new LinkedList<BuildingIO>();

    @Override
    public ImportIO parse(byte[] data, DownloadFormat format) {
        switch (format) { //TODO remove redundant functions (one of the parse ones)
        case XML:
            throw new Error("Please use Vista adapter type for XML files");
        case CSV:
            try {
                return parse(data, "csv");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new Error(e);
            }
        case XLS:

            try {
                return parse(data, "xls");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new Error(e);
            }
        case XLSX:
            try {
                return parse(data, "xlsx");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new Error(e);
            }
            // TODO
        default:
            throw new Error("Unsupported file format");
        }
    }

    public static ImportIO parse(byte[] inFile, String type) throws IOException {

        readTenantRoster(inFile, type); // populate buildings

        int unitCount = 0;
        ImportIO importIO = EntityFactory.create(ImportIO.class);
        for (BuildingIO building : buildings) {
            if (!building.units().isNull()) {
                importIO.buildings().add(building);
                unitCount += building.units().size();
            }
        }

        System.out.println("Exported " + importIO.buildings().size() + " buildings");
        System.out.println("Exported " + unitCount + " units");
        return importIO;

    }

    /**
     * @param args
     * @throws IOException
     */
    public static void readTenantRoster(byte[] fileName, String type) throws IOException {

        EntityCSVReciver<RentRollCSV> csv = EntityCSVReciver.create(RentRollCSV.class);
        csv.setHeaderLinesCount(2);
        csv.setHeadersMatchMinimum(EntityFactory.getEntityMeta(RentRollCSV.class).getMemberNames().size());
        InputStream is = new ByteArrayInputStream(fileName);

        if (type.equals("csv")) {
            try {
                CSVParser parser = new CSVParser();
                parser.setAllowComments(false);
                CSVLoad.loadFile(is, parser, csv);
            } finally {
                IOUtils.closeQuietly(is);
            }
        } else {
            try {
                XLSLoad.loadFile(is, csv);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        BuildingIO building = EntityFactory.create(BuildingIO.class); //create initial building

        for (RentRollCSV line : csv.getEntities()) {
            if (line.resident().getStringView().equals("Total") && line.name().getStringView().equals("All Properties")) { // check if the end of file
                return;
            }

            AptUnitIO unit = EntityFactory.create(AptUnitIO.class);
            String unitNumber = line.unit().getValue();

            if (unitNumber != null && !strings.contains(unitNumber.toLowerCase().trim()) && !line.resident().getStringView().equals("Total")) {

                unit.number().setValue(unitNumber);
                double marketRent;
                LogicalDate availableForRent = new LogicalDate();

                if (line.name().getStringView().toLowerCase().trim().equals("vacant")) {
                    marketRent = parseMoney(line.marketRent().getStringView());
                    unit.availableForRent().setValue(availableForRent);
                    unit.marketRent().setValue(marketRent);
                }

                building.units().add(unit);
            } else if (line.resident().getStringView().equals("Total")) { //building address is in this line
                String externalId = line.name().getValue();
                Collection<BuildingIO> verifiedBuildings = new LinkedList<BuildingIO>();
                building.externalId().setValue(externalId);
                verifiedBuildings = splitComplexes(building);
                verifiedBuildings = trimUnitNumbers(verifiedBuildings);
                buildings.addAll(verifiedBuildings);
                building = EntityFactory.create(BuildingIO.class);
// assume document is properly formatted, if unit number == null the line does not contain valid  data
            }
        }
    }

    private static double parseMoney(String money) {
        NumberFormat nf = new DecimalFormat("#,###.##");
        try {
            return nf.parse(money).doubleValue();
        } catch (ParseException e) {
            throw new Error(e);
        }

    }

    public static boolean isEmpty(String values[]) {
        for (String val : values) {
            if (!"".equals(val)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, BuildingIO> buildingList = new HashMap<String, BuildingIO>();

    public static Collection<BuildingIO> splitComplexes(BuildingIO building) { //sorts complexes that contain multiple buildings under the same externalId. Assumes either all units are with hyphen or none.
        Iterator<AptUnitIO> it = building.units().iterator();
        buildingList = new HashMap<String, BuildingIO>();
        while (it.hasNext()) {
            AptUnitIO unit = it.next();
            BuildingIO b = getBuildingForUnitNumber(building.externalId().getValue(), unit.number().getValue());
            if (b != null) {
                it.remove();
                b.units().add(unit);
            }
        }

        if (building.units().size() > 0) {
            buildingList.put(building.externalId().getValue(), building);
        }
        return buildingList.values();
    }

    private static BuildingIO getBuildingForUnitNumber(String externalId, String unitNumber) {
        if (unitNumber.contains("-")) {
            String[] t = unitNumber.split("-");

            try {
                Integer.parseInt(t[0]);
            } catch (NumberFormatException e) {
                throw new Error("Illegal building number: " + e);
            }

            String newexternalId = externalId + "(b" + t[0] + ")";
            BuildingIO b = buildingList.get(newexternalId);
            if (b == null) {
                b = EntityFactory.create(BuildingIO.class);
                b.externalId().setValue(newexternalId);
                buildingList.put(b.externalId().getValue(), b);
            }
            return b;
        } else {
            return null;
        }
    }

    private static Collection<BuildingIO> trimUnitNumbers(Collection<BuildingIO> verifiedBuildings) {
        Iterator<BuildingIO> it = verifiedBuildings.iterator();
        while (it.hasNext()) {
            for (AptUnitIO u : it.next().units()) {
                if (u.number().getValue().contains("-")) {
                    String[] t = u.number().getValue().split("-");

                    try {
                        Integer.parseInt(t[1]);
                    } catch (NumberFormatException e) {
                        throw new Error("Illegal apartment number: " + e);
                    }

                    u.number().setValue(t[1]); // sets proper unit number, removes t[0] (building number) from it
                }
            }
        }
        return verifiedBuildings;
    }
}
