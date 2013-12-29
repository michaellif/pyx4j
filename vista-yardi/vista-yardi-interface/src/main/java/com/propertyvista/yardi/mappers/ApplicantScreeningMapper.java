/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mappers;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Addressinfo;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.screening.Applicant;
import com.yardi.entity.screening.Income;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IncomeSource;

public class ApplicantScreeningMapper {

    public Customer map(Applicant applicant) throws YardiServiceException {

        Customer customer = EntityFactory.create(Customer.class);

        YardiCustomer yCustomer = applicant.getCustomers().getCustomer().get(0);

        customer.person().name().firstName().setValue(yCustomer.getName().getFirstName());
        customer.person().name().lastName().setValue(yCustomer.getName().getLastName());
        customer.person().name().middleName().setValue(yCustomer.getName().getMiddleName());

        if (applicant.getASInformation().getBirthdate() != null) {
            customer.person().birthDate().setValue(new LogicalDate(applicant.getASInformation().getBirthdate()));
        }

        CustomerCreditCheck creditCheck = EntityFactory.create(CustomerCreditCheck.class);
        customer.personScreening().creditChecks().add(creditCheck);

        AddressMapper addrMapper = new AddressMapper();
        for (Address address : yCustomer.getAddress()) {
            if (address.getType() == Addressinfo.CURRENT) {
                creditCheck.screening().version().currentAddress().set(addrMapper.map(address));
            }
            if (address.getType() == Addressinfo.PREVIOUS) {
                creditCheck.screening().version().previousAddress().set(addrMapper.map(address));
            }
        }

        //sin
        String sin = applicant.getASInformation().getSocSecNumber();

        IdentificationDocumentFolder document = EntityFactory.create(IdentificationDocumentFolder.class);
        creditCheck.screening().version().documents().add(document);

        document.idType().type().setValue(Type.canadianSIN);
        document.idNumber().setValue(sin);

        //income
        if (applicant.getIncome() != null) {

            CustomerScreeningIncome presentIncome = EntityFactory.create(CustomerScreeningIncome.class);
            creditCheck.screening().version().incomes().add(presentIncome);

            Date startDate = applicant.getIncome().getEmploymentStartDate();
            if (startDate != null) {
                presentIncome.details().starts().setValue(new LogicalDate(startDate));
            }
            Date endDate = applicant.getIncome().getEmploymentEndDate();
            if (endDate != null) {
                presentIncome.details().ends().setValue(new LogicalDate(endDate));
            }

            presentIncome.incomeSource().setValue(getIncomeSource(applicant.getIncome()));
        }

        //amount to check
        if (applicant.getOther() != null && applicant.getOther().getCurrentRent() != null) {
            creditCheck.amountChecked().setValue(applicant.getOther().getCurrentRent());
        }

        return customer;
    }

    private IncomeSource getIncomeSource(Income income) {
        IncomeSource incomeSource = IncomeSource.other;
        if (StringUtils.equalsIgnoreCase("employed", income.getEmploymentStatus())) {
            incomeSource = IncomeSource.fulltime;
        } else if (StringUtils.equalsIgnoreCase("selfemployed", income.getEmploymentStatus())) {
            incomeSource = IncomeSource.selfemployed;
        } else if (StringUtils.equalsIgnoreCase("unemployed", income.getEmploymentStatus())) {
            incomeSource = IncomeSource.unemployed;
        } else if (StringUtils.equalsIgnoreCase("retired", income.getEmploymentStatus())) {
            incomeSource = IncomeSource.retired;
        } else if (StringUtils.equalsIgnoreCase("student", income.getEmploymentStatus())) {
            incomeSource = IncomeSource.student;
        }
        return incomeSource;
    }
}
