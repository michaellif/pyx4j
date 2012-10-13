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

import ca.equifax.uat.from.CNConsumerCreditReportType;
import ca.equifax.uat.from.CNScoreType;
import ca.equifax.uat.from.CodeType;
import ca.equifax.uat.from.EfxReportType;
import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.from.ObjectFactory;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;

public class EquifaxSimulation {

    public static EfxTransmit simulateResponce(CNConsAndCommRequestType requestMessage, Customer customer, PersonCreditCheck pcc, int strategyNumber) {
        ObjectFactory factory = new ObjectFactory();
        EfxTransmit response = factory.createEfxTransmit();

        EfxReportType efxReportType = factory.createEfxReportType();
        response.getEfxReport().add(efxReportType);

        efxReportType.setCNConsumerCreditReports(factory.createEfxReportTypeCNConsumerCreditReports());
        CNConsumerCreditReportType creditReport = factory.createCNConsumerCreditReportType();
        efxReportType.getCNConsumerCreditReports().getCNConsumerCreditReport().add(creditReport);

        CNScoreType score = factory.createCNScoreType();
        creditReport.setCNScores(factory.createCNScoresType());
        creditReport.getCNScores().getCNScore().add(score);
        score.setProductId("10301");

        CodeType codeType = factory.createCodeType();
        score.setRejectCodes(factory.createCNScoreTypeRejectCodes());
        score.getRejectCodes().getRejectCode().add(codeType);

        String riskCode = pcc.screening().version().currentAddress().suiteNumber().getValue();
        if (riskCode == null) {
            riskCode = "01";
        }
        codeType.setCode(riskCode);
        codeType.setDescription("Simulation reason Description");

        return response;
    }
}
