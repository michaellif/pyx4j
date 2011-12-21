/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Arrays;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.PolicyPreset;
import com.propertyvista.domain.policy.PolicyPresetAtNode;
import com.propertyvista.domain.policy.PolicyPresetAtNode.NodeType;
import com.propertyvista.domain.policy.policies.AllowedIDs;
import com.propertyvista.domain.policy.policies.GymUsageFeePolicy;
import com.propertyvista.domain.policy.policies.IdentificationDocument;
import com.propertyvista.domain.policy.policies.NumberOfIDs;
import com.propertyvista.domain.policy.policies.PoolUsageFeePolicy;

public class PolicyPreloader extends BaseVistaDevDataPreloader {
    private static final I18n i18n = I18n.get(PolicyPreloader.class);

    @Override
    public String create() {
        PolicyPreset defaultPolicyPreset = EntityFactory.create(PolicyPreset.class);
        defaultPolicyPreset.name().setValue(i18n.tr("Default Global Policy"));
        defaultPolicyPreset.description().setValue(i18n.tr("Default built-in global policy"));

        // Create default policies
        defaultPolicyPreset.policies().addAll(Arrays.asList(//@formatter:off
                initNumberOfIdsDefaultPolicy(),
                initAllowedIdsDefaultPolicy(),
                initGymUsageFeeDefaultPolicy(),
                initPoolUsageFeeDefaultPolicy()
        ));//@formatter:on

        Persistence.service().persist(defaultPolicyPreset);

        // Assign the default preset to the organization
        PolicyPresetAtNode policyPresetAtOrganizationNode = EntityFactory.create(PolicyPresetAtNode.class);
        policyPresetAtOrganizationNode.nodeType().setValue(NodeType.organization);
        policyPresetAtOrganizationNode.policyPreset().set(defaultPolicyPreset);

        Persistence.service().persist(policyPresetAtOrganizationNode);

        return "Created default global policy";
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PolicyPresetAtNode.class, IdentificationDocument.class);
        } else {
            return "This is production";
        }
    }

    private NumberOfIDs initNumberOfIdsDefaultPolicy() {
        NumberOfIDs numOfIDs = EntityFactory.create(NumberOfIDs.class);
        numOfIDs.numOfIDs().setValue(5);
        Persistence.service().persist(numOfIDs);
        return numOfIDs;
    }

    private AllowedIDs initAllowedIdsDefaultPolicy() {
        // Allowed IDs default policy
        AllowedIDs allowedIDs = EntityFactory.create(AllowedIDs.class);
        IdentificationDocument id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Passport"));
        Persistence.service().persist(id);
        allowedIDs.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Drivers License"));
        Persistence.service().persist(id);
        allowedIDs.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocument.class);
        id.name().setValue(i18n.tr("Citizenship Card"));
        Persistence.service().persist(id);
        allowedIDs.allowedIDs().add(id);
        Persistence.service().persist(allowedIDs);
        return allowedIDs;
    }

    private GymUsageFeePolicy initGymUsageFeeDefaultPolicy() {
        GymUsageFeePolicy policy = EntityFactory.create(GymUsageFeePolicy.class);
        policy.monthlyGymFee().setValue(30.0);
        Persistence.service().persist(policy);
        return policy;
    }

    private PoolUsageFeePolicy initPoolUsageFeeDefaultPolicy() {
        PoolUsageFeePolicy policy = EntityFactory.create(PoolUsageFeePolicy.class);
        policy.monthlyPoolFee().setValue(27.99);
        Persistence.service().persist(policy);
        return policy;
    }

}
