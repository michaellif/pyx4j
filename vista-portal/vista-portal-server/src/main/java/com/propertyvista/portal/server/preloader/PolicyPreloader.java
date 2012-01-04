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
import java.util.List;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.DefaultPoliciesNode;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.policies.AllowedIDsPolicy;
import com.propertyvista.domain.policy.policies.GymUsageFeePolicy;
import com.propertyvista.domain.policy.policies.NumberOfIDsPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.PoolUsageFeePolicy;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.IdentificationDocument;

public class PolicyPreloader extends BaseVistaDevDataPreloader {
    private static final I18n i18n = I18n.get(PolicyPreloader.class);

    @Override
    public String create() {
        // create a node for organzation scope policies
        Persistence.service().persist(EntityFactory.create(OrganizationPoliciesNode.class));

        // Create the node for default policies and default policies
        DefaultPoliciesNode defaultPoliciesNode = EntityFactory.create(DefaultPoliciesNode.class);
        Persistence.service().persist(defaultPoliciesNode);

        List<? extends Policy> defaults = Arrays.asList(//@formatter:off
                createDefaultNumberOfIdsPolicy(),
                createDefaultLeaseTermsPolicy(),
                createDefaultPetPolicy(),
                createDefaultAllowedIdsPolicy(),
                createDefaultGymUsageFeePolicy(),
                createDefaultPoolUsageFeePolicy()
        );//@formatter:on

        for (Policy policy : defaults) {
            PolicyAtNode policyAtNode = EntityFactory.create(PolicyAtNode.class);
            policyAtNode.node().set(defaultPoliciesNode);
            policyAtNode.policy().set(policy);
            Persistence.service().persist(policyAtNode);
        }

        return "Created default global policies";
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PolicyAtNode.class, IdentificationDocument.class, DefaultPoliciesNode.class, OrganizationPoliciesNode.class);
        } else {
            return "This is production";
        }
    }

    private NumberOfIDsPolicy createDefaultNumberOfIdsPolicy() {
        NumberOfIDsPolicy numOfIDs = EntityFactory.create(NumberOfIDsPolicy.class);
        numOfIDs.numberOfIDs().setValue(5);
        Persistence.service().persist(numOfIDs);
        return numOfIDs;
    }

    private PetPolicy createDefaultPetPolicy() {
        PetPolicy petPolicy = EntityFactory.create(PetPolicy.class);
        petPolicy.maxPetWeight().setValue(50d);
        petPolicy.maxNumberOfPets().setValue(10);
        petPolicy.petChargeEquation().setValue("E = mc^2");
        Persistence.service().persist(petPolicy);
        return petPolicy;
    }

    private AllowedIDsPolicy createDefaultAllowedIdsPolicy() {
        // Allowed IDs default policy
        AllowedIDsPolicy allowedIDs = EntityFactory.create(AllowedIDsPolicy.class);
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

    private GymUsageFeePolicy createDefaultGymUsageFeePolicy() {
        GymUsageFeePolicy policy = EntityFactory.create(GymUsageFeePolicy.class);
        policy.monthlyGymFee().setValue(30.0);
        Persistence.service().persist(policy);
        return policy;
    }

    private PoolUsageFeePolicy createDefaultPoolUsageFeePolicy() {
        PoolUsageFeePolicy policy = EntityFactory.create(PoolUsageFeePolicy.class);
        policy.monthlyPoolFee().setValue(27.99);
        Persistence.service().persist(policy);
        return policy;
    }

    private LegalTermsPolicy createDefaultLeaseTermsPolicy() {
        LegalTermsPolicy policy = EntityFactory.create(LegalTermsPolicy.class);
        Persistence.service().persist(policy);
        return policy;
    }

}
