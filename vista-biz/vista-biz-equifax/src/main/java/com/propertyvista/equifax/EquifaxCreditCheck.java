/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.from.CNConsumerCreditReportType;
import ca.equifax.uat.from.CNErrorReportType;
import ca.equifax.uat.from.CNScoreType;
import ca.equifax.uat.from.CodeType;
import ca.equifax.uat.from.EfxReportType;
import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.dev.EntityFileLogger;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.DevInfoUnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.RiskLevel;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.equifax.request.EquifaxConsts;
import com.propertyvista.equifax.request.EquifaxHttpClient;
import com.propertyvista.equifax.request.EquifaxModelMapper;
import com.propertyvista.equifax.request.XmlCreator;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.domain.CustomerCreditCheckReport;
import com.propertyvista.server.domain.CustomerCreditCheckReportNoBackup;

public class EquifaxCreditCheck {

    private final static Logger log = LoggerFactory.getLogger(EquifaxCreditCheck.class);

    private final static I18n i18n = I18n.get(EquifaxCreditCheck.class);

    private final static Map<String, CreditCheckResult> riskCodeMapping = new HashMap<String, CreditCheckResult>();

    final static Map<String, Integer> riskCodeAmountPrcMapping = new LinkedHashMap<String, Integer>();

    final static Map<String, CustomerCreditCheckLongReportDTO.RiskLevel> riskLevelMapping = new HashMap<String, CustomerCreditCheckLongReportDTO.RiskLevel>();

    private final static Map<String, String> riskCodeOverrideDescription = new HashMap<String, String>();

    static {
        riskCodeAmountPrcMapping.put("01", 100);
        riskCodeAmountPrcMapping.put("02", 80);
        riskCodeAmountPrcMapping.put("03", 70);
        riskCodeAmountPrcMapping.put("04", 60);
        riskCodeAmountPrcMapping.put("05", 50);
        riskCodeAmountPrcMapping.put("06", 40);
        riskCodeAmountPrcMapping.put("07", 30);
        riskCodeAmountPrcMapping.put("08", 20);
        riskCodeAmountPrcMapping.put("09", 10);

        riskCodeMapping.put("01", CreditCheckResult.Accept);
        riskCodeMapping.put("02", CreditCheckResult.Accept);
        riskCodeMapping.put("03", CreditCheckResult.Accept);
        riskCodeMapping.put("04", CreditCheckResult.Accept);
        riskCodeMapping.put("05", CreditCheckResult.Accept);
        riskCodeMapping.put("06", CreditCheckResult.Accept);
        riskCodeMapping.put("07", CreditCheckResult.Accept);
        riskCodeMapping.put("08", CreditCheckResult.Accept);
        riskCodeMapping.put("09", CreditCheckResult.Accept);

        riskCodeMapping.put("S9", CreditCheckResult.ReviewNoInformationAvalable);

        riskCodeMapping.put("10", CreditCheckResult.Review);
        riskCodeMapping.put("11", CreditCheckResult.Review);
        riskCodeMapping.put("U9", CreditCheckResult.Review);
        riskCodeMapping.put("D1", CreditCheckResult.Review);
        riskCodeMapping.put("H2", CreditCheckResult.Review);
        riskCodeMapping.put("H3", CreditCheckResult.Review);

        riskCodeMapping.put("B5", CreditCheckResult.Decline);
        riskCodeMapping.put("J8", CreditCheckResult.Decline);
        riskCodeMapping.put("C5", CreditCheckResult.Decline);
        riskCodeMapping.put("R8", CreditCheckResult.Decline);

        riskCodeOverrideDescription.put("11", i18n.ntr("Review - Decline Applicant received an unacceptably low score"));

        riskLevelMapping.put("01", RiskLevel.veryLowRisk);
        riskLevelMapping.put("02", RiskLevel.lowRisk);
        riskLevelMapping.put("03", RiskLevel.lowRisk);
        riskLevelMapping.put("04", RiskLevel.moderateRisk);
        riskLevelMapping.put("05", RiskLevel.moderateRisk);
        riskLevelMapping.put("06", RiskLevel.highRisk);
        riskLevelMapping.put("07", RiskLevel.highRisk);
        riskLevelMapping.put("08", RiskLevel.highRisk);
        riskLevelMapping.put("09", RiskLevel.highRisk);
        riskLevelMapping.put("10", RiskLevel.veryHighRisk);
        riskLevelMapping.put("11", RiskLevel.veryHighRisk);

    }

    public static CustomerCreditCheck runCreditCheck(PmcEquifaxInfo equifaxInfo, Customer customer, CustomerCreditCheck pcc, int strategyNumber,
    /* simulation data parameters */
    Lease lease, LeaseTermParticipant<?> leaseParticipant) {

        if (equifaxInfo.reportType().getValue() == CreditCheckReportType.FullCreditReport) {
            if (!ServerSideFactory.create(EncryptedStorageFacade.class).isStorageAvalable()) {
                log.warn("Encrypted Storage not available -> Credit Check will not work");
                throw new UserRuntimeException(i18n.tr("Credit Check is not available at this time"));
            }
        }

        EquifaxEnforceLimit.assertLimit(AuditRecordEventType.EquifaxRequest);
        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.EquifaxRequest, customer, "Run EquifaxRequest for customer {0}", customer);

        CNConsAndCommRequestType requestMessage = EquifaxModelMapper.createRequest(equifaxInfo, customer, pcc, strategyNumber);

        if (ApplicationMode.isDevelopment()) {
            EntityFileLogger.logXml("equifax", "request", XmlCreator.devToXMl(requestMessage));
        }

        EfxTransmit efxResponse;
        if (VistaSystemsSimulationConfig.getConfiguration().useEquifaxSimulator().getValue(Boolean.FALSE)) {
            efxResponse = EquifaxSimulation.simulateResponce(requestMessage, customer, pcc, strategyNumber, lease, leaseParticipant);
        } else {
            try {
                efxResponse = EquifaxHttpClient.execute(requestMessage);
            } catch (Exception e) {
                log.error("Equifax error", e);
                if (ApplicationMode.isDevelopment()) {
                    throw new DevInfoUnRecoverableRuntimeException(e);
                } else {
                    throw new UnRecoverableRuntimeException(i18n.tr("Equifax communication error"));
                }
            }
        }

        if (ApplicationMode.isDevelopment()) {
            EntityFileLogger.logXml("equifax", "response", XmlCreator.devToXMl(efxResponse));
            EntityFileLogger.logXml("equifax", "response-db", XmlCreator.toStorageXMl(efxResponse));
        }

        if (equifaxInfo.reportType().getValue() == CreditCheckReportType.FullCreditReport) {
            final CustomerCreditCheckReport report = EntityFactory.create(CustomerCreditCheckReport.class);
            report.pmc().setValue(equifaxInfo.pmc().getPrimaryKey());
            report.customer().setValue(customer.getPrimaryKey());
            EquifaxEncryptedStorage.encrypt(report, XmlCreator.toStorageXMl(efxResponse));

            TaskRunner.runInTargetNamespace(VistaNamespace.expiringNamespace, new Callable<Void>() {
                @Override
                public Void call() {
                    Persistence.service().persist(report);
                    Persistence.service().commit();
                    return null;
                }
            });
            pcc.creditCheckReport().setValue(report.getPrimaryKey());
        }

        reportsLoop: for (EfxReportType efxReportType : efxResponse.getEfxReport()) {
            for (CNConsumerCreditReportType creditReport : efxReportType.getCNConsumerCreditReports().getCNConsumerCreditReport()) {
                for (CNScoreType score : creditReport.getCNScores().getCNScore()) {
                    if (EquifaxConsts.scoringProductId_iDecisionPower.equals(score.getProductId())) {
                        for (CodeType codeType : score.getRejectCodes().getRejectCode()) {
                            pcc.riskCode().setValue(codeType.getCode());
                            pcc.reason().setValue(codeType.getDescription());
                            break reportsLoop;
                        }
                    }
                }
            }
        }

        CreditCheckResult creditCheckResult = riskCodeMapping.get(pcc.riskCode().getValue());
        if (creditCheckResult == null) {
            creditCheckResult = CreditCheckResult.Error;
            if (efxResponse.getCNErrorReport() != null) {
                for (CNErrorReportType.Errors.Error error : efxResponse.getCNErrorReport().getErrors().getError()) {
                    pcc.reason().setValue(
                            error.getErrorCode()
                                    + " "
                                    + CommonsStringUtils.nvl_concat(CommonsStringUtils.nvl_concat(error.getDescription(), error.getActionDescription(), " "),
                                            error.getAdditionalInformation(), " "));
                    break;
                }
            }
        }

        String overrideDescription = riskCodeOverrideDescription.get(pcc.riskCode().getValue());
        if (overrideDescription != null) {
            pcc.reason().setValue(overrideDescription);
        }

        if (creditCheckResult == CreditCheckResult.Accept) {
            Integer prc = riskCodeAmountPrcMapping.get(pcc.riskCode().getValue());
            if (prc == null) {
                creditCheckResult = CreditCheckResult.Error;
            } else {
                pcc.amountApproved().setValue(pcc.amountChecked().getValue().multiply(new BigDecimal(prc)).divide(new BigDecimal("100")));
            }
        }

        pcc.creditCheckResult().setValue(creditCheckResult);

        return pcc;
    }

    public static CustomerCreditCheckLongReportDTO createLongReport(CustomerCreditCheck ccc) {

        EquifaxEnforceLimit.assertLimit(AuditRecordEventType.EquifaxReadReport);

        Persistence.ensureRetrieve(ccc.screening(), AttachLevel.Attached);
        Persistence.ensureRetrieve(ccc.screening().screene(), AttachLevel.ToStringMembers);

        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.EquifaxReadReport, ccc.screening().screene(),
                "Read Equifax LongReport for customer {0}", ccc.screening().screene());

        Pmc pmc = VistaDeployment.getCurrentPmc();

        final EntityQueryCriteria<CustomerCreditCheckReport> criteria = EntityQueryCriteria.create(CustomerCreditCheckReport.class);
        criteria.eq(criteria.proto().id(), ccc.creditCheckReport());
        criteria.eq(criteria.proto().pmc(), pmc);

        final EntityQueryCriteria<CustomerCreditCheckReportNoBackup> criteria2 = EntityQueryCriteria.create(CustomerCreditCheckReportNoBackup.class);
        criteria2.eq(criteria2.proto().id(), ccc.creditCheckReport());
        criteria2.eq(criteria2.proto().pmc(), pmc);

        CustomerCreditCheckReport report = TaskRunner.runInTargetNamespace(VistaNamespace.expiringNamespace, new Callable<CustomerCreditCheckReport>() {
            @Override
            public CustomerCreditCheckReport call() {
                CustomerCreditCheckReport cr = Persistence.service().retrieve(criteria);
                if (cr == null) {
                    cr = Persistence.service().retrieve(criteria2);
                }
                if (cr == null) {
                    return null;
                } else {
                    return cr;
                }
            }
        });

        if (report == null) {
            return null;
        }

        EfxTransmit efxResponse;
        try {
            efxResponse = XmlCreator.fromStorageXMl(EquifaxEncryptedStorage.decrypt(report));
        } catch (Exception e) {
            log.error("data error", e);
            if (ApplicationMode.isDevelopment()) {
                throw new DevInfoUnRecoverableRuntimeException(e);
            } else {
                throw new UnRecoverableRuntimeException(i18n.tr("Equifax data storage error"));
            }
        }
        return EquifaxLongReportModelMapper.createLongReport(efxResponse, ccc);
    }
}
