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

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitFinancial;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo;
import com.propertyvista.domain.property.asset.unit.AptUnitMarketing;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.bean.mits.Customer;
import com.propertyvista.yardi.bean.mits.Customers;
import com.propertyvista.yardi.bean.mits.Identification;
import com.propertyvista.yardi.bean.mits.PropertyId;
import com.propertyvista.yardi.bean.resident.PhysicalProperty;
import com.propertyvista.yardi.bean.resident.RTCustomer;
import com.propertyvista.yardi.bean.resident.ResidentTransactions;
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

        // convert them into vista buildings
        GetPropertyConfigurationsMapper mapper = new GetPropertyConfigurationsMapper();
        mapper.map(properties);

        Assert.assertEquals("Converted size", properties.getProperties().size(), mapper.getBuildings().size());
        for (Building building : mapper.getBuildings()) {
            log.debug("Building {}", building);
            Assert.assertNotNull(building.propertyCode());
        }
    }

    @Test
    public void testGetUnitInformation() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetUnitInformation.xml", getClass()));
        xml = YardiXmlUtil.stripGetUnitInformation(xml);
        PhysicalProperty property = MarshallUtil.unmarshal(PhysicalProperty.class, xml);

        log.info("Loaded properties {}", property);
    }

    @Test
    public void testGenerateGetResidentTransactions() throws IOException, JAXBException {
        ResidentTransactions transactions = new ResidentTransactions();
        com.propertyvista.yardi.bean.resident.Property property = new com.propertyvista.yardi.bean.resident.Property();
        transactions.getProperties().add(property);

        // property
        PropertyId propertyId = new PropertyId();
        property.setPropertyId(propertyId);

        Identification identification = new Identification();
        identification.setType("other");
        identification.setMarketingName("Vasya");
        identification.setPrimaryId("anya");
        propertyId.setIdentification(identification);

        // rt customer
        RTCustomer rtCustomer = new RTCustomer();
        property.getCustomers().add(rtCustomer);

        Customers customers = new Customers();
        rtCustomer.setCustomers(customers);

        Customer customer = new Customer();
        customer.setDescription("277");
        customer.setCustomerId("0007");
        customer.setType("future_resident");
        customers.getCustomers().add(customer);

//        JAXBContext context = JAXBContext.newInstance(data.getClass());
//        Marshaller m = context.createMarshaller();
//        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        NamespaceP
//        NamespacePrefixMapper prefixMapper = new MyPrefixMapperImpl();
//        m.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
//
//        StringWriter sw = new StringWriter();
//        m.marshal(transactions, sw);
//        String xml = sw.toString();

        String xml = MarshallUtil.marshall(transactions);
        log.info(xml);
    }

    @Test
    public void testGetResidentTransactions() throws IOException, JAXBException {
        String xml = IOUtils.getTextResource(IOUtils.resourceFileName("GetResidentTransactions.xml", getClass()));

        ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);

        log.info("Loaded transactions:\n{}", transactions);

        GetResidentTransactionsMapper mapper = new GetResidentTransactionsMapper();
        mapper.map(transactions);

        Assert.assertTrue("Has units", !mapper.getModel().getAptUnits().isEmpty());
        for (AptUnit unit : mapper.getModel().getAptUnits()) {
            log.debug("Unit {}", unit);

            // info
            AptUnitInfo info = unit.info();
            Assert.assertFalse(info.name().isNull());
            Assert.assertFalse(info.number().isNull());
            Assert.assertFalse(info.type().isNull());
            Assert.assertFalse(info.bedrooms().isNull());
            Assert.assertFalse(info.bathrooms().isNull());
            Assert.assertFalse(info.area().isNull());
            Assert.assertFalse(info.areaUnits().isNull());
            Assert.assertFalse(info.economicStatus().isNull());

            // marketing
            AptUnitMarketing marketing = unit.marketing();
            Assert.assertFalse(marketing.name().isNull());

            // financial
            AptUnitFinancial financial = unit.financial();
            Assert.assertFalse(financial.unitRent().isNull());
            Assert.assertFalse(financial.marketRent().isNull());
        }

        Assert.assertTrue("Has tenants", !mapper.getModel().getTenants().isEmpty());
        for (Tenant tenant : mapper.getModel().getTenants()) {
            log.info("Tenant {}", tenant);

            // name
            Name name = tenant.person().name();
            Assert.assertFalse(name.firstName().isNull());
            Assert.assertFalse(name.lastName().isNull());
        }
    }

    @BeforeClass
    public static void init() {
        SharedData.init();
    }
}
