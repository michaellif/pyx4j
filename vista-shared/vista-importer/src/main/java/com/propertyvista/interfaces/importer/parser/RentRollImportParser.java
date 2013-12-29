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
package com.propertyvista.interfaces.importer.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xls.RentRollCSV;

public class RentRollImportParser implements ImportParser {

    private final static Logger log = LoggerFactory.getLogger(RentRollImportParser.class);

    private boolean isInComplex = false; //if in complex, unitNumber "1-A" splits into building = building(b1), unit A. Otherwise it's unit 1-A

    private static final I18n i18n = I18n.get(RentRollImportParser.class);

    public static Set<String> strings = new HashSet<String>(Arrays.asList("misc", "roof", "strg", "sign1", "nonrespk", "nrp01", "nrp02", "nrp03", "nrp04",
            "nrp05", "nrp06", "current/notice/vacant residents", "nrp-02", "roof1", "roof2", "roof3", "roof4")); // add erroneous "apt numbers" here (lower case)

    private final List<BuildingIO> buildings = new ArrayList<BuildingIO>();

    @Override
    public ImportIO parse(byte[] data, DownloadFormat format) {
        try {
            switch (format) {

            case XML:
                throw new Error("Please use Vista adapter type for XML files");
            case CSV:
                readTenantRoster(data, format);
                break;
            case XLS:
                readTenantRoster(data, format);
                break;
            case XLSX:
                readTenantRoster(data, format);
                break;
            default:
                throw new Error("Unsupported file format");
            }

            int unitCount = 0;
            ImportIO importIO = EntityFactory.create(ImportIO.class);
            for (BuildingIO building : buildings) {
                if (!building.units().isNull()) {
                    importIO.buildings().add(building);
                    unitCount += building.units().size();
                }
            }

            log.debug("Loaded {} buildings", importIO.buildings().size());
            log.debug("Loaded {} units", unitCount);
            return importIO;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    private void readTenantRoster(byte[] fileName, DownloadFormat format) throws IOException {

        EntityCSVReciver<RentRollCSV> csv = EntityCSVReciver.create(RentRollCSV.class);
        csv.setHeaderLinesCount(1, 2);
        csv.setHeadersMatchMinimum(EntityFactory.getEntityMeta(RentRollCSV.class).getMemberNames().size());
        InputStream is = new ByteArrayInputStream(fileName);

        if (format.name().equals("CSV")) {
            try {
                CSVParser parser = new CSVParser();
                parser.setAllowComments(false);
                CSVLoad.loadFile(is, parser, csv);
            } finally {
                IOUtils.closeQuietly(is);
            }
        } else {
            try {
                XLSLoad.loadFile(is, format == DownloadFormat.XLSX, csv);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        BuildingIO building = EntityFactory.create(BuildingIO.class); //create initial building
        boolean voidData = false;
        for (RentRollCSV line : csv.getEntities()) {

            if (line.resident().getStringView().equals("Total") && line.name().getStringView().equals("All Properties")) { // check if the end of file
                return;
            }

            AptUnitIO unit = EntityFactory.create(AptUnitIO.class);
            String unitNumber = line.unit().getValue();

            if (unitNumber != null && !strings.contains(unitNumber.toLowerCase().trim()) && !line.resident().getStringView().equals("Total")
                    && !line.resident().getStringView().equals("Future Residents/Applicants") && !voidData) {

                unit.number().setValue(unitNumber);
                BigDecimal marketRent;
                LogicalDate availableForRent = new LogicalDate();

                marketRent = parseMoney(line.marketRent().getStringView());
                unit.marketRent().setValue(marketRent);
                if (line.name().getStringView().toLowerCase().trim().equals("vacant")) {
                    unit.availableForRent().setValue(availableForRent);
                } else {
                    unit.availableForRent().setValue(null);
                }

                building.units().add(unit);

            } else if (line.resident().getStringView().equals("Future Residents/Applicants")) {
                voidData = true;
            } else if (line.resident().getStringView().equals("Total")) { //building address is in this line
                voidData = false;
                String externalId = line.name().getValue();
                Collection<BuildingIO> verifiedBuildings = new LinkedList<BuildingIO>();
                building.externalId().setValue(externalId);
                verifiedBuildings = splitComplexes(building);
                verifiedBuildings = trimUnitNumbers(verifiedBuildings);
                buildings.addAll(verifiedBuildings);
                building = EntityFactory.create(BuildingIO.class);
// assume document is properly formatted, if unit number == null the line does not contain valid  data

                //Star over with new building
                building = EntityFactory.create(BuildingIO.class);
            }
        }
    }

    private BigDecimal parseMoney(String money) {
        NumberFormat nf = new DecimalFormat("#,###.##");
        try {
            return new BigDecimal(nf.parse(money).doubleValue());
        } catch (ParseException e) {
            throw new UserRuntimeException(i18n.tr("Please make sure all your Rent Roll values are in proper numeric format"));
        }

    }

    private Collection<BuildingIO> splitComplexes(BuildingIO building) { //sorts complexes that contain multiple buildings under the same externalId. Assumes either all units are with hyphen or none.
        Iterator<AptUnitIO> it = building.units().iterator();
        Map<String, BuildingIO> buildingList = new HashMap<String, BuildingIO>();
        while (it.hasNext()) {
            AptUnitIO unit = it.next();
            BuildingIO b = getBuildingForUnitNumber(buildingList, building.externalId().getValue(), unit.number().getValue());
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

    private BuildingIO getBuildingForUnitNumber(Map<String, BuildingIO> buildingList, String externalId, String unitNumber) {
        if (unitNumber.contains("-")) {
            isInComplex = true;
            String[] t = unitNumber.split("-");

            try {
                Integer.parseInt(t[0]);
            } catch (NumberFormatException e) {
                isInComplex = false;
                return null;
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

    private Collection<BuildingIO> trimUnitNumbers(Collection<BuildingIO> verifiedBuildings) {
        Iterator<BuildingIO> it = verifiedBuildings.iterator();
        while (it.hasNext()) {
            for (AptUnitIO u : it.next().units()) {
                String s = u.number().getValue();
                if (u.number().getValue().contains("-")) {
                    if (isInComplex) {
                        String[] t = u.number().getValue().split("-");
                        u.number().setValue(t[1]); // sets proper unit number, removes t[0] (building number) from it
                    }
                }
            }
        }
        return verifiedBuildings;
    }
}
