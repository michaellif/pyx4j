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

import com.propertyvista.domain.policy.policies.LeaseApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LeaseApplicationPolicyPreloader extends AbstractPolicyPreloader<LeaseApplicationLegalPolicy> {

    public LeaseApplicationPolicyPreloader() {
        super(LeaseApplicationLegalPolicy.class);
    }

    @Override
    protected LeaseApplicationLegalPolicy createPolicy(StringBuilder log) {
        LeaseApplicationLegalPolicy policy = EntityFactory.create(LeaseApplicationLegalPolicy.class);

        // add legal terms
        policy.legalTerms().add(
                createLegalTerm("Conditions of Acceptance of a Lease", "leaseApplicationTerm1.html", TargetRole.Applicant, SignatureFormat.AgreeBox));
        policy.legalTerms().add(createLegalTerm("Consent to Lease", "leaseApplicationTerm2.html", TargetRole.Applicant, SignatureFormat.AgreeBox));

        // add confirmation terms
        policy.confirmationTerms().add(createConfTerm("Privacy Policy", "leaseApplicationConfirm1.html", TargetRole.Applicant, SignatureFormat.AgreeBox));
        policy.confirmationTerms().add(createConfTerm("Digital Signature", "leaseApplicationConfirm2.html", TargetRole.Applicant, SignatureFormat.FullName));

        return policy;
    }

    public LeaseApplicationLegalTerm createLegalTerm(String caption, String termsSourceFile, TargetRole role, SignatureFormat format) {

        String termsContent = getTermsContent(termsSourceFile);

        LeaseApplicationLegalTerm term = EntityFactory.create(LeaseApplicationLegalTerm.class);
        term.applyToRole().setValue(role);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private LeaseApplicationConfirmationTerm createConfTerm(String caption, String termsSourceFile, TargetRole role, SignatureFormat format) {
        String termsContent = getTermsContent(termsSourceFile);

        LeaseApplicationConfirmationTerm term = EntityFactory.create(LeaseApplicationConfirmationTerm.class);
        term.applyToRole().setValue(role);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private String getTermsContent(String termsSourceFile) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, LeaseApplicationPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found.");
        }
        return termsContent;
    }
}
