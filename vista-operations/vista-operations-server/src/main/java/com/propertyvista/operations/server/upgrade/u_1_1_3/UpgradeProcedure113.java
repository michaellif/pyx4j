/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_1_3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;
import com.propertyvista.operations.server.upgrade.u_1_0_5.UpgradeProcedure105;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.LegalTermsPolicyPreloader;
import com.propertyvista.portal.server.preloader.policy.subpreloaders.ProspectPortalPolicyPreloader;

public class UpgradeProcedure113 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure105.class);

    @Override
    public int getUpgradeStepsCount() {
        return 1;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runLegalTermsPolicyPreloaderPolicyGeneration();
            runProspectPortalPolicyPreloaderPolicyGeneration();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runLegalTermsPolicyPreloaderPolicyGeneration() {
        log.info("Creating LegalTermsPolicy and setting its scope to 'Organization'");
        LegalTermsPolicyPreloader policyPreloader = new LegalTermsPolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Finished policy creation: " + policyCreationLog);
    }

    private void runProspectPortalPolicyPreloaderPolicyGeneration() {
        log.info("Creating ProspectPortalPolicy and setting its scope to 'Organization'");
        ProspectPortalPolicyPreloader policyPreloader = new ProspectPortalPolicyPreloader();
        OrganizationPoliciesNode organizationNode = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        if (organizationNode == null) {
            throw new UserRuntimeException("Organizational Policy Was not found");
        }
        policyPreloader.setTopNode(organizationNode);
        String policyCreationLog = policyPreloader.create();
        log.info("Finished policy creation: " + policyCreationLog);
    }
}
