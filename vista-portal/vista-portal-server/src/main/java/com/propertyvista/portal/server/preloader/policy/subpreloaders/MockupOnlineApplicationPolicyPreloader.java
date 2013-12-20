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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureType;

import com.propertyvista.domain.policy.policies.OnlineApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm.TargetRole;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MockupOnlineApplicationPolicyPreloader extends AbstractPolicyPreloader<OnlineApplicationLegalPolicy> {

    public MockupOnlineApplicationPolicyPreloader() {
        super(OnlineApplicationLegalPolicy.class);
    }

    @Override
    protected OnlineApplicationLegalPolicy createPolicy(StringBuilder log) {
        OnlineApplicationLegalPolicy policy = EntityFactory.create(OnlineApplicationLegalPolicy.class);

        // add legal terms
        policy.terms().add(createTerm(SignatureType.None));
        policy.terms().add(createTerm(SignatureType.AgreeBox));
        policy.terms().add(createTerm(SignatureType.AgreeBoxAndFullName));
        policy.terms().add(createTerm(SignatureType.FullName));
        policy.terms().add(createTerm(SignatureType.Initials));

        return policy;
    }

    private OnlineApplicationLegalTerm createTerm(SignatureType type) {
        OnlineApplicationLegalTerm term = EntityFactory.create(OnlineApplicationLegalTerm.class);

        term.signatureType().setValue(type);
        term.applyToRole().setValue(TargetRole.Any);
        term.title().setValue(CommonsGenerator.lipsumShort());
        term.body().setValue(CommonsGenerator.lipsum() + " <i>" + CommonsGenerator.lipsumShort() + "</i>" + CommonsGenerator.lipsum());

        return term;
    }

}
