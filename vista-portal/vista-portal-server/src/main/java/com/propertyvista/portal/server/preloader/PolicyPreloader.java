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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.propertvista.generator.BuildingsGenerator;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.policies.AllowedIDsPolicy;
import com.propertyvista.domain.policy.policies.GymUsageFeePolicy;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.NumberOfIDsPolicy;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.PoolUsageFeePolicy;
import com.propertyvista.domain.policy.policies.specials.IdentificationDocument;
import com.propertyvista.domain.policy.policies.specials.LegalTermsContent;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.shared.CompiledLocale;

public class PolicyPreloader extends BaseVistaDevDataPreloader {
    private static final I18n i18n = I18n.get(PolicyPreloader.class);

    @Override
    public String create() {
        // Create the node for default policies and default policies
        OrganizationPoliciesNode organizationalPoliciesNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(organizationalPoliciesNode);

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
            policyAtNode.node().set(organizationalPoliciesNode);
            policyAtNode.policy().set(policy);
            Persistence.service().persist(policyAtNode);
        }

        return "Created default global policies";
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PolicyAtNode.class, IdentificationDocument.class, OrganizationPoliciesNode.class);
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

    private LeaseTermsPolicy createDefaultLeaseTermsPolicy() {
        LeaseTermsPolicy policy = EntityFactory.create(LeaseTermsPolicy.class);

        String termsContentText = "failed to get lease terms during the preload phase";
        try {
            termsContentText = IOUtils.getTextResource("leaseTerms.html", BuildingsGenerator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AvailableLocale en = EntityFactory.create(AvailableLocale.class);
        en.displayOrder().setValue(10);
        en.lang().setValue(CompiledLocale.en);
        Persistence.service().merge(en);

        String caption = i18n.tr("Mockup Summary Terms");
        policy.summaryTerms().add(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        caption = i18n.tr("Mockup One Time Payment Terms");
        policy.oneTimePaymentTerms().set(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        caption = i18n.tr("Mockup Recurrent Time Payment Terms");
        policy.recurrentPaymentTerms().set(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

        Persistence.service().merge(policy);

        return policy;
    }

    private LegalTermsDescriptor createLegalTermsDescriptor(String name, String description, LegalTermsContent... content) {
        LegalTermsDescriptor descriptor = EntityFactory.create(LegalTermsDescriptor.class);
        descriptor.name().setValue(name);
        descriptor.description().setValue(description);
        for (LegalTermsContent c : content) {
            descriptor.content().add(c);
        }
        return descriptor;
    }

    private LegalTermsContent createLegalTermsContent(String caption, AvailableLocale locale, String contentText) {
        LegalTermsContent content = EntityFactory.create(LegalTermsContent.class);
        content.locale().set(locale);
        content.localizedCaption().setValue(caption);
        content.content().setValue(contentText);
        return content;
    }

}
