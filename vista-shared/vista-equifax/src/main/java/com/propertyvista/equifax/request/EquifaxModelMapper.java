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
package com.propertyvista.equifax.request;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.equifax.model.ChallengerMode;
import com.propertyvista.equifax.model.EmploymentStatus;
import com.propertyvista.equifax.model.MonthlyHousingCosts;
import com.propertyvista.equifax.model.MonthlyIncome;
import com.propertyvista.equifax.model.ResidentialStatus;
import com.propertyvista.equifax.model.StrategyNumber;
import com.propertyvista.equifax.model.TimeAtPresentAddress;
import com.propertyvista.equifax.model.TimeAtPresentEmployer;

public class EquifaxModelMapper {

    private final static Logger log = LoggerFactory.getLogger(EquifaxModelMapper.class);

    public static CNConsAndCommRequestType createRequest(Customer customer, PersonCreditCheck pcc, int strategyNumber) {

        ObjectFactory factory = new ObjectFactory();

        CNConsAndCommRequestType transmit = factory.createCNConsAndCommRequestType();

        // cn customer info
        CNCustomerInfoType cnCustomerInfo = factory.createCNCustomerInfoType();
        transmit.setCNCustomerInfo(cnCustomerInfo);

        cnCustomerInfo.setCustomerCode("P028");
        cnCustomerInfo.setCustomerId("vista");

        CustomerInfoType customerInfo = factory.createCustomerInfoType();
        cnCustomerInfo.setCustomerInfo(customerInfo);

        customerInfo.setCustomerNumber("999RZ00012");
        customerInfo.setSecurityCode("77");

        // requests
        CNRequestsType requests = factory.createCNRequestsType();
        transmit.setCNRequests(requests);
        // customer info  FMTLDECGEN0000115
        if (true) {
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
            subjectName.setFirstName(customer.person().name().firstName().getValue());
            subjectName.setLastName(customer.person().name().lastName().getValue());

            // addresses
            AddressesType addresses = factory.createAddressesType();
            subjects.setAddresses(addresses);

            // address
            {
                Address address = factory.createAddressesTypeAddress();
                addresses.getAddress().add(address);
                address.setAddressType("CURR");

                PriorAddress currentAddress = pcc.screening().version().currentAddress();

                address.setCivicNumber(currentAddress.streetNumber().getValue());
                address.setStreetName(currentAddress.streetName().getValue());
                address.setSuite(currentAddress.suiteNumber().getValue());
                CityType city = factory.createCityType();
                city.setValue(currentAddress.city().getValue());
                address.setCity(city);
                CodeType province = factory.createCodeType();
                province.setCode(currentAddress.province().code().getValue());
                province.setDescription(currentAddress.province().name().getValue());
                address.setProvince(province);
                address.setPostalCode(currentAddress.postalCode().getValue());
            }

            if (!customer.person().birthDate().isNull()) {
                // date of birth
                DateOfBirth dob = factory.createDateOfBirth();
                dob.setValue(new SimpleDateFormat("YYYY-MM-dd").format(customer.person().birthDate().getValue()));
                subject.setDateOfBirth(dob);
            }
        }

        // customer info
        if (false) {
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
            telephone.setNumber("1234567");
            telephone.setTelephoneType("BUS");

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
        }

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
        XmlCreator.addParameter(new StrategyNumber(strategyNumber), parameters);
        XmlCreator.addParameter(ChallengerMode.N, parameters);

        //TODO
        //XmlCreator.addParameter(PresentPosition.C, parameters);

        //Find PresentEmployer and calculate Income
        PersonalIncome presentPersonalIncome = null;
        for (PersonalIncome personalIncome : pcc.screening().version().incomes()) {
            if (isCurrentDate(personalIncome.details().ends())) {
                addMonthlyIncome(personalIncome, parameters);
                if (presentPersonalIncome == null) {
                    presentPersonalIncome = personalIncome;
                }
            }
        }
        if (presentPersonalIncome != null) {
            EmploymentStatus employmentStatus = EmploymentStatus.NotAsked;
            switch (presentPersonalIncome.incomeSource().getValue()) {
            case fulltime:
                employmentStatus = EmploymentStatus.Employed;
                break;
            case parttime:
                employmentStatus = EmploymentStatus.Employed;
                break;
            case selfemployed:
                employmentStatus = EmploymentStatus.SelfEmployed;
                break;
            case seasonallyEmployed:
                employmentStatus = EmploymentStatus.Employed;
                break;
            case socialServices:
                employmentStatus = EmploymentStatus.UnemployedPlusIncome;
                break;
            case pension:
                employmentStatus = EmploymentStatus.UnemployedPlusIncome;
                break;
            case retired:
                employmentStatus = EmploymentStatus.Retired;
                break;
            case student:
                employmentStatus = EmploymentStatus.FullTimeStudent;
                break;
            case disabilitySupport:
                employmentStatus = EmploymentStatus.UnemployedPlusIncome;
                break;
            case dividends:
                employmentStatus = EmploymentStatus.UnemployedPlusIncome;
                break;
            case unemployed:
            case other:
                if (isZero(presentPersonalIncome.details().monthlyAmount())) {
                    employmentStatus = EmploymentStatus.UnemployedNoIncome;
                } else {
                    employmentStatus = EmploymentStatus.UnemployedPlusIncome;
                }
                break;
            }
            XmlCreator.addParameter(employmentStatus, parameters);
            if (!presentPersonalIncome.details().starts().isNull()) {
                XmlCreator.addParameter(new TimeAtPresentEmployer(monthSince(presentPersonalIncome.details().starts())), parameters);
            }
        } else {
            XmlCreator.addParameter(EmploymentStatus.NotAsked, parameters);
        }

        XmlCreator.addParameter(new MonthlyHousingCosts(pcc.amountCheked().getValue().intValue()), parameters);
        //XmlCreator.addParameter(new MonthlyCostsOther(300), parameters);

        if (!pcc.screening().version().currentAddress().moveInDate().isNull()) {
            XmlCreator.addParameter(new TimeAtPresentAddress(monthSince(pcc.screening().version().currentAddress().moveInDate())), parameters);
        }

        if (!pcc.screening().version().currentAddress().rented().isNull()) {
            switch (pcc.screening().version().currentAddress().rented().getValue()) {
            case owned:
                XmlCreator.addParameter(ResidentialStatus.OwnsOrBuying, parameters);
                break;
            case rented:
                XmlCreator.addParameter(ResidentialStatus.Rents, parameters);
                break;
            }
        } else {
            XmlCreator.addParameter(ResidentialStatus.NotAsked, parameters);
        }

        return transmit;
    }

    private static boolean isZero(IPrimitive<BigDecimal> monthlyAmount) {
        if (monthlyAmount.isNull()) {
            return true;
        } else {
            return monthlyAmount.getValue().compareTo(BigDecimal.ZERO) != 0;
        }
    }

    private static void addMonthlyIncome(PersonalIncome personalIncome, Parameters parameters) {
        if (!personalIncome.details().monthlyAmount().isNull()) {
            XmlCreator.addParameter(new MonthlyIncome(personalIncome.details().monthlyAmount().getValue().intValue()), parameters);
        }
    }

    private static boolean isCurrentDate(IPrimitive<LogicalDate> date) {
        if (date.isNull()) {
            return true;
        } else {
            return monthSince(date) >= 1;
        }
    }

    private static int monthSince(IPrimitive<LogicalDate> moveInDate) {
        Calendar cal = new GregorianCalendar();
        int toMonth = cal.get(Calendar.MONTH);
        int toYear = cal.get(Calendar.YEAR);
        cal.setTime(moveInDate.getValue());
        int fromMonth = cal.get(Calendar.MONTH);
        int fromYear = cal.get(Calendar.YEAR);
        return ((toYear - fromYear) * cal.getMaximum(Calendar.MONTH)) + (toMonth - fromMonth);
    }
}
