/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.io.IOException;

import com.propertvista.generator.BuildingsGenerator;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;
import com.propertyvista.shared.CompiledLocale;

public class LeaseTermsPolicyPreloader extends AbstractPolicyPreloader<LeaseTermsPolicy> {

    private final static I18n i18n = I18n.get(LeaseTermsPolicyPreloader.class);

    public LeaseTermsPolicyPreloader() {
        super(LeaseTermsPolicy.class);
    }

    private LeaseTermsPolicy createDefaultLeaseTermsPolicy() {
        LeaseTermsPolicy policy = EntityFactory.create(LeaseTermsPolicy.class);

        String termsContentText = "failed to get lease terms during the preload phase";
        try {
            termsContentText = IOUtils.getTextResource("leaseTerms.html", BuildingsGenerator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Find Local if exists
        EntityQueryCriteria<AvailableLocale> criteria = EntityQueryCriteria.create(AvailableLocale.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lang(), CompiledLocale.en));
        AvailableLocale en = Persistence.service().retrieve(criteria);
        if (en == null) {
            en = EntityFactory.create(AvailableLocale.class);
            en.lang().setValue(CompiledLocale.en);
            Persistence.service().persist(en);
        }

        String caption = i18n.tr("Mockup Summary Terms");
        policy.tenantSummaryTerms().add(createLegalTermsDescriptor(caption, "", createLegalTermsContent(caption, en, termsContentText)));

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

    @Override
    protected LeaseTermsPolicy createPolicy(StringBuilder log) {
        return createDefaultLeaseTermsPolicy();
    }

}
