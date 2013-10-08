/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 9, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.ils.Property;
import com.yardi.entity.ils.VacateDate;
import com.yardi.entity.mits.Information;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.settings.PmcYardiCredential.Platform;
import com.propertyvista.yardi.stub.YardiILSGuestCardStub;

public class YardiILSServiceClientTest {

    private final static Logger log = LoggerFactory.getLogger(YardiILSServiceClientTest.class);

    public static void main(String[] args) {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));

        PmcYardiCredential yc = EntityFactory.create(PmcYardiCredential.class);
        yc.username().setValue("propertyvista-ilsws");
        yc.password().number().setValue("55318");
        yc.serverName().setValue("aspdb04");
        yc.database().setValue("afqoml_live");
        yc.platform().setValue(Platform.SQL);
        yc.ilsGuestCardServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/Webservices/itfilsguestcard20.asmx");

        PhysicalProperty property = null;
        try {
            if (true) {
                YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
                property = stub.getPropertyMarketingInfo(yc, "gran0002");
                for (Property building : property.getProperty()) {
                    log.info(print(building, "0005"));
                }
            } else {
                String xml = getSampleXml();
                property = MarshallUtil.unmarshal(PhysicalProperty.class, xml);
            }
            log.info("PhysicalProperty: {}", property.getProperty().get(0).getPropertyID().getIdentification().getPrimaryID());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static String print(Property building, String unitId) {
        StringBuilder sb = new StringBuilder();
        for (ILSUnit unit : building.getILSUnit()) {
            if (unitId != null && !unit.getId().equals(unitId)) {
                continue;
            }
            sb.append("Unit: " + unit.getId() + " {\n");
            for (Information info : unit.getUnit().getInformation()) {
                sb.append("\tInformation: {\n") //@formatter:off
                .append("\t\tOccupancy: " + info.getUnitOccupancyStatus() + "\n")
                .append("\t\tLeaseStatus: " + info.getUnitLeasedStatus() + "\n")
                .append("\t}\n");//@formatter:on
            }
            Availability avail = unit.getAvailability();
            if (avail != null) {
                sb.append("\tAvailability: {\n") //@formatter:off
                .append("\t\tStatus: " + avail.getVacancyClass() + "\n")
                .append("\t\tVacated on: " + printDate(avail.getVacateDate()) + "\n")
                .append("\t\tAvailable on: " + printDate(avail.getMadeReadyDate()) + "\n")
                .append("\t\tMoveOut Code: " + avail.getMoveOutCode() + "\n")
                .append("\t}\n");//@formatter:on
            }
            sb.append("}\n");
        }
        return sb.toString();
    }

    private static String getSampleXml() throws IOException {
        Class<?> refClass = YardiILSServiceClientTest.class;
        String rcPath = refClass.getPackage().getName().replaceAll("\\.", "/") + "/PhysicalProperty.xml";
        InputStream is = refClass.getClassLoader().getResourceAsStream(rcPath);
        return IOUtils.toString(is);
    }

    private static String printDate(VacateDate vacDate) {
        StringBuilder date = new StringBuilder();
        date.append(vacDate.getYear() + "-");
        date.append(vacDate.getMonth() + "-");
        date.append(vacDate.getDay());
        return date.toString();
    }

    private static String printDate(MadeReadyDate rdyDate) {
        StringBuilder date = new StringBuilder();
        date.append(rdyDate.getYear() + "-");
        date.append(rdyDate.getMonth() + "-");
        date.append(rdyDate.getDay());
        return date.toString();
    }
}
