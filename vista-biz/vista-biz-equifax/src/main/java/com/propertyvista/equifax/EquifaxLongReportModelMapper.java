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
package com.propertyvista.equifax;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.from.CNAddressType;
import ca.equifax.uat.from.CNBankruptcyOrActType;
import ca.equifax.uat.from.CNCollectionType;
import ca.equifax.uat.from.CNConsumerCreditReportType;
import ca.equifax.uat.from.CNEmploymentType;
import ca.equifax.uat.from.CNHeaderType.Subject;
import ca.equifax.uat.from.CNLegalItemType;
import ca.equifax.uat.from.CNLocalInquiryType;
import ca.equifax.uat.from.CNScoreType;
import ca.equifax.uat.from.CNTradeType;
import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.from.ParsedTelephone;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.RatingLevel;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.RiskLevel;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class EquifaxLongReportModelMapper {

    private final static Logger log = LoggerFactory.getLogger(EquifaxLongReportModelMapper.class);

    public static CustomerCreditCheckLongReportDTO createLongReport(EfxTransmit efxResponse, CustomerCreditCheck ccc) {
        CustomerCreditCheckLongReportDTO dto = EntityFactory.create(CustomerCreditCheckLongReportDTO.class);

        if (efxResponse.getEfxReport() != null && efxResponse.getEfxReport().get(0).getCNConsumerCreditReports() != null) {
            CNConsumerCreditReportType report = efxResponse.getEfxReport().get(0).getCNConsumerCreditReports().getCNConsumerCreditReport().get(0);

            CNScoreType score = getCNScore(report.getCNScores().getCNScore(), "SCOR");
            if (score.getRejectCodes() != null && score.getRejectCodes().getRejectCode() != null && !score.getRejectCodes().getRejectCode().isEmpty()) {
                dto.percentOfRentCovered().setValue(getRejectCode(score.getRejectCodes().getRejectCode().get(0).getCode()));
                dto.equifaxRiskLevel().setValue(getRiskLevel(score.getRejectCodes().getRejectCode().get(0).getCode()));
            }
            if (report.getCNTrades() != null) {
                List<CNTradeType> cnTrades = report.getCNTrades().getCNTrade();
                dto.totalAccounts().setValue(cnTrades.size());
                dto.totalOutstandingBalance().setValue(getTotalCNTradesBalance(cnTrades));
                dto.accountsWithNoLatePayments().setValue(countAccounts(cnTrades, "R1"));
                dto.outstandingRevolvingDebt().setValue(getTotalRevolvingDebt(cnTrades));

                int[] lateAccounts = countLateAccounts(cnTrades);
                dto.latePayments1_30days().setValue(lateAccounts[0]);
                dto.latePayments31_60days().setValue(lateAccounts[1]);
                dto.latePayments61_90days().setValue(lateAccounts[2]);

                dto.rating1().setValue(countAccounts(cnTrades, "R1"));
                dto.rating2().setValue(countAccounts(cnTrades, "R2"));
                dto.rating3().setValue(countAccounts(cnTrades, "R3"));
                dto.rating4().setValue(countAccounts(cnTrades, "R4"));
                dto.rating5().setValue(countAccounts(cnTrades, "R5"));
                dto.rating6().setValue(countAccounts(cnTrades, "R6"));
                dto.rating7().setValue(countAccounts(cnTrades, "R7"));
                dto.rating8().setValue(countAccounts(cnTrades, "R8"));
                dto.rating9().setValue(countAccounts(cnTrades, "R9"));
            }

            dto.numberOfBancruptciesOrActs().setValue(
                    report.getCNBankruptciesOrActs() != null ? report.getCNBankruptciesOrActs().getCNBankruptcyOrAct().size() : 0);
            dto.numberOfLegalItems().setValue(report.getCNLegalItems() != null ? report.getCNLegalItems().getCNLegalItem().size() : 0);
            dto.outstandingCollectionsBalance().setValue(
                    getTotalCNCollectionsBalance(report.getCNCollections() != null ? report.getCNCollections().getCNCollection() : null));

            dto.landlordCollectionsFiled().setValue(report.getCNCollections() != null ? report.getCNCollections().getCNCollection().size() : 0);

            // TODO mapping says SCBC, error in mapping?
            dto.equifaxCheckScore().setValue(
                    getCNScore(report.getCNScores().getCNScore(), "SCBS") != null ? getScoreResult(getCNScore(report.getCNScores().getCNScore(), "SCBS"))
                            : null);
            if (dto.equifaxCheckScore().getValue() != null) {
                dto.equifaxRatingLevel().setValue(getRatingLevel(dto.equifaxCheckScore().getValue()));
            }

            // Identity
            CustomerCreditCheckLongReportDTO.IdentityDTO identity = EntityFactory.create(CustomerCreditCheckLongReportDTO.IdentityDTO.class);
            if (report.getCNHeader().getSubject() != null) {
                Subject subject = report.getCNHeader().getSubject();
                Name identityName = EntityFactory.create(Name.class);
                identityName.firstName().setValue(subject.getSubjectName().getFirstName());
                identityName.lastName().setValue(subject.getSubjectName().getLastName());
                identityName.middleName().setValue(subject.getSubjectName().getMiddleName());
                identity.name().set(identityName);
                if (subject.getSubjectId() != null) {
                    identity.birthDate().setValue(
                            getLogicalDateFromString(subject.getSubjectId().getDateOfBirth() != null ? subject.getSubjectId().getDateOfBirth().getValue()
                                    : null));
                    identity.SIN().setValue(subject.getSubjectId().getSocialInsuranceNumber());
                    identity.deathDate().setValue(
                            subject.getSubjectId().getDateOfDeath() != null ? new LogicalDate(subject.getSubjectId().getDateOfDeath().toGregorianCalendar()
                                    .getTime()) : null);
                    if (subject.getSubjectId().getMaritalStatus() != null) {
                        identity.maritalStatus().setValue(subject.getSubjectId().getMaritalStatus().getDescription());
                    }
                }

            }
            identity.currentAddress().set(createAddress(getCNAddress(report.getCNAddresses().getCNAddress(), "CA")));
            identity.formerAddress().set(createAddress(getCNAddress(report.getCNAddresses().getCNAddress(), "FA")));
            if (report.getCNEmployments() != null) {
                identity.currentEmployer().setValue(
                        getEmployer(getEmployment(report.getCNEmployments() != null ? report.getCNEmployments().getCNEmployment() : null, "ES")));
                identity.currentOccupation().setValue(getOccupation(getEmployment(report.getCNEmployments().getCNEmployment(), "ES")));
                identity.formerEmployer().setValue(getEmployer(getEmployment(report.getCNEmployments().getCNEmployment(), "EF")));
                identity.formerOccupation().setValue(getOccupation(getEmployment(report.getCNEmployments().getCNEmployment(), "EF")));
            }

            dto.identity().set(identity);

            // Accounts
            if (report.getCNTrades() != null) {
                List<CNTradeType> cnTrades = report.getCNTrades().getCNTrade();
                for (CNTradeType cnTrade : cnTrades) {
                    CustomerCreditCheckLongReportDTO.AccountDTO account = EntityFactory.create(CustomerCreditCheckLongReportDTO.AccountDTO.class);
                    account.name().setValue(cnTrade.getCreditorId().getName());
                    account.number().setValue(cnTrade.getAccountNumber().getValue());
                    account.creditAmount().setValue(cnTrade.getHighCreditAmount().getValue());
                    account.balanceAmount().setValue(cnTrade.getBalanceAmount().getValue());
                    account.lastPayment().setValue(
                            cnTrade.getDateLastActivityOrPayment() != null ? new LogicalDate(cnTrade.getDateLastActivityOrPayment().toGregorianCalendar()
                                    .getTime()) : null);
                    account.code().setValue(cnTrade.getPortfolioType().getCode() + cnTrade.getPaymentRate().getCode());
                    account.type().setValue(cnTrade.getPortfolioType().getDescription());
                    account.paymentRate().setValue(cnTrade.getPaymentRate().getDescription());
                    // TODO do we need all descriptions here or just first one?
                    account.paymentType().setValue(cnTrade.getNarratives().getNarrative().get(0).getDescription());

                    dto.accounts().add(account);
                }
            }

            // Court Judgements

            if (report.getCNLegalItems() != null) {
                List<CNLegalItemType> cnLegals = report.getCNLegalItems().getCNLegalItem();
                for (CNLegalItemType cnLegal : cnLegals) {
                    CustomerCreditCheckLongReportDTO.JudgementDTO judgement = EntityFactory.create(CustomerCreditCheckLongReportDTO.JudgementDTO.class);
                    judgement.caseNumber().setValue(cnLegal.getCaseNumber());
                    judgement.customerNumber().setValue(cnLegal.getCourtId().getCustomerNumber());
                    judgement.customerName().setValue(cnLegal.getCourtId().getName());
                    judgement.status().setValue(cnLegal.getStatus().getDescription());
                    judgement.dateFiled().setValue(
                            cnLegal.getDateFiled() != null ? new LogicalDate(cnLegal.getDateFiled().toGregorianCalendar().getTime()) : null);
                    judgement.dateSatisfied().setValue(
                            cnLegal.getDateSatisfied() != null ? new LogicalDate(cnLegal.getDateSatisfied().toGregorianCalendar().getTime()) : null);
                    judgement.plaintiff().setValue(cnLegal.getPlaintiff().getValue());
                    judgement.defendants().setValue(cnLegal.getDefendant());

                    dto.judgements().add(judgement);
                }
            }

            // Proposals and Bankrupcies

            if (report.getCNBankruptciesOrActs() != null) {
                List<CNBankruptcyOrActType> cnActs = report.getCNBankruptciesOrActs().getCNBankruptcyOrAct();
                for (CNBankruptcyOrActType cnAct : cnActs) {
                    CustomerCreditCheckLongReportDTO.ProposalDTO proposal = EntityFactory.create(CustomerCreditCheckLongReportDTO.ProposalDTO.class);
                    proposal.customerNumber().setValue(cnAct.getCourtId().getCustomerNumber());
                    proposal.customerName().setValue(cnAct.getCourtId().getName());
                    if (cnAct.getIntentOrDisposition() != null && cnAct.getIntentOrDisposition().getDate() != null) {
                        proposal.dispositionDate().setValue(new LogicalDate(cnAct.getIntentOrDisposition().getDate().toGregorianCalendar().getTime()));
                    }
                    proposal.liabilityAmount().setValue(cnAct.getLiabilityAmount().getValue());
                    proposal.assetAmount().setValue(cnAct.getAssetAmount() != null ? cnAct.getAssetAmount().getValue() : null);
                    proposal.caseNumberAndTrustee().setValue(cnAct.getCaseNumberAndTrustee());
                    proposal.intentOrDisposition().setValue(cnAct.getIntentOrDisposition().getDescription());
                    dto.proposals().add(proposal);
                }
            }

            // Evictions not present in this equifax report version

            // Rent History not present in this equifax report version

            // Collections

            if (report.getCNCollections() != null) {
                List<CNCollectionType> cnCollections = report.getCNCollections().getCNCollection();
                for (CNCollectionType cnCollection : cnCollections) {
                    CustomerCreditCheckLongReportDTO.CollectionDTO collection = EntityFactory.create(CustomerCreditCheckLongReportDTO.CollectionDTO.class);
                    collection.onBehalf().setValue(cnCollection.getCollectionCreditor().getAccountNumberAndOrName());
                    collection.date().setValue(
                            cnCollection.getAssignedDate() != null ? new LogicalDate(cnCollection.getAssignedDate().toGregorianCalendar().getTime()) : null);
                    collection.lastActive().setValue(
                            cnCollection.getDateOfLastPayment() != null ? new LogicalDate(cnCollection.getDateOfLastPayment().toGregorianCalendar().getTime())
                                    : null);
                    collection.originalAmount().setValue(cnCollection.getOriginalAmount().getValue().getValue());
                    collection.balance().setValue(cnCollection.getBalanceAmount().getValue());
                    collection.status().setValue(cnCollection.getDescription());
                    dto.collections().add(collection);
                }
            }

            // Inquiries

            if (report.getCNLocalInquiries() != null) {
                List<CNLocalInquiryType> cnInquiries = report.getCNLocalInquiries().getCNLocalInquiry();
                for (CNLocalInquiryType cnInquiry : cnInquiries) {
                    CustomerCreditCheckLongReportDTO.InquiryDTO inquiry = EntityFactory.create(CustomerCreditCheckLongReportDTO.InquiryDTO.class);
                    inquiry.onBehalf().setValue(cnInquiry.getCustomerId().getName());
                    inquiry.date().setValue(cnInquiry.getDate() != null ? new LogicalDate(cnInquiry.getDate().toGregorianCalendar().getTime()) : null);
                    inquiry.customerNumber().setValue(cnInquiry.getCustomerId() != null ? cnInquiry.getCustomerId().getCustomerNumber() : null);
                    if (cnInquiry.getCustomerId().getTelephone() != null) {
                        ParsedTelephone cnPhone = cnInquiry.getCustomerId().getTelephone().getParsedTelephone();
                        String phone = cnPhone.getAreaCode().toString() + "-" + cnPhone.getNumber();
                        if (cnPhone.getExtension() != null) {
                            phone = phone + " ex. " + cnPhone.getExtension().toString();
                        }
                        inquiry.phone().setValue(phone);
                    }
                    dto.inquiries().add(inquiry);
                }
            }

            return dto;
        }
        return null;
    }

    private static RiskLevel getRiskLevel(String codeString) {
        if (EquifaxCreditCheck.riskCodeAmountPrcMapping.containsKey(codeString)) {
            return EquifaxCreditCheck.riskLevelMapping.get(codeString);
        } else {
            log.debug("Risk Level Mapping does not contain the value ''{}'', returning null", codeString);
            return null;
        }
    }

    private static RatingLevel getRatingLevel(Double score) {
        if (score >= 750 && score <= 800) {
            return RatingLevel.excellent;
        } else if (score >= 660 && score < 750) {
            return RatingLevel.good;
        } else if (score >= 620 && score < 660) {
            return RatingLevel.fair;
        } else if (score >= 359 && score < 620) {
            return RatingLevel.poor;
        }
        log.debug("Rating Level score is out of limits of 359-800 does not contain the value ''{}'', returning null", score);
        return null;
    }

    private static BigDecimal getTotalRevolvingDebt(List<CNTradeType> trades) {
        BigDecimal total = BigDecimal.ZERO;
        if (trades != null) {
            for (CNTradeType trade : trades) {
                if (trade.getPortfolioType() != null && trade.getPortfolioType().getCode() != null && trade.getPortfolioType().getCode().equals("R")) {
                    if (trade.getBalanceAmount() != null && trade.getBalanceAmount().getValue() != null)
                        total = total.add(trade.getBalanceAmount().getValue());
                }
            }
        }
        return total;
    }

    private static Double getScoreResult(CNScoreType cnScore) {
        if (cnScore.getResult() != null) {
            return new Double(cnScore.getResult().getValue());
        }
        return null;
    }

    private static int[] countLateAccounts(List<CNTradeType> cnTrades) {
        int[] numbers = { 0, 0, 0 };
        if (cnTrades != null) {
            for (CNTradeType cnTrade : cnTrades) {
                if (!"R1".equals((cnTrade.getPortfolioType().getCode() + cnTrade.getPaymentRate().getCode()))) {
                    if (cnTrade.getHistoryDerogatoryCounters() != null) {
                        if (cnTrade.getHistoryDerogatoryCounters().getCount30DayPastDue() != null) {
                            numbers[0]++;
                        }
                        if (cnTrade.getHistoryDerogatoryCounters().getCount60DayPastDue() != null) {
                            numbers[1]++;
                        }
                        if (cnTrade.getHistoryDerogatoryCounters().getCount90DayPastDue() != null) {
                            numbers[2]++;
                        }
                    }
                }
            }
        }
        return numbers;
    }

    private static Integer countAccounts(List<CNTradeType> cnTrades, String code) {
        Integer number = 0;
        if (cnTrades != null) {
            for (CNTradeType cnTrade : cnTrades) {
                if (code.equals((cnTrade.getPortfolioType().getCode() + cnTrade.getPaymentRate().getCode()))) {
                    number++;
                }
            }
        }
        return number;
    }

    private static String getEmployer(CNEmploymentType employment) {
        if (employment != null) {
            return employment.getEmployer();
        }
        return null;
    }

    private static String getOccupation(CNEmploymentType employment) {
        if (employment != null) {
            return employment.getOccupation();
        }
        return null;
    }

    private static LogicalDate getLogicalDateFromString(String string) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (string == null) {
            return null;
        }
        try {
            Date date = formatter.parse(string);
            return new LogicalDate(date);
        } catch (ParseException e) {
            log.error("Error parsing LogicalDate from string", e);
            return null;
        }
    }

    private static CNEmploymentType getEmployment(List<CNEmploymentType> employments, String type) {
        if (employments != null) {
            for (CNEmploymentType employment : employments) {
                if (type.equals(employment.getCode())) {
                    return employment;
                }
            }
        }
        return null;
    }

    private static AddressSimple createAddress(CNAddressType cnAddress) {
        if (cnAddress != null) {
            AddressSimple address = EntityFactory.create(AddressSimple.class);
            address.city().setValue(cnAddress.getCity() != null ? cnAddress.getCity().getValue() : null);
            address.postalCode().setValue(cnAddress.getPostalCode());
            String streetName = "";
            if (cnAddress.getCivicNumber() != null) {
                streetName = streetName + cnAddress.getCivicNumber();
            }
            if (cnAddress.getStreetName() != null) {
                streetName = streetName + " " + cnAddress.getStreetName();
            }
            address.street1().setValue(streetName);
            if (cnAddress.getProvince() != null) {
                address.province().code().setValue(cnAddress.getProvince().getCode());
                List<Province> provinces = getProvinces();
                address.country().name().setValue(getCountry(provinces, cnAddress.getProvince().getCode()));
                address.province().name().setValue(getProvince(provinces, cnAddress.getProvince().getCode()));
            }
            return address;
        }
        return null;
    }

    private static CNScoreType getCNScore(List<CNScoreType> scores, String type) {
        if (scores != null) {
            for (CNScoreType score : scores) {
                if (score.getProductType().equals(type)) {
                    return score;
                }
            }
        }
        return null;
    }

    private static List<Province> getProvinces() {
        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);
        criteria.asc(criteria.proto().name());
        return Persistence.service().query(criteria);
    }

    private static String getProvince(List<Province> provinces, String code) {
        for (Province province : provinces) {
            if (StringUtils.equals(province.code().getValue(), code)) {
                return province.name().getValue();
            }
        }
        return null;
    }

    private static String getCountry(List<Province> provinces, String stateCode) {
        for (Province province : provinces) {
            if (StringUtils.equals(province.code().getValue(), stateCode)) {
                return province.country().name().getValue();
            }
        }
        return null;
    }

    private static CNAddressType getCNAddress(List<CNAddressType> addresses, String type) {
        if (addresses != null) {
            for (CNAddressType address : addresses) {
                if (type.equals(address.getCode())) {
                    return address;
                }
            }
        }
        return null;
    }

    private static BigDecimal getTotalCNCollectionsBalance(List<CNCollectionType> collections) {
        if (collections == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CNCollectionType collection : collections) {
            if ("RA".equals(collection.getCode()) || "RE".equals(collection.getCode())) {
                total = total.add(collection.getBalanceAmount().getValue());
            }
        }
        return total;
    }

    private static BigDecimal getTotalCNTradesBalance(List<CNTradeType> trades) {
        BigDecimal total = BigDecimal.ZERO;
        if (trades != null) {
            for (CNTradeType trade : trades) {
                if (trade.getBalanceAmount() != null && trade.getBalanceAmount().getValue() != null) {
                    total = total.add(trade.getBalanceAmount().getValue());
                }
            }
        }
        return total;
    }

    private static BigDecimal getRejectCode(String codeString) {
        if (EquifaxCreditCheck.riskCodeAmountPrcMapping.containsKey(codeString)) {
            BigDecimal result = new BigDecimal(EquifaxCreditCheck.riskCodeAmountPrcMapping.get(codeString));
            result = result.divide(new BigDecimal(100));
            return result;
        }
        return BigDecimal.ZERO;
    }
}