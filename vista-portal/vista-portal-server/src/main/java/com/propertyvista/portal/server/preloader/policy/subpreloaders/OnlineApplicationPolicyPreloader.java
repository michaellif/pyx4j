/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.io.IOException;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.policy.policies.OnlineApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm.TargetRole;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class OnlineApplicationPolicyPreloader extends AbstractPolicyPreloader<OnlineApplicationLegalPolicy> {

    public OnlineApplicationPolicyPreloader() {
        super(OnlineApplicationLegalPolicy.class);
    }

    @Override
    protected OnlineApplicationLegalPolicy createPolicy(StringBuilder log) {
        OnlineApplicationLegalPolicy policy = EntityFactory.create(OnlineApplicationLegalPolicy.class);

        // add legal terms
        policy.legalTerms().add(
                createTerm("Conditions of Acceptance of a Lease", "onlineApplicationTerm1.html", TargetRole.Applicant, SignatureFormat.AgreeBox,
                        OnlineApplicationLegalTerm.class));
        policy.legalTerms()
                .add(createTerm("Consent to Lease", "onlineApplicationTerm2.html", TargetRole.Applicant, SignatureFormat.AgreeBox,
                        OnlineApplicationLegalTerm.class));

        // add confirmation terms
        policy.confirmationTerms().add(
                createTerm("Privacy Policy", "onlineApplicationTerm3.html", TargetRole.Applicant, SignatureFormat.AgreeBox,
                        OnlineApplicationConfirmationTerm.class));
        policy.confirmationTerms().add(
                createTerm("Digital Signature", "onlineApplicationTerm4.html", TargetRole.Applicant, SignatureFormat.AgreeBox,
                        OnlineApplicationConfirmationTerm.class));

        return policy;
    }

    public <T extends OnlineApplicationLegalTerm> T createTerm(String caption, String termsSourceFile, TargetRole role, SignatureFormat format,
            Class<T> termClass) {

        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, LegalTermsPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found to create " + caption);
        }

        T term = EntityFactory.create(termClass);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.applyToRole().setValue(role);
        term.signatureFormat().setValue(format);
        return term;
    }
}
