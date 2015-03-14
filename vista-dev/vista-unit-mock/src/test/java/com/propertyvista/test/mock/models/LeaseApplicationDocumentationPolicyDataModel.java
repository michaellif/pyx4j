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
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationDocumentType.Importance;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfEmploymentDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfIncomeDocumentType;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset.AssetType;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseApplicationDocumentationPolicyDataModel extends MockDataModel<ApplicationDocumentationPolicy> {

    @Override
    protected void generate() {
        ApplicationDocumentationPolicy policy = EntityFactory.create(ApplicationDocumentationPolicy.class);

        policy.numberOfRequiredIDs().setValue(0);

        IdentificationDocumentType id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue("SIN");
        id.importance().setValue(Importance.Required);
        id.type().setValue(Type.canadianSIN);
        policy.allowedIDs().add(id);

        // ---------------------------------------------------------

        policy.mandatoryProofOfEmployment().setValue(true);
        {
            ProofOfEmploymentDocumentType poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
            poe.importance().setValue(Importance.Optional);
            poe.incomeSource().setValue(IncomeSource.fulltime);
            poe.notes().setValue("Letter of Employment and Pay Stub are required");
            policy.allowedEmploymentDocuments().add(poe);
        }

        // ---------------------------------------------------------

        policy.mandatoryProofOfIncome().setValue(false);
        {
            ProofOfIncomeDocumentType poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
            poi.importance().setValue(Importance.Optional);
            poi.incomeSource().setValue(IncomeSource.pension);
            poi.notes().setValue("Pension Confirmation is required");
            policy.allowedIncomeDocuments().add(poi);
        }

        // ---------------------------------------------------------

        policy.mandatoryProofOfAsset().setValue(false);
        {
            ProofOfAssetDocumentType poa = EntityFactory.create(ProofOfAssetDocumentType.class);
            poa.importance().setValue(Importance.Optional);
            poa.assetType().setValue(AssetType.bankAccounts);
            poa.notes().setValue("Bank Statement (for every account) is required");
            policy.allowedAssetDocuments().add(poa);
        }

        // ---------------------------------------------------------

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }
}
