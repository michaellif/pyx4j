/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.util.Map;
import java.util.concurrent.Callable;

import ca.equifax.uat.from.CNConsumerCreditReportType;
import ca.equifax.uat.from.CNScoreType;
import ca.equifax.uat.from.CodeType;
import ca.equifax.uat.from.EfxReportType;
import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.from.ObjectFactory;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.equifax.request.EquifaxConsts;
import com.propertyvista.equifax.request.XmlCreator;
import com.propertyvista.operations.domain.dev.EquifaxSimulatorConfig;
import com.propertyvista.server.TaskRunner;

public class EquifaxSimulation {

    private static ObjectFactory factory = new ObjectFactory();

    private static class SimulationDefinition {

        CreditCheckResult simulateCheckResult = null;

        int simulateRiskPrc = 0;

        String description;
    }

    public static EfxTransmit simulateResponce(CNConsAndCommRequestType requestMessage, Customer customer, CustomerCreditCheck pcc, int strategyNumber,
            Lease lease, LeaseTermParticipant<?> leaseParticipant) {

        EquifaxSimulatorConfig equifaxSimulatorConfig = TaskRunner.runInOperationsNamespace(new Callable<EquifaxSimulatorConfig>() {
            @Override
            public EquifaxSimulatorConfig call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(EquifaxSimulatorConfig.class));
            }
        });

        EfxTransmit efxResponse;

        SimulationDefinition simulationDefinition = createSimulationDefinition(lease, leaseParticipant);

        String xml = null;
        switch (simulationDefinition.simulateCheckResult) {
        case Accept:
            xml = equifaxSimulatorConfig.approve().xml().getValue();
            break;
        case Decline:
            xml = equifaxSimulatorConfig.decline().xml().getValue();
            break;
        case Review:
            xml = equifaxSimulatorConfig.moreInfo().xml().getValue();
            break;
        default:
            throw new IllegalArgumentException();
        }

        if (xml == null) {
            efxResponse = factory.createEfxTransmit();
        } else {
            efxResponse = XmlCreator.fromStorageXMl(xml);
        }

        appendCustomerInfo(efxResponse, customer);

        appendScore(efxResponse, simulationDefinition);

        return efxResponse;
    }

    private static SimulationDefinition createSimulationDefinition(Lease lease, LeaseTermParticipant<?> leaseParticipant) {

        SimulationDefinition simulationDefinition = new SimulationDefinition();

        boolean hasDependent = false;
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            if (tenant.role().getValue() == LeaseTermParticipant.Role.Dependent) {
                hasDependent = true;
                break;
            }
        }
        boolean hasGuarantors = (lease.currentTerm().version().guarantors().size() != 0);
        //
        int sDecision = 0;
        if (hasDependent) {
            sDecision = 1;
        } else if (hasGuarantors) {
            sDecision = 2;
        } else {
            sDecision = 3;
        }

        switch (sDecision) {
        case 1:
            simulationDefinition.simulateCheckResult = CreditCheckResult.Accept;
            simulationDefinition.simulateRiskPrc = 100;
            simulationDefinition.description = "Accept - requested rent amount";
            break;
        case 2:
            switch (leaseParticipant.role().getValue()) {
            case Applicant:
                simulationDefinition.simulateCheckResult = CreditCheckResult.Review;
                simulationDefinition.simulateRiskPrc = 50;
                simulationDefinition.description = "Approve rent for 50% of requested amount";
                break;
            case CoApplicant:
                simulationDefinition.simulateCheckResult = CreditCheckResult.Review;
                simulationDefinition.simulateRiskPrc = 30;
                simulationDefinition.description = "Approve rent for 30% of requested amount";
                break;
            case Guarantor:
                simulationDefinition.simulateCheckResult = CreditCheckResult.Accept;
                simulationDefinition.simulateRiskPrc = 100;
                simulationDefinition.description = "Accept - requested rent amount";
                break;
            default:
                throw new IllegalArgumentException();
            }
            break;
        case 3:
            switch (leaseParticipant.role().getValue()) {
            case Applicant:
                simulationDefinition.simulateCheckResult = CreditCheckResult.Review;
                simulationDefinition.simulateRiskPrc = 100;
                simulationDefinition.description = "Accept - requested rent amount";
                break;
            case CoApplicant:
                simulationDefinition.simulateCheckResult = CreditCheckResult.Decline;
                simulationDefinition.description = "Decline applicant due to Bankruptcy";
                break;
            default:
                throw new IllegalArgumentException();
            }
            break;
        default:
            throw new Error("Simulation Decision" + sDecision + " not designed");
        }

        return simulationDefinition;
    }

    private static void appendCustomerInfo(EfxTransmit efxResponse, Customer customer) {
        CNConsumerCreditReportType creditReport = getOrCreateCreditReport(efxResponse);

        if (creditReport.getCNHeader() == null) {
            creditReport.setCNHeader(factory.createCNHeaderType());
        }
        if (creditReport.getCNHeader().getSubject() == null) {
            creditReport.getCNHeader().setSubject(factory.createCNHeaderTypeSubject());
        }
        if (creditReport.getCNHeader().getSubject().getSubjectName() == null) {
            creditReport.getCNHeader().getSubject().setSubjectName(factory.createCNSubjectNameType());
        }

        creditReport.getCNHeader().getSubject().getSubjectName().setFirstName(customer.person().name().firstName().getStringView());
        creditReport.getCNHeader().getSubject().getSubjectName().setLastName(customer.person().name().lastName().getStringView());

        creditReport.getCNHeader().getSubject().getSubjectName()
                .setMiddleName(customer.person().name().middleName() != null ? customer.person().name().middleName().getStringView() : null);

    }

    private static void appendScore(EfxTransmit efxResponse, SimulationDefinition simulationDefinition) {
        CNConsumerCreditReportType creditReport = getOrCreateCreditReport(efxResponse);

        CNScoreType scoreIDecision = null;
        for (CNScoreType score : creditReport.getCNScores().getCNScore()) {
            if (EquifaxConsts.scoringProductId_iDecisionPower.equals(score.getProductId())) {
                scoreIDecision = score;
            }
        }
        if (scoreIDecision == null) {
            scoreIDecision = factory.createCNScoreType();
            creditReport.setCNScores(factory.createCNScoresType());
            creditReport.getCNScores().getCNScore().add(scoreIDecision);
            scoreIDecision.setProductId(EquifaxConsts.scoringProductId_iDecisionPower);
        }
        if (scoreIDecision.getRejectCodes() == null) {
            scoreIDecision.setRejectCodes(factory.createCNScoreTypeRejectCodes());
        }

        CodeType codeType;
        if (scoreIDecision.getRejectCodes().getRejectCode() == null) {
            codeType = factory.createCodeType();
            scoreIDecision.getRejectCodes().getRejectCode().add(codeType);
        } else {
            codeType = scoreIDecision.getRejectCodes().getRejectCode().get(0);
        }

        String riskCode = null;

        if (simulationDefinition.simulateCheckResult == CreditCheckResult.Decline) {
            riskCode = "B5"; // Bankruptcy
        } else if (simulationDefinition.simulateCheckResult == CreditCheckResult.Accept) {
            riskCode = "01";
        } else {
            String prevRiskCode = "01";
            for (Map.Entry<String, Integer> me : EquifaxCreditCheck.riskCodeAmountPrcMapping.entrySet()) {
                if (me.getValue() < simulationDefinition.simulateRiskPrc) {
                    riskCode = prevRiskCode;
                    break;
                }
                prevRiskCode = me.getKey();
            }
        }

        codeType.setCode(riskCode);
        codeType.setDescription(simulationDefinition.description);
    }

    private static CNConsumerCreditReportType getOrCreateCreditReport(EfxTransmit efxResponse) {
        EfxReportType efxReportType = null;
        for (EfxReportType efxReportTypeI : efxResponse.getEfxReport()) {
            efxReportType = efxReportTypeI;
            for (CNConsumerCreditReportType creditReport : efxReportTypeI.getCNConsumerCreditReports().getCNConsumerCreditReport()) {
                return creditReport;
            }
        }

        if (efxReportType == null) {
            efxReportType = factory.createEfxReportType();
            efxResponse.getEfxReport().add(efxReportType);
        }

        efxReportType.setCNConsumerCreditReports(factory.createEfxReportTypeCNConsumerCreditReports());
        CNConsumerCreditReportType creditReport = factory.createCNConsumerCreditReportType();
        efxReportType.getCNConsumerCreditReports().getCNConsumerCreditReport().add(creditReport);
        return creditReport;
    }

}
