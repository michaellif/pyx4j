/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.portal.server.importer.bean.Residential;
import com.propertyvista.portal.server.importer.csv.AvailableUnit;

public class Reader {

    private static final Logger log = LoggerFactory.getLogger(Reader.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd");

    private Residential residential;

    private final List<AvailableUnit> units = new ArrayList<AvailableUnit>();

    public void readCsv() throws IOException, ParseException {
        String csv = IOUtils.getTextResource(XmlUtil.resourceFileName(XmlUtil.class, "units.csv"));

        StringTokenizer lines = new StringTokenizer(csv, "\n");
        lines.nextToken(); // skip the headings
        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            log.debug(line);

            String[] values = line.split(",");

            AvailableUnit unit = new AvailableUnit();

            unit.setPropertyCode(values[0].trim());
            unit.setAddress(values[1].trim());
            unit.setCity(values[2].trim());
            unit.setProvince(values[3].trim());
            unit.setUnitNumber(values[4].trim());
            unit.setType(values[5].trim());
            unit.setArea(Double.valueOf(values[6].trim()));
            unit.setRent(Double.valueOf(values[7].trim()));
            unit.setDescription(values[8].trim());
            unit.setAvailable(DATE_FORMAT.parse(values[9].trim()));

            units.add(unit);
            log.info("" + unit);
        }
    }

    public void readXml() throws IOException, JAXBException {
        // read
        String xml = IOUtils.getTextResource(XmlUtil.resourceFileName(XmlUtil.class, "data.xml"));
        log.debug("Loaded " + xml);

        residential = XmlUtil.unmarshallResidential(xml);
        log.debug("Residential\n " + residential + "\n");
    }

    public Residential getResidential() {
        return residential;
    }

    public List<AvailableUnit> getUnits() {
        return units;
    }
}
