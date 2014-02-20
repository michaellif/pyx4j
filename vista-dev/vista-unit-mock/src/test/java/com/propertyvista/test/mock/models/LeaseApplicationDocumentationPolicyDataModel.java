/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseApplicationDocumentationPolicyDataModel extends MockDataModel<ApplicationDocumentationPolicy> {

    @Override
    protected void generate() {
        ApplicationDocumentationPolicy policy = EntityFactory.create(ApplicationDocumentationPolicy.class);

        policy.numberOfRequiredIDs().setValue(0);

        IdentificationDocumentType id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue("SIN");
        id.required().setValue(true);
        id.type().setValue(Type.canadianSIN);
        policy.allowedIDs().add(id);

        policy.mandatoryProofOfIncome().setValue(false);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);

    }

}
