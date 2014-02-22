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

import com.propertyvista.domain.policy.policies.LeaseAgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LeaseAgreementPolicyPreloader extends AbstractPolicyPreloader<LeaseAgreementLegalPolicy> {

    public LeaseAgreementPolicyPreloader() {
        super(LeaseAgreementLegalPolicy.class);
    }

    @Override
    protected LeaseAgreementLegalPolicy createPolicy(StringBuilder log) {
        LeaseAgreementLegalPolicy policy = EntityFactory.create(LeaseAgreementLegalPolicy.class);

        // add legal terms
        policy.legal().add(createTerm("Conditions of Acceptance of a Lease", "leaseAgreementTerm1.html", TargetRole.Applicant, SignatureFormat.AgreeBox));
        policy.legal().add(createTerm("Consent to Lease", "leaseAgreementTerm2.html", TargetRole.Applicant, SignatureFormat.AgreeBox));

        // add confirmation terms
        policy.confirmation().add(createTerm("Privacy Policy", "leaseAgreementConfirm1.html", SignatureFormat.AgreeBox));
        policy.confirmation().add(createTerm("Digital Signature", "leaseAgreementConfirm2.html", SignatureFormat.FullName));

        return policy;
    }

    private LeaseAgreementLegalTerm createTerm(String caption, String termsSourceFile, TargetRole role, SignatureFormat format) {
        String termsContent = getTermsContent(termsSourceFile);

        LeaseAgreementLegalTerm term = EntityFactory.create(LeaseAgreementLegalTerm.class);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private LeaseAgreementConfirmationTerm createTerm(String caption, String termsSourceFile, SignatureFormat format) {
        String termsContent = getTermsContent(termsSourceFile);

        LeaseAgreementConfirmationTerm term = EntityFactory.create(LeaseAgreementConfirmationTerm.class);
        term.title().setValue(caption);
        term.body().setValue(termsContent);
        term.signatureFormat().setValue(format);
        return term;
    }

    private String getTermsContent(String termsSourceFile) {
        String termsContent;
        try {
            termsContent = IOUtils.getUTF8TextResource(termsSourceFile, LeaseAgreementPolicyPreloader.class);
        } catch (IOException e) {
            throw new Error(e);
        }
        if (termsContent == null) {
            throw new Error("Resource " + termsSourceFile + " not found.");
        }
        return termsContent;
    }
}
