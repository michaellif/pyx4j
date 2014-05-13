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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsNamespaceResolver;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitFinancial;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mappers.UnitsMapper;
import com.propertyvista.yardi.processors.YardiBuildingProcessor;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class XmlBeanTest {

    private final static Logger log = LoggerFactory.getLogger(XmlBeanTest.class);

    @BeforeClass
    //TODO clean init of tests without persistence
    public static void init() {
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
        VistaTestDBSetup.init();

        createPmc();

        // mock countries and provinces
        Country country = EntityFactory.create(Country.class);
        country.name().setValue("United States");
        Persistence.service().persist(country);

        Province province = EntityFactory.create(Province.class);
        province.code().setValue("CA");
        province.country().set(country);
        Persistence.service().persist(province);
    }

    public static synchronized void createPmc() {
        final Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.namespace().setValue(VistaTestsNamespaceResolver.demoNamespace);
        pmc.features().countryOfOperation().setValue(CountryOfOperation.US);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().persist(pmc);
                return null;
            }
        });
    }

    @Test
    public void testGetPropertyConfigurations() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetPropertyConfigurations.xml", getClass()));
        Properties properties = MarshallUtil.unmarshal(Properties.class, xml);

        log.info("Loaded {} properties", properties.getProperties().size());

        Assert.assertTrue("Has properties", !properties.getProperties().isEmpty());

        for (com.propertyvista.yardi.beans.Property property : properties.getProperties()) {
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

        log.info("Loaded transactions:\n{}", transactions);

        YardiBuildingProcessor buildingProcessor = new YardiBuildingProcessor();
        List<Property> properties = YardiResidentTransactionsService.getProperties(transactions);
        for (Property property : properties) {
            Building building = buildingProcessor.getBuilding(property.getPropertyID().get(0));
            Assert.assertNotNull("Has buildings", building);

            Assert.assertFalse(building.propertyCode().isNull());
            Assert.assertFalse(building.marketing().isNull());
            Assert.assertFalse(building.marketing().name().isNull());

            Assert.assertFalse(building.info().address().isNull());
            Assert.assertFalse(building.info().address().streetName().isNull());
            Assert.assertFalse(building.info().address().streetNumber().isNull());
            Assert.assertFalse(building.info().address().city().isNull());
            Assert.assertFalse(building.info().address().postalCode().isNull());

            List<AptUnit> units = new ArrayList<AptUnit>();

            for (RTCustomer rtCustomer : property.getRTCustomer()) {
                units.add(new UnitsMapper().map(rtCustomer.getRTUnit().getUnit()));
            }

            Assert.assertTrue("Has units", !units.isEmpty());

            for (AptUnit aptUnit : units) {
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

                // financial
                AptUnitFinancial financial = aptUnit.financial();
                Assert.assertFalse(financial._marketRent().isNull());
            }
        }

    }
}
