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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;

public class RentRollAdaptor {

    public static String inFile = "C:\\projects\\Starlight\\database_exports\\tenantRoster.csv";

    public static String outFile = "C:\\projects\\Starlight\\database_exports\\example-output.xml";

    public enum RosterReaderState {
        ExpectAddress, ExpectHeader, ExpectData
    }

    public static Set<String> strings = new HashSet<String>(Arrays.asList("misc", "roof", "strg", "sign1", "nonrespk", "nrp01")); // add erroneous "apt numbers" here (lower case)

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");

    private static Map<String, BuildingIO> buildings = new HashMap<String, BuildingIO>();

    public static void main(String[] args) throws IOException {
        String tenantRosterFile = inFile;

        readTenantRoster(tenantRosterFile);

        ImportIO importIO = EntityFactory.create(ImportIO.class);
        for (BuildingIO building : buildings.values()) {
            if (!building.units().isNull()) {
                System.out.println(building.externalId().getStringView());
                for (AptUnitIO unit : building.units()) {
                    System.out.println("\t\t\t\t" + unit.number().getStringView() + ": " + unit.availableForRent().getStringView());
                }
                importIO.buildings().add(building);
            }

        }

        File f = new File(outFile);
        FileWriter w = null;
        try {
            w = new FileWriter(f);
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityName());
            xmlWriter.setEmitId(false);
            xmlWriter.write(importIO);
            w.write(xml.toString());
            w.flush();
        } catch (IOException e) {
            //TODO error stuff
        } finally {
            IOUtils.closeQuietly(w);
        }

    }

    /**
     * @param args
     * @throws IOException
     */
    public static void readTenantRoster(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(new File(fileName).toPath(), Charset.defaultCharset());
        RosterReaderState readerState = RosterReaderState.ExpectHeader;

        int n = 0;
        boolean gotData = false;
        boolean gotHeaders = false;
        String address = "uninitialized address";
        Map<String, Integer> keyToIndexMap = null;
        BuildingIO building = EntityFactory.create(BuildingIO.class);
        Iterator<String> it = lines.iterator();

        while (it.hasNext()) {
            String line = it.next();
            ++n;
            switch (readerState) {

            case ExpectHeader:
                if (line.startsWith("\"Unit\"")) {
                    String[] keys = splitCSVRow(line);
                    keyToIndexMap = new HashMap<String, Integer>();
                    for (int i = 0; i < keys.length; ++i) {
                        keyToIndexMap.put(keys[i], i);
                    }

                    readerState = RosterReaderState.ExpectData;
                    it.next();
                    it.next(); // skip 2 lines w/ useless data:
//                        ,," Sq Ft",,," Rent"," Rent"," Deposit"," Deposit",," Expiration",,
//                        "Current/Notice/Vacant Residents",,,,,,,,,,,,
                }
                break;

            case ExpectData:
                String[] values = splitCSVRow(line);
                if (!isEmpty(values)) {

                    gotData = true;
                    // TODO normalize unitID form
                    String unitNumber = values[keyToIndexMap.get("Unit")];
                    assert unitNumber != null : "line " + n + ": failed to fetch UNIT ID from the current line";
                    if (CommonsStringUtils.isEmpty(unitNumber)) {
                        // TODO verify for "Total"
                        building.externalId().setValue("vv"); //TODO set value

                        // start new building
                        building = EntityFactory.create(BuildingIO.class);

                    } else {

                        AptUnitIO unit = EntityFactory.create(AptUnitIO.class);
                        unit.number().setValue(unitNumber);

                        String vacant = values[keyToIndexMap.get("Name")];
                        assert vacant != null : "line " + n + ": failed to fetch TENANT_NAME from the current line";
                        if (vacant.toLowerCase().trim().equals("vacant")) {
                            unit.availableForRent().setValue(new LogicalDate());
                            String number = values[keyToIndexMap.get("Unit")];

                            if (!strings.contains(number.toLowerCase().trim())) {
                                building.units().add(unit);
                            }
                        }
                    }
                } else {
                    if (gotData == true) {
                        gotData = false;
                        readerState = RosterReaderState.ExpectAddress;
                    }
                }

                break;

            case ExpectAddress:

                address = splitCSVRow(line)[1];
                String externalId = propertyCodeFromAddress(address);
                building = buildings.get(externalId);
                if (building == null) {
                    building = EntityFactory.create(BuildingIO.class);
                    building.externalId().setValue(externalId);
                    buildings.put(externalId, building);
                    System.out.println("externalId = [" + externalId + "]");
                }
                readerState = RosterReaderState.ExpectData;

                break;
            }
        }
    }

    public interface Row {
        String get(String key);
    }

    public class RowImpl implements Row {

        String[] values;

        Map<String, Integer> keyToIndexMap;

        public RowImpl(String[] values, Map<String, Integer> keyToIndexMap) {
            this.values = values;
            this.keyToIndexMap = keyToIndexMap;
        }

        @Override
        public String get(String key) {
            return values[keyToIndexMap.get(key)];
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

    public static class Building {

        public Map<String, AptUnitIO> units = new HashMap<String, AptUnitIO>();

        final public String propertyCode;

        public Building(String propertyCode) {
            this.propertyCode = propertyCode;
        }

    }

    public static String propertyCodeFromAddress(String address) {
        // TODO
        return address;
    }

    public static String removeQuotes(String str) {
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    public static String[] splitCSVRow(String row) {
        ArrayList<String> splitted = new ArrayList<String>(50);

        boolean ignoreCommas = false;
        boolean startValue = true;
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < row.length(); ++i) { // crude way to skip ",,,'Total',"
            char c = row.charAt(i);

            if (startValue) {
                startValue = false;
                splitted.add(value.toString());
                value = new StringBuffer();
            }
            if (!ignoreCommas & c == '"') {
                ignoreCommas = true;
            } else if (ignoreCommas & c == '"') {
                ignoreCommas = false;
            } else {
                if (!ignoreCommas & c == ',') {
                    startValue = true;
                } else {
                    value.append(c);
                }
            }
        }
        splitted.add(value.toString());

        return splitted.toArray(new String[splitted.size()]);
    }
}
