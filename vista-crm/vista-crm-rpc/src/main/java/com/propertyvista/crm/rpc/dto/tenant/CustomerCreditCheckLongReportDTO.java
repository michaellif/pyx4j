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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.person.Name;

@Transient
public interface CustomerCreditCheckLongReportDTO extends IEntity {

    @Caption(name = "% Rent Covered")
    IPrimitive<BigDecimal> percentOfRentCovered();

    IPrimitive<BigDecimal> grossMonthlyIncome();

    IPrimitive<Integer> totalAccounts();

    IPrimitive<BigDecimal> totalOutstandingBalance();

    @Caption(name = "Bankruptcies/Proposals")
    IPrimitive<Integer> numberOfBancruptciesOrActs();

    @Caption(name = "Landlord/Tenant Court")
    IPrimitive<Integer> numberOfLegalItems();

    IPrimitive<BigDecimal> outstandingCollectionsBalance();

    IPrimitive<Double> monthlyIncomeToRentRatio();

    IPrimitive<BigDecimal> estimatedDebtandRentPayments();

    IPrimitive<Integer> accountsWithNoLatePayments();

    IPrimitive<BigDecimal> outstandingRevolvingDebt();

    IPrimitive<Integer> numberOfEvictions();

    IPrimitive<Integer> landlordCollectionsFiled();

    @Caption(name = "1-30 days")
    IPrimitive<Integer> latePayments1_30days();

    @Caption(name = "31-60 days")
    IPrimitive<Integer> latePayments31_60days();

    @Caption(name = "61-90 days")
    IPrimitive<Integer> latePayments61_90days();

    @Caption(name = "Check Score")
    IPrimitive<Double> equifaxCheckScore();

    @Caption(name = "Rating Level")
    IPrimitive<Double> equifaxRatingLevel();

    @Caption(name = "Risk Level")
    IPrimitive<Double> equifaxRiskLevel();

    @Caption(name = "R1")
    IPrimitive<Integer> rating1();

    @Caption(name = "R2")
    IPrimitive<Integer> rating2();

    @Caption(name = "R3")
    IPrimitive<Integer> rating3();

    @Caption(name = "R4")
    IPrimitive<Integer> rating4();

    @Caption(name = "R5")
    IPrimitive<Integer> rating5();

    @Caption(name = "R6")
    IPrimitive<Integer> rating6();

    @Caption(name = "R7")
    IPrimitive<Integer> rating7();

    @Caption(name = "R9")
    IPrimitive<Integer> rating8();

    @Caption(name = "R9")
    IPrimitive<Integer> rating9();

    @Transient
    interface IdentityDTO extends IEntity {

        Name name();

        IPrimitive<LogicalDate> birthDate();

        IPrimitive<LogicalDate> deathDate();

        IPrimitive<String> SIN();

        IPrimitive<String> marritialStatus();

        AddressSimple currentAddress();

        AddressSimple formerAddress();

        IPrimitive<String> currentEmployer();

        IPrimitive<String> currentOccupation();

        IPrimitive<String> formerEmployer();

        IPrimitive<String> formerOccupation();
    }

    @EmbeddedEntity
    IdentityDTO identity();

    // Accounts
    @Transient
    interface AccountDTO extends IEntity {

        IPrimitive<String> name();

        IPrimitive<String> number();

        IPrimitive<BigDecimal> creditAmount();

        IPrimitive<BigDecimal> balanceAmount();

        IPrimitive<LogicalDate> lastPaymentDate();

        IPrimitive<String> portfolioTypeCode();

        IPrimitive<String> paymentRateCode();

        IPrimitive<String> portfolioTypeDescription();

        IPrimitive<String> paymentRate();

        IPrimitive<String> paymentType();
    }

    IList<AccountDTO> accounts();

    // Court Judgments
    @Transient
    interface JudgementDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        IPrimitive<String> personName();

        IPrimitive<String> status();

        IPrimitive<LogicalDate> dateFiled();

        IPrimitive<LogicalDate> dateSatisfied();

        IPrimitive<String> plaintiff();

        IPrimitive<String> defendants();
    }

    IList<JudgementDTO> judgements();

    // Proposals and Bankruptcies
    @Transient
    interface ProposalDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        IPrimitive<String> personName();

        IPrimitive<LogicalDate> dispositionDate();

        IPrimitive<BigDecimal> liabilityAmount();

        IPrimitive<BigDecimal> assetAmount();

        IPrimitive<String> caseNumberAndTrustee();

        IPrimitive<String> intentOrDisposition();
    }

    IList<ProposalDTO> proposals();

    // Evictions
    // Not present in current version of Equifax
    @Transient
    interface EvictionDTO extends IEntity {

        IPrimitive<String> caseNumber();

        IPrimitive<String> customerNumber();

        Name personName();

        IPrimitive<LogicalDate> dateFiled();

        IPrimitive<LogicalDate> judgementDate();

        IPrimitive<String> judgment();

        Name plaintiff();

        IList<Name> defendants();

        AddressSimple address();
    }

    IList<EvictionDTO> evictions();

    // Rent History
    // Not present in current version of Equifax
    @Transient
    interface RentDTO extends IEntity {

        Name landlord();

        IPrimitive<BigDecimal> rent();

        IPrimitive<String> writeOffs();

        IPrimitive<Boolean> noticeGiven();

        IPrimitive<BigDecimal> latePayments();

        IPrimitive<BigDecimal> NSFChecks();

        IPrimitive<LogicalDate> lastUpdated();

        IPrimitive<LogicalDate> from();

        IPrimitive<LogicalDate> to();

        IPrimitive<LogicalDate> duration();

        AddressSimple address();

        IPrimitive<String> historyTable();
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

        IPrimitive<String> onBehalf();

        IPrimitive<LogicalDate> date();

        IPrimitive<String> customerNumber();

        IPrimitive<String> phone();
    }

    IList<InquiryDTO> inquiries();
}
