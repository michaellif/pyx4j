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

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.bean2.PhysicalProperty;
import com.propertyvista.yardi.bean2.ResidentTransactions;
import com.propertyvista.yardi.mapper.GetPropertyConfigurationsMapper;
import com.propertyvista.yardi.mapper.GetResidentTransactionsMapper;
import com.propertyvista.yardi.mapper.YardiXmlUtil;

public class XmlBeanTest {

    private final static Logger log = LoggerFactory.getLogger(XmlBeanTest.class);

    @Test
    public void testImportResidentTransactions() throws IOException, JAXBException {
//        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("Charge.xml", getClass()));
//        Charge charge = MarshallUtil.unmarshall(Charge.class, xml);
//        log.info("Loaded charge: {}", charge);
//        String xml2 = MarshallUtil.marshalls(charge);
//        log.info("Produced xml: {}", xml2);
    }

    @Test
    public void testGetPropertyConfigurations() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetPropertyConfigurations.xml", getClass()));
        Properties properties = MarshallUtil.unmarshall(Properties.class, xml);

        log.info("Loaded {} properties", properties.getProperties().size());

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

        // convert them into vista buildings
        GetPropertyConfigurationsMapper mapper = new GetPropertyConfigurationsMapper();
        mapper.map(properties);

        Assert.assertEquals("Converted size", properties.getProperties().size(), mapper.getBuildings().size());
        for (Building building : mapper.getBuildings()) {
            log.debug("Building {}", building);
            Assert.assertNotNull(building.info().propertyCode());
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

//        log.info(xml);
        xml = YardiXmlUtil.stripGetResidentTransactions(xml);
        ResidentTransactions transactions = MarshallUtil.unmarshall(ResidentTransactions.class, xml);

        log.debug("Loaded transactions {}", transactions);

        GetResidentTransactionsMapper mapper = new GetResidentTransactionsMapper();
        mapper.map(transactions);
    }

    @BeforeClass
    public static void init() {
        SharedData.init();
    }
}
