/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;

import com.propertyvista.domain.policy.policies.AgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;
import com.propertyvista.test.mock.MockDataModel;

public class AgreementLegalPolicyDataModel extends MockDataModel<AgreementLegalPolicy> {

    public AgreementLegalPolicyDataModel() {
    }

    @Override
    protected void generate() {

        AgreementLegalPolicy policy = EntityFactory.create(AgreementLegalPolicy.class);

        policy.terms().add(createTerm(SignatureFormat.AgreeBox));

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

    private AgreementLegalTerm createTerm(SignatureFormat format) {
        AgreementLegalTerm term = EntityFactory.create(AgreementLegalTerm.class);

        term.signatureFormat().setValue(format);
        term.title().setValue("Ut ut pellentesque nulla.");
        term.body().setValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In accumsan aliquam tellus at congue.");

        return term;
    }
}
