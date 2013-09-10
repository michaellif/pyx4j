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

import com.yardi.entity.ils.PhysicalProperty;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.settings.PmcYardiCredential.Platform;
import com.propertyvista.yardi.stub.YardiILSGuestCardStub;

public class YardiILSServiceClientTest {

    private final static Logger log = LoggerFactory.getLogger(YardiILSServiceClientTest.class);

    public static void main(String[] args) {
        PmcYardiCredential yc = EntityFactory.create(PmcYardiCredential.class);
        yc.username().setValue("propertyvista-ilsws");
        yc.password().number().setValue("55318");
        yc.serverName().setValue("aspdb04");
        yc.database().setValue("afqoml_live");
        yc.platform().setValue(Platform.SQL);
        yc.ilsGuestCardServiceURL().setValue("https://www.iyardiasp.com/8223thirddev/Webservices/itfilsguestcard20.asmx");

        PhysicalProperty property = null;
        try {
            if (false) {
                YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
                property = stub.getPropertyMarketingInfo(yc, "prvista1");
            } else {
                String xml = getSampleXml();
                property = MarshallUtil.unmarshal(PhysicalProperty.class, xml);
            }
            log.info("PhysicalProperty: {}", property.getProperty().get(0).getPropertyID().getIdentification().getPrimaryID());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    private static String getSampleXml() throws IOException {
        Class<?> refClass = YardiILSServiceClientTest.class;
        String rcPath = refClass.getPackage().getName().replaceAll("\\.", "/") + "/PhysicalProperty.xml";
        InputStream is = refClass.getClassLoader().getResourceAsStream(rcPath);
        return IOUtils.toString(is);
    }
}
