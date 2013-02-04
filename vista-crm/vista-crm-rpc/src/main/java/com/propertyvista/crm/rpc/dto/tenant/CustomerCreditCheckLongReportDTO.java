/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.person.Name;

@Transient
public interface CustomerCreditCheckLongReportDTO extends IEntity {

    IPrimitive<BigDecimal> percentOfRentCovered();

    IPrimitive<BigDecimal> grossMonthlyIncome();

    IPrimitive<Integer> totalAccounts();

    IPrimitive<BigDecimal> totalOutstandingBalance();

    IPrimitive<Integer> numberOfBancruptciesOrActs();

    IPrimitive<Integer> numberOfLegalItems();

    IPrimitive<BigDecimal> outstandingCollectionsBalance();

    IPrimitive<Double> monthlyIncomeToRentRatio();

    IPrimitive<BigDecimal> estimatedDebtandRentPayments();

    IPrimitive<Integer> accountsWithNoLatePayments();

    IPrimitive<BigDecimal> outstandingRevolvingDebt();

    IPrimitive<Integer> numberOfEvictions();

    IPrimitive<Integer> landlordCollectionsFiled();

    IPrimitive<Integer> latePayments1_30days();

    IPrimitive<Integer> latePayments31_60days();

    IPrimitive<Integer> latePayments61_90days();

    IPrimitive<Double> equifaxCheckScore();

    IPrimitive<Double> equifaxRatingLevel();

    IPrimitive<Double> equifaxRiskLevel();

    IPrimitive<Integer> rating1();

    IPrimitive<Integer> rating2();

    IPrimitive<Integer> rating3();

    IPrimitive<Integer> rating4();

    IPrimitive<Integer> rating5();

    IPrimitive<Integer> rating6();

    IPrimitive<Integer> rating7();

    IPrimitive<Integer> rating8();

    IPrimitive<Integer> rating9();

    // Identity
    Name identityName();

    IPrimitive<LogicalDate> identityBirthDate();

    IPrimitive<LogicalDate> identityDeathDate();

    IPrimitive<String> identitySIN();

    IPrimitive<String> identityMarritialStatus();

    AddressSimple identityCurrentAddress();

    AddressSimple identityFormerAddress();

    IPrimitive<String> identityCurrentEmployer();

    IPrimitive<String> identityCurrentOccupation();

    IPrimitive<String> identityFormerEmployer();

    IPrimitive<String> identityFormerOccupation();

    // Accounts
    @Transient
    interface AccountDTO extends IEntity {

        IPrimitive<String> name();

        IPrimitive<String> number();

        IPrimitive<BigDecimal> creditAmount();

        IPrimitive<BigDecimal> balanceAmount();

        IPrimitive<Date> lastPaymentDate();

        IPrimitive<Integer> radeCode();

        IPrimitive<String> radeType();

        IPrimitive<String> paymentRate();

        IPrimitive<String> paymentType();
    }

    IList<AccountDTO> accounts();

    // Court Judgments
    @Transient
    interface JudgementDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        Name personName();

        IPrimitive<String> status();

        IPrimitive<Date> dateFiled();

        IPrimitive<Date> dateSatisfied();

        Name plaintiff();

        IList<Name> defendants();
    }

    IList<JudgementDTO> judgements();

    // Proposals and Bankruptcies
    @Transient
    interface ProposalDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        Name personName();

        IPrimitive<Date> dispositionDate();

        IPrimitive<BigDecimal> liabilityAmount();

        IPrimitive<BigDecimal> assetAmount();

        IPrimitive<String> caseNumberAndTrustee();

        IPrimitive<String> intentOrDisposition();
    }

    IList<ProposalDTO> proposals();

    // Evictions
    @Transient
    interface EvictionDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        Name personName();

        IPrimitive<Date> dateFiled();

        IPrimitive<Date> judgementDate();

        IPrimitive<String> judgment();

        Name plaintiff();

        IList<Name> defendants();

        AddressSimple address();
    }

    IList<EvictionDTO> evictions();

    // Rent History
    @Transient
    interface RentDTO extends IEntity {

        Name landlord();

        IPrimitive<BigDecimal> rent();

        IPrimitive<String> writeOffs();

        IPrimitive<Boolean> noticeGiven();

        IPrimitive<BigDecimal> latePayments();

        IPrimitive<BigDecimal> NSFChecks();

        IPrimitive<Date> lastUpdated();

        IPrimitive<LogicalDate> from();

        IPrimitive<LogicalDate> to();

        IPrimitive<LogicalDate> duration();

        AddressSimple address();

        IPrimitive<String> historyTable(); // ??? what is this
    }

    IList<RentDTO> rents();

    // Collections
    @Transient
    interface CollectionDTO extends IEntity {

        IPrimitive<String> onBehalf();

        IPrimitive<LogicalDate> date();

        IPrimitive<LogicalDate> lastActive();

        IPrimitive<BigDecimal> originalAmount();

        IPrimitive<BigDecimal> balance();

        IPrimitive<String> status();

        AddressSimple address();
    }

    IList<CollectionDTO> collections();

    // Inquiries
    @Transient
    interface InquiryDTO extends IEntity {

        Name onBehalf();

        IPrimitive<LogicalDate> date();

        IPrimitive<String> customerNumber();

        IPrimitive<String> phone();
    }

    IList<InquiryDTO> inquiries();
}
