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

import java.util.concurrent.Callable;

import ca.equifax.uat.from.CNConsumerCreditReportType;
import ca.equifax.uat.from.CNScoreType;
import ca.equifax.uat.from.CodeType;
import ca.equifax.uat.from.EfxReportType;
import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.from.ObjectFactory;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.domain.dev.EquifaxSimulatorConfig;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.equifax.request.EquifaxConsts;
import com.propertyvista.equifax.request.XmlCreator;
import com.propertyvista.server.jobs.TaskRunner;

public class EquifaxSimulation {

    private static ObjectFactory factory = new ObjectFactory();

    public static EfxTransmit simulateResponce(CNConsAndCommRequestType requestMessage, Customer customer, CustomerCreditCheck pcc, int strategyNumber,
            Lease lease) {

        EquifaxSimulatorConfig equifaxSimulatorConfig = TaskRunner.runInAdminNamespace(new Callable<EquifaxSimulatorConfig>() {
            @Override
            public EquifaxSimulatorConfig call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(EquifaxSimulatorConfig.class));
            }
        });

        EfxTransmit efxResponse;

        String xml = null;
        int simDecision = (int) (lease.id().getValue().asLong() % 10) % 3;
        switch (simDecision) {
        case 0:
            xml = equifaxSimulatorConfig.declineXml().getValue();
            break;
        case 1:
            xml = equifaxSimulatorConfig.approveXml().getValue();
            break;
        case 2:
            xml = equifaxSimulatorConfig.moreInfoXml().getValue();
            break;
        default:
            throw new Error("# % 3 = " + simDecision + " not working");
        }

        if (xml == null) {
            efxResponse = factory.createEfxTransmit();
        } else {
            efxResponse = XmlCreator.fromStorageXMl(xml);
        }

        boolean riskCodeFound = false;
        reportsLoop: for (EfxReportType efxReportType : efxResponse.getEfxReport()) {
            for (CNConsumerCreditReportType creditReport : efxReportType.getCNConsumerCreditReports().getCNConsumerCreditReport()) {
                for (CNScoreType score : creditReport.getCNScores().getCNScore()) {
                    if (EquifaxConsts.scoringProductId_iDecisionPower.equals(score.getProductId())) {
                        for (CodeType codeType : score.getRejectCodes().getRejectCode()) {
                            if (codeType.getCode() != null) {
                                riskCodeFound = true;
                                break reportsLoop;
                            }
                        }
                    }
                }
            }
        }

        appendCustomerInfo(efxResponse, customer);

        if (!riskCodeFound) {
            appendScore(efxResponse, pcc);
        }

        return efxResponse;
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
    }

    private static void appendScore(EfxTransmit efxResponse, CustomerCreditCheck pcc) {
        CNConsumerCreditReportType creditReport = getOrCreateCreditReport(efxResponse);

        CNScoreType score = factory.createCNScoreType();
        creditReport.setCNScores(factory.createCNScoresType());
        creditReport.getCNScores().getCNScore().add(score);
        score.setProductId(EquifaxConsts.scoringProductId_iDecisionPower);

        CodeType codeType = factory.createCodeType();
        score.setRejectCodes(factory.createCNScoreTypeRejectCodes());
        score.getRejectCodes().getRejectCode().add(codeType);

        String riskCode = pcc.screening().version().currentAddress().suiteNumber().getValue();
        if (riskCode == null) {
            riskCode = "01";
        }
        codeType.setCode(riskCode);
        codeType.setDescription("Simulation reason Description");
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
