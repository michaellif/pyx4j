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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.equifax.model.ChallengerMode;
import com.propertyvista.equifax.model.EmploymentStatus;
import com.propertyvista.equifax.model.MonthlyHousingCosts;
import com.propertyvista.equifax.model.MonthlyIncome;
import com.propertyvista.equifax.model.ResidentialStatus;
import com.propertyvista.equifax.model.StrategyNumber;
import com.propertyvista.equifax.model.TimeAtPresentAddress;
import com.propertyvista.equifax.model.TimeAtPresentEmployer;
import com.propertyvista.misc.BusinessRules;

public class EquifaxModelMapper {

    private final static Logger log = LoggerFactory.getLogger(EquifaxModelMapper.class);

    public static CNConsAndCommRequestType createRequest(PmcEquifaxInfo equifaxInfo, Customer customer, CustomerCreditCheck pcc, int strategyNumber) {

        ObjectFactory factory = new ObjectFactory();

        CNConsAndCommRequestType transmit = factory.createCNConsAndCommRequestType();

        // cn customer info
        CNCustomerInfoType cnCustomerInfo = factory.createCNCustomerInfoType();
        transmit.setCNCustomerInfo(cnCustomerInfo);

        if (equifaxInfo.customerCode().isNull()) {
            cnCustomerInfo.setCustomerCode("P028");
            cnCustomerInfo.setCustomerId("vista");
        } else {
            cnCustomerInfo.setCustomerCode(equifaxInfo.customerCode().getValue());
            cnCustomerInfo.setCustomerId(equifaxInfo.customerReferenceNumber().getValue());
        }

        CustomerInfoType customerInfo = factory.createCustomerInfoType();
        cnCustomerInfo.setCustomerInfo(customerInfo);

        customerInfo.setCustomerNumber(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(equifaxInfo.memberNumber()));
        customerInfo.setSecurityCode(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(equifaxInfo.securityCode()));

        // requests
        CNRequestsType requests = factory.createCNRequestsType();
        transmit.setCNRequests(requests);
        // customer info  FMTLDECGEN0000115

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
        subjectName.setMiddleName(customer.person().name().middleName().getValue());

        // addresses
        AddressesType addresses = factory.createAddressesType();
        subjects.setAddresses(addresses);

        // address
        {
            Address efxAddress = factory.createAddressesTypeAddress();
            addresses.getAddress().add(efxAddress);
            efxAddress.setAddressType("CURR");

            PriorAddress currentAddress = pcc.screening().version().currentAddress();

            toEtxAddress(factory, currentAddress, efxAddress);
        }
        // previousAddress {
        {
            PriorAddress previousAddress = pcc.screening().version().previousAddress();
            if (!previousAddress.isEmpty() && BusinessRules.infoPageNeedPreviousAddress(pcc.screening().version().currentAddress().moveInDate().getValue())) {
                Address efxAddress = factory.createAddressesTypeAddress();
                addresses.getAddress().add(efxAddress);
                efxAddress.setAddressType("FORM");
                toEtxAddress(factory, previousAddress, efxAddress);
            }
        }

        if (!customer.person().birthDate().isNull()) {
            // date of birth
            DateOfBirth dob = factory.createDateOfBirth();
            dob.setValue(new SimpleDateFormat("YYYY-MM-dd").format(customer.person().birthDate().getValue()));
            subject.setDateOfBirth(dob);
        }

        if (!customer.person().homePhone().isNull()) {
            toEfxPhone(factory, subject, "RES", customer.person().homePhone().getStringView());
        }
        if (!customer.person().mobilePhone().isNull()) {
            toEfxPhone(factory, subject, "MOB", customer.person().mobilePhone().getStringView());
        }
        if (!customer.person().workPhone().isNull()) {
            toEfxPhone(factory, subject, "BUS", customer.person().workPhone().getStringView());
        }

        // sin
        for (IdentificationDocumentFolder document : pcc.screening().version().documents()) {
            if (document.donotHave().getValue(false)) {
                continue;
            }
            if ((document.idType().type().getValue() == IdentificationDocumentType.Type.canadianSIN) && (!document.idNumber().isNull())) {
                subject.setSocialInsuranceNumber(new BigInteger(document.idNumber().getValue().replaceAll("[\\s-]+", "")));
                break;
            }
        }

        // May add this in future
        if (false) {
            // account number
            AccountNumberType accountNumber = factory.createAccountNumberType();
            accountNumber.setMnemonic("abc");
            accountNumber.setValue("1277792");
            subject.setAccountNumber(accountNumber);
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
        CustomerScreeningIncome presentPersonalIncome = null;
        for (CustomerScreeningIncome personalIncome : pcc.screening().version().incomes()) {
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

            // subject fields
            if (presentPersonalIncome.details().isInstanceOf(IEmploymentInfo.class)) {
                IEmploymentInfo personalIncomeDetails = presentPersonalIncome.details().cast();
                subject.setOccupation(personalIncomeDetails.position().getStringView());
                subject.setEmployer(personalIncomeDetails.name().getStringView());
            }
        } else {
            XmlCreator.addParameter(EmploymentStatus.NotAsked, parameters);
        }

        XmlCreator.addParameter(new MonthlyHousingCosts(pcc.amountChecked().getValue().intValue()), parameters);
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
            XmlCreator.addParameter(ResidentialStatus.NotGiven, parameters);
        }

        return transmit;
    }

    private static void toEfxPhone(ObjectFactory factory, Subject subject, String telephoneType, String telephone) {
        // parsed telephones
        ParsedTelephonesType telephones = subject.getParsedTelephones();
        if (telephones == null) {
            telephones = factory.createParsedTelephonesType();
            subject.setParsedTelephones(telephones);
        }

        // parsed telephone
        ParsedTelephone efxTelephone = factory.createParsedTelephone();
        efxTelephone.setTelephoneType(telephoneType);

        String unformatedPhone = telephone.replaceAll("[\\s\\(\\)-]+", "");

        try {
            if (unformatedPhone.length() == 10) {
                efxTelephone.setAreaCode(Short.valueOf(unformatedPhone.substring(0, 3)));
                efxTelephone.setNumber(unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10));
            } else if (unformatedPhone.length() > 10) {
                efxTelephone.setAreaCode(Short.valueOf(unformatedPhone.substring(0, 3)));
                efxTelephone.setNumber(unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10));
                efxTelephone.setAreaCode(Short.valueOf(unformatedPhone.substring(10, unformatedPhone.length())));
            } else {
                efxTelephone.setNumber(telephone);
            }
        } catch (NumberFormatException notANumber) {
            efxTelephone.setNumber(telephone);
        }

        telephones.getParsedTelephone().add(efxTelephone);
    }

    private static void toEtxAddress(ObjectFactory factory, PriorAddress vistaAddress, Address efxAddress) {
        efxAddress.setCivicNumber(vistaAddress.streetNumber().getValue());
        efxAddress.setStreetName(vistaAddress.streetName().getValue());
        efxAddress.setSuite(vistaAddress.suiteNumber().getValue());
        CityType city = factory.createCityType();
        city.setValue(vistaAddress.city().getValue());
        efxAddress.setCity(city);
        CodeType province = factory.createCodeType();
        province.setCode(vistaAddress.province().code().getValue());
        //TODO Need to know Equifax point of view on this
        //province.setDescription(vistaAddress.province().name().getValue());
        efxAddress.setProvince(province);
        efxAddress.setPostalCode(efxPostalCodeFormat(vistaAddress.postalCode().getValue()));
    }

    private static String efxPostalCodeFormat(String value) {
        if (value == null) {
            return null;
        } else {
            return value.trim().replaceAll(" ", "");
        }
    }

    private static boolean isZero(IPrimitive<BigDecimal> monthlyAmount) {
        if (monthlyAmount.isNull()) {
            return true;
        } else {
            return monthlyAmount.getValue().compareTo(BigDecimal.ZERO) != 0;
        }
    }

    private static void addMonthlyIncome(CustomerScreeningIncome personalIncome, Parameters parameters) {
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
