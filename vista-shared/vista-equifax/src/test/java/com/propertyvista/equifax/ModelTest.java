/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.to.CNConsAndCommRequestType;
import ca.equifax.uat.to.CNConsumerRequestType;
import ca.equifax.uat.to.CNCustomerInfoType;
import ca.equifax.uat.to.CNRequestsType;
import ca.equifax.uat.to.CNRequestsType.CNConsumerRequests;
import ca.equifax.uat.to.CustomerInfoType;
import ca.equifax.uat.to.ObjectFactory;
import ca.equifax.uat.to.SubjectsType;

public class ModelTest {

    private final static Logger log = LoggerFactory.getLogger(ModelTest.class);

    @Test
    public void testTransmit() throws JAXBException {
        ObjectFactory factory = new ObjectFactory();
        CNConsAndCommRequestType transmit = factory.createCNConsAndCommRequestType();

        // cn customer info
        CNCustomerInfoType cnCustomerInfo = factory.createCNCustomerInfoType();
        transmit.setCNCustomerInfo(cnCustomerInfo);

        cnCustomerInfo.setCustomerCode("ABCD");
        cnCustomerInfo.setCustomerId("0001");

        // customer info
        CustomerInfoType customerInfo = factory.createCustomerInfoType();
        cnCustomerInfo.setCustomerInfo(customerInfo);

        customerInfo.setCustomerNumber("112233");
        customerInfo.setSecurityCode("SEC001");

        // requests
        CNRequestsType requests = factory.createCNRequestsType();
        transmit.setCNRequests(requests);

        // consumer requests
        CNConsumerRequests consumerRequests = factory.createCNRequestsTypeCNConsumerRequests();
        requests.setCNConsumerRequests(consumerRequests);

        // consumer request
        CNConsumerRequestType consumerRequest = factory.createCNConsumerRequestType();
        consumerRequests.getCNConsumerRequest().add(consumerRequest);

        // subject
        SubjectsType subjects = factory.createSubjectsType();
        consumerRequest.setSubjects(subjects);

        // marshalling
        QName qname = new QName("http://www.equifax.ca/XMLSchemas/CustToEfx", "CNCustTransmitToEfx");
        JAXBElement<CNConsAndCommRequestType> element = new JAXBElement<CNConsAndCommRequestType>(qname, CNConsAndCommRequestType.class, transmit);

        JAXBContext context = JAXBContext.newInstance(CNConsAndCommRequestType.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(element, sw);
        String xml = sw.toString();
        log.info("\n" + xml);
    }
}
