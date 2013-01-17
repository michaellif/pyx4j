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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitFinancial;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.services.YardiGetResidentTransactionsService;

public class XmlBeanTest {

    private final static Logger log = LoggerFactory.getLogger(XmlBeanTest.class);

    @Test
    public void testGetPropertyConfigurations() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetPropertyConfigurations.xml", getClass()));
        Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

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

    }

    @Test
    public void testGetResidentTransactions() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetResidentTransactions.xml", getClass()));

        ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
        YardiGetResidentTransactionsService residentTransactionsService = new YardiGetResidentTransactionsService();

        log.info("Loaded transactions:\n{}", transactions);

        List<Building> buildings = residentTransactionsService.getBuildings(Arrays.asList(transactions));
        Assert.assertTrue("Has buildings", !buildings.isEmpty());

        for (Building building : buildings) {
            Assert.assertFalse(building.propertyCode().isNull());
            Assert.assertFalse(building.marketing().isNull());
            Assert.assertFalse(building.marketing().name().isNull());

            Assert.assertFalse(building.info().address().isNull());
            Assert.assertFalse(building.info().address().streetName().isNull());
            Assert.assertFalse(building.info().address().streetNumber().isNull());
            Assert.assertFalse(building.info().address().city().isNull());
            Assert.assertFalse(building.info().address().postalCode().isNull());
        }

        Map<String, List<AptUnit>> units = residentTransactionsService.getUnits(Arrays.asList(transactions));
        for (Map.Entry<String, List<AptUnit>> entry : units.entrySet()) {
            List<AptUnit> entryUnits = entry.getValue();
            Assert.assertTrue("Has units", !entryUnits.isEmpty());

            for (AptUnit aptUnit : entryUnits) {
                log.debug("Unit {}", aptUnit);

                // info
                AptUnitInfo info = aptUnit.info();
                Assert.assertFalse(info.number().isNull());

                Assert.assertFalse(aptUnit.floorplan().isNull());
                Assert.assertFalse(info._bedrooms().isNull());
                Assert.assertFalse(info._bathrooms().isNull());

                Assert.assertFalse(info.area().isNull());
                Assert.assertFalse(info.areaUnits().isNull());
                Assert.assertFalse(info.economicStatus().isNull());

                // marketing
                Marketing marketing = aptUnit.marketing();
                Assert.assertFalse(marketing.name().isNull());

                // financial
                AptUnitFinancial financial = aptUnit.financial();
                Assert.assertFalse(financial._unitRent().isNull());
                Assert.assertFalse(financial._marketRent().isNull());
            }
        }
    }

    @BeforeClass
    public static void init() {
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
    }
}
