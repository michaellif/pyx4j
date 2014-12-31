/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2014
 * @author VladL
 */
package com.propertyvista.operations.server.upgrade.u_1_4_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.policy.subpreloaders.ApplicationDocumentationPolicyPreloader;
import com.propertyvista.biz.preloader.policy.subpreloaders.LegalQuestionsPolicyPreloader;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;

public class UpgradeProcedure142 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure142.class);

    @Override
    public int getUpgradeStepsCount() {
        return 2;
    }

    @Override
    public String runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runLegalQuestionsPolicyPreload();
            break;
        case 2:
            runApplicationDocumentationPolicyUpdate();
            break;
        default:
            throw new IllegalArgumentException();
        }
        return null;
    }

    private void runLegalQuestionsPolicyPreload() {
        log.info("Creating LegalQuestionsPolicy and setting its scope to 'Organization'");
        LegalQuestionsPolicyPreloader policyPreloader = new LegalQuestionsPolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Finished policy creation: " + policyCreationLog);
    }

    private void runApplicationDocumentationPolicyUpdate() {
        ApplicationDocumentationPolicyPreloader policyPreloader = new ApplicationDocumentationPolicyPreloader();

        EntityQueryCriteria<ApplicationDocumentationPolicy> criteria = new EntityQueryCriteria<>(ApplicationDocumentationPolicy.class);
        for (ApplicationDocumentationPolicy policy : Persistence.service().query(criteria)) {
            policyPreloader.updatePolicy(policy);
            Persistence.service().merge(policy);
        }
    }
}
