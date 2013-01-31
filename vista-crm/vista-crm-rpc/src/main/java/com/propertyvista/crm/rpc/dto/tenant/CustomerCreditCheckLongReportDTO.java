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

    IPrimitive<Integer> latePayments61_90();

    IPrimitive<Double> equifaxCheckScore();

    IPrimitive<Double> equifaxRatingLevel();

    IPrimitive<Double> equifaxRiskLevel();

    IPrimitive<Integer> rating();

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

    IPrimitive<String> accountsName();

    IPrimitive<String> accountsNumber();

    IPrimitive<BigDecimal> accountsCreditAmount();

    IPrimitive<BigDecimal> accountsBalanceAmount();

    IPrimitive<Date> accountsLastPaymentDate();

    IPrimitive<Integer> accountsCNTRadeCode();

    IPrimitive<String> accountsCNTRadeType();

    IPrimitive<String> accountsPaymentRate();

    IPrimitive<String> accountsPaymentType();

    // Court Judgments

    IPrimitive<String> judgmentCaseNumber();

    IPrimitive<String> judgmentCustomerNumber();

    Name judgmentPersonName();

    IPrimitive<String> judgmentStatus();

    IPrimitive<Date> judgmentDateFiled();

    IPrimitive<Date> judgmentDateSatisfied();

    Name judgmentPlaintiff();

    IList<Name> judgmentDefendants();

    // Proposals and Bankruptcies

    IPrimitive<String> proposalCaseNumber();

    IPrimitive<String> proposalCustomerNumber();

    Name proposalPersonName();

    IPrimitive<Date> proposalDispositionDate();

    IPrimitive<BigDecimal> proposalLiabilityAmount();

    IPrimitive<BigDecimal> proposalAssetAmount();

    IPrimitive<String> proposalCaseNumberAndTrustee();

    IPrimitive<String> proposalIntentOrDisposition();

    // Evictions

    IPrimitive<String> evictionCaseNumber();

    IPrimitive<String> evictionCustomerNumber();

    Name evictionPersonName();

    IPrimitive<Date> evictionDateFiled();

    IPrimitive<Date> evictionJudgementDate();

    IPrimitive<String> evictionJudgment();

    Name evictionPlaintiff();

    IList<Name> evictionDefendants();

    AddressSimple evictionAddress();

    // Rent History

    Name rentLandlord();

    IPrimitive<BigDecimal> rent();

    IPrimitive<String> rentWriteOffs();

    IPrimitive<Boolean> rentNoticeGiven();

    IPrimitive<BigDecimal> rentLatePayments();

    IPrimitive<BigDecimal> rentNSFChecks();

    IPrimitive<Date> rentLastUpdated();

    IPrimitive<LogicalDate> rentFrom();

    IPrimitive<LogicalDate> rentTo();

    IPrimitive<LogicalDate> rentDuration();

    AddressSimple rentAddress();

    IPrimitive<String> rentHistoryTable(); // ??? what is this

    // Collections

    IPrimitive<String> collectionsOnBehalf();

    IPrimitive<LogicalDate> collectionsDate();

    IPrimitive<LogicalDate> collectionsLastActive();

    IPrimitive<BigDecimal> collectionsOriginalAmount();

    IPrimitive<BigDecimal> collectionsBalance();

    IPrimitive<String> collectionsStatus();

    AddressSimple collectionsAdress();

    // Inquiries

    Name inquiriesOnBehalf();

    IPrimitive<LogicalDate> inquiriesDate();

    IPrimitive<String> inquiriesCustomer();

    IPrimitive<String> inquiriesPhone();
}
