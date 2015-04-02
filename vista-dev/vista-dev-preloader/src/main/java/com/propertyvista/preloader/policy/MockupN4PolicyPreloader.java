/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 30, 2015
 * @author ernestog
 */
package com.propertyvista.preloader.policy;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.policies.N4Policy;
import com.propertyvista.domain.policy.policies.N4Policy.EmployeeSelectionMethod;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.generator.BuildingsGenerator.BuildingsGeneratorConfig;
import com.propertyvista.generator.util.CommonsGenerator;

public class MockupN4PolicyPreloader extends BaseVistaDevDataPreloader {

    public MockupN4PolicyPreloader() {
    }

    @Override
    public String create() {
        N4Policy policy = Persistence.service().retrieve(EntityQueryCriteria.create(N4Policy.class));
        // Add ARCodes
        EntityQueryCriteria<ARCode> arCodes = EntityQueryCriteria.create(ARCode.class);
        for (ARCode arCode : Persistence.service().query(arCodes)) {
            policy.relevantARCodes().add(arCode);
        }

        // Add N4 Agent Info
        policy.agentSelectionMethodN4().setValue(EmployeeSelectionMethod.ByLoggedInUser);
        policy.useAgentContactInfoN4().setValue(Boolean.FALSE);
        policy.useAgentSignatureN4().setValue(Boolean.TRUE);

        // Add CS Agent Info
        policy.agentSelectionMethodCS().setValue(EmployeeSelectionMethod.ByLoggedInUser);
        policy.useAgentContactInfoCS().setValue(Boolean.TRUE);
        policy.useAgentSignatureCS().setValue(Boolean.TRUE);
        policy.phoneNumberCS().setValue(CommonsGenerator.createPhone());

        // Add company info
        policy.companyName().setValue("Advance Legals Ltd.");
        policy.emailAddress().setValue("communications@advancelegals.com");
        policy.phoneNumber().setValue(CommonsGenerator.createPhone());
        policy.faxNumber().setValue(CommonsGenerator.createPhone());

        BuildingsGeneratorConfig config = new BuildingsGeneratorConfig();
        config.provinceCode = "ON";
        config.country = ISOCountry.Canada;

        policy.mailingAddress().set(CommonsGenerator.createInternationalAddress(config));

        Persistence.service().persist(policy);
        return null;
    }

    @Override
    public String delete() {
        return null;
    }
}
