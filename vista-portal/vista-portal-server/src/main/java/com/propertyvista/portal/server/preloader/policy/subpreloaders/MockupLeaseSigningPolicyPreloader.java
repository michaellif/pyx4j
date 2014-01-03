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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;

import com.propertyvista.domain.policy.policies.AgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MockupLeaseSigningPolicyPreloader extends AbstractPolicyPreloader<AgreementLegalPolicy> {

    public MockupLeaseSigningPolicyPreloader() {
        super(AgreementLegalPolicy.class);
    }

    @Override
    protected AgreementLegalPolicy createPolicy(StringBuilder log) {
        AgreementLegalPolicy policy = EntityFactory.create(AgreementLegalPolicy.class);

        // add legal terms
        policy.terms().add(createTerm(SignatureFormat.None));
        policy.terms().add(createTerm(SignatureFormat.AgreeBox));
        policy.terms().add(createTerm(SignatureFormat.AgreeBoxAndFullName));
        policy.terms().add(createTerm(SignatureFormat.FullName));
        policy.terms().add(createTerm(SignatureFormat.Initials));

        return policy;
    }

    private AgreementLegalTerm createTerm(SignatureFormat format) {
        AgreementLegalTerm term = EntityFactory.create(AgreementLegalTerm.class);

        term.signatureFormat().setValue(format);
        term.title().setValue(CommonsGenerator.lipsumShort());
        term.body().setValue(CommonsGenerator.lipsum() + " <i>" + CommonsGenerator.lipsumShort() + "</i> " + CommonsGenerator.lipsum());

        return term;
    }
}
