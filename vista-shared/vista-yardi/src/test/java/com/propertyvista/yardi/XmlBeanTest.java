/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.bean2.PhysicalProperty;
import com.propertyvista.yardi.bean2.ResidentTransactions;

public class XmlBeanTest {

    private final static Logger log = LoggerFactory.getLogger(XmlBeanTest.class);

    @Test
    public void testGetPropertyConfigurations() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetPropertyConfigurations.xml", getClass()));
        Properties properties = MarshallUtil.unmarshall(Properties.class, xml);

        log.debug("Loaded properties: " + properties);

        Assert.assertTrue("Has properties", !properties.getProperties().isEmpty());

        for (Property property : properties.getProperties()) {
            Assert.assertNotNull(property.getCode());
            Assert.assertNotNull(property.getAccountsPayable());
            Assert.assertNotNull(property.getAccountsReceivable());
            Assert.assertNotNull(property.getAddressLine1());
            Assert.assertNotNull(property.getAddressLine2());
            Assert.assertNotNull(property.getAddressLine3());
            Assert.assertNotNull(property.getCity());
            Assert.assertNotNull(property.getCode());
            Assert.assertNotNull(property.getPostalCode());
            Assert.assertNotNull(property.getState());
            Assert.assertNotNull(property.getMarketingName());
        }
    }

    @Test
    public void testGetUnitInformation() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetUnitInformation.xml", getClass()));
        PhysicalProperty property = MarshallUtil.unmarshall(PhysicalProperty.class, xml);

        log.debug("Loaded properties {}", property);
    }

    @Test
    public void testGetResidentTransactions() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetResidentTransactions.xml", getClass()));

        String token = "xsi:schemaLocation=\"http://yardi.com/ResidentTransactions20 .\\Itf_MITS_ResidentTransactions2.0.xsd\"";
        xml = xml.replace(token, "");

//        log.info(xml);
        ResidentTransactions transactions = MarshallUtil.unmarshall(ResidentTransactions.class, xml);

        log.info("Loaded transactions {}", transactions);
    }
//    private void validate(Object o) {
//        Method[] methods = o.getClass().getMethods();
//        for (Method method : methods) {
//            if (!method.getName().startsWith("get")) {
//                continue;
//            }
//
//            Annotation[] annotations = method.getAnnotations();
//            for (Annotation annotation : annotations) {
//                log.info("Annotation: " + annotation);
//                if (annotation.getClass() == NotNull.class) {
//                    log.info("Checking [" + method.getName() + "] has not null annotation");
//                }
//            }
//        }
//    }
}
