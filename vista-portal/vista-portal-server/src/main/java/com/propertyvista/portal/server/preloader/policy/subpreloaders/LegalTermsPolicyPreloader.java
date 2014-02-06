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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsPolicyItem;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LegalTermsPolicyPreloader extends AbstractPolicyPreloader<LegalTermsPolicy> {

    public LegalTermsPolicyPreloader() {
        super(LegalTermsPolicy.class);
    }

    @Override
    protected LegalTermsPolicy createPolicy(StringBuilder log) {
        LegalTermsPolicy policy = EntityFactory.create(LegalTermsPolicy.class);
        policy.residentPortalTermsAndConditions().set(
                createLegalTermsPolicyItem("RESIDENT PORTAL TERMS AND CONDITIONS", "ResidentPortalTermsAndConditionsCrm.html"));
        policy.residentPortalPrivacyPolicy().set(createLegalTermsPolicyItem("RESIDENT PORTAL PRIVACY POLICY", "ResidentPortalPrivacyPolicyCrm.html"));
        policy.prospectPortalTermsAndConditions().set(
                createLegalTermsPolicyItem("ONLINE APPLICATION TERMS AND CONDITIONS", "ProspectPortalTermsAndConditionsCrm.html"));
        policy.prospectPortalPrivacyPolicy().set(createLegalTermsPolicyItem("ONLINE APPLICATION PRIVACY POLICY", "ProspectPortalPrivacyPolicyCrm.html"));
        return policy;
    }

    public LegalTermsPolicyItem createLegalTermsPolicyItem(String caption, String termsSourceFile) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, LegalTermsPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found to create " + caption);
        }

        LegalTermsPolicyItem legalDocument = EntityFactory.create(LegalTermsPolicyItem.class);
        legalDocument.enabled().setValue(true);
        legalDocument.caption().setValue(caption);
        legalDocument.content().setValue(termsContent);
        return legalDocument;
    }

}
