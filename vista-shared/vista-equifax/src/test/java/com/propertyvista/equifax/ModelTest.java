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
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.to.AccountNumberType;
import ca.equifax.uat.to.AddressesType;
import ca.equifax.uat.to.AddressesType.Address;
import ca.equifax.uat.to.CNConsAndCommRequestType;
import ca.equifax.uat.to.CNConsumerRequestType;
import ca.equifax.uat.to.CNCustomerInfoType;
import ca.equifax.uat.to.CNOutputParametersType;
import ca.equifax.uat.to.CNRequestsType;
import ca.equifax.uat.to.CNRequestsType.CNConsumerRequests;
import ca.equifax.uat.to.CNScoringProductsType;
import ca.equifax.uat.to.CNSubjectNameType;
import ca.equifax.uat.to.CityType;
import ca.equifax.uat.to.CodeType;
import ca.equifax.uat.to.CustomerInfoType;
import ca.equifax.uat.to.DateOfBirth;
import ca.equifax.uat.to.ObjectFactory;
import ca.equifax.uat.to.ParsedTelephone;
import ca.equifax.uat.to.ParsedTelephonesType;
import ca.equifax.uat.to.ScoringProductType;
import ca.equifax.uat.to.ScoringProductType.Parameters;
import ca.equifax.uat.to.SubjectsType;
import ca.equifax.uat.to.SubjectsType.Subject;

import com.propertyvista.equifax.model.ChallengerMode;
import com.propertyvista.equifax.model.EmploymentStatus;
import com.propertyvista.equifax.model.MonthlyCostsOther;
import com.propertyvista.equifax.model.MonthlyHousingCosts;
import com.propertyvista.equifax.model.MonthlyIncome;
import com.propertyvista.equifax.model.PresentPosition;
import com.propertyvista.equifax.model.ResidentialStatus;
import com.propertyvista.equifax.model.StrategyNumber;
import com.propertyvista.equifax.model.TimeAtPresentAddress;
import com.propertyvista.equifax.model.TimeAtPresentEmployer;
import com.propertyvista.equifax.request.XmlCreator;

public class ModelTest {

    private final static Logger log = LoggerFactory.getLogger(ModelTest.class);

    /**
     * 
     * @throws JAXBException
     */
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

        // subjects
        SubjectsType subjects = factory.createSubjectsType();
        consumerRequest.setSubjects(subjects);

        // subject
        Subject subject = factory.createSubjectsTypeSubject();
        subjects.getSubject().add(subject);
        subject.setSubjectType("SUBJ");

        // subject name
        CNSubjectNameType subjectName = factory.createCNSubjectNameType();
        subject.setSubjectName(subjectName);
        subjectName.setFirstName("Sherlock");
        subjectName.setMiddleName("P");
        subjectName.setLastName("Holmes");

        // sin
        subject.setSocialInsuranceNumber(new BigInteger("123456789"));

        // date of birth
        DateOfBirth dob = factory.createDateOfBirth();
        dob.setValue("1967-08-13");
        subject.setDateOfBirth(dob);

        // subject fields
        subject.setOccupation("Programmer");
        subject.setEmployer("IBM");

        // account number
        AccountNumberType accountNumber = factory.createAccountNumberType();
        accountNumber.setMnemonic("abc");
        accountNumber.setValue("1277792");
        subject.setAccountNumber(accountNumber);

        // parsed telephones
        ParsedTelephonesType telephones = factory.createParsedTelephonesType();
        subject.setParsedTelephones(telephones);

        // parsed telephone
        ParsedTelephone telephone = factory.createParsedTelephone();
        telephones.getParsedTelephone().add(telephone);
        telephone.setAreaCode((short) 416);
        telephone.setExtension((short) 555);
        telephone.setNumber("2332");
        telephone.setTelephoneType("home");

        // addresses
        AddressesType addresses = factory.createAddressesType();
        subjects.setAddresses(addresses);

        // address
        Address address = factory.createAddressesTypeAddress();
        addresses.getAddress().add(address);
        address.setAddressType("CURR");
        address.setCivicNumber("55");
        address.setStreetName("Rose Valley St");
        address.setSuite("221b");
        CityType city = factory.createCityType();
        city.setCode("YYZ");
        city.setValue("Toronto");
        address.setCity(city);
        CodeType province = factory.createCodeType();
        province.setCode("ON");
        province.setDescription("Ontario");
        address.setProvince(province);
        address.setPostalCode("L1C 9H5");

        // customer info
        consumerRequest.setCustomerInfo(customerInfo);

        // customer reference number
        consumerRequest.setCustomerReferenceNumber("ABCDEFG");
        consumerRequest.setECOAInquiryType("ABC");
        consumerRequest.setJointAccessIndicator("JOINTABC");
        consumerRequest.setProfileIndicator("Q"); // indicates iDecision request

        // output parameters
        CNOutputParametersType outputParameters = factory.createCNOutputParametersType();
        requests.setCNOutputParameters(outputParameters);

        // language
        outputParameters.setLanguage("EN");
        outputParameters.getOutputParameter().add(XmlCreator.createOutputParameter());

        // scoring
        CNScoringProductsType scoringProducts = factory.createCNScoringProductsType();
        requests.setCNScoringProducts(scoringProducts);

        // scoring product
        ScoringProductType scoringProduct = XmlCreator.createScoringProduct();
        scoringProducts.getScoringProduct().add(scoringProduct);

        // parameters
        Parameters parameters = factory.createScoringProductTypeParameters();
        scoringProduct.setParameters(parameters);

        // add parameters
        XmlCreator.addParameter(new StrategyNumber(1), parameters);
        XmlCreator.addParameter(ChallengerMode.N, parameters);
        XmlCreator.addParameter(EmploymentStatus.C, parameters);
        XmlCreator.addParameter(PresentPosition.C, parameters);
        XmlCreator.addParameter(new TimeAtPresentEmployer(10), parameters);
        XmlCreator.addParameter(new MonthlyIncome(2000), parameters);
        XmlCreator.addParameter(new MonthlyIncome(400), parameters);
        XmlCreator.addParameter(new MonthlyHousingCosts(1000), parameters);
        XmlCreator.addParameter(new MonthlyCostsOther(300), parameters);
        XmlCreator.addParameter(ResidentialStatus.O, parameters);
        XmlCreator.addParameter(new TimeAtPresentAddress(21), parameters);

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
