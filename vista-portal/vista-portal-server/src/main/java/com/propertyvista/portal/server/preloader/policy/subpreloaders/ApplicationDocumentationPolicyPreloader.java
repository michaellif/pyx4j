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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationDocumentType.Importance;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfEmploymentDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfIncomeDocumentType;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ApplicationDocumentationPolicyPreloader extends AbstractPolicyPreloader<ApplicationDocumentationPolicy> {

    public ApplicationDocumentationPolicyPreloader() {
        super(ApplicationDocumentationPolicy.class);
    }

    private final static I18n i18n = I18n.get(ApplicationDocumentationPolicyPreloader.class);

    @Override
    protected ApplicationDocumentationPolicy createPolicy(StringBuilder log) {
        ApplicationDocumentationPolicy policy = EntityFactory.create(ApplicationDocumentationPolicy.class);
        policy.numberOfRequiredIDs().setValue(2);

        IdentificationDocumentType id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("SIN"));
        id.type().setValue(Type.canadianSIN);
        id.importance().setValue(Importance.Required);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Passport"));
        id.type().setValue(Type.passport);
        id.importance().setValue(Importance.Optional);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Citizenship Card"));
        id.type().setValue(Type.citizenship);
        id.importance().setValue(Importance.Optional);
        policy.allowedIDs().add(id);

        id = EntityFactory.create(IdentificationDocumentType.class);
        id.name().setValue(i18n.tr("Driver License"));
        id.type().setValue(Type.license);
        id.importance().setValue(Importance.Optional);
        policy.allowedIDs().add(id);

        // ---------------------------------------------------------

        policy.mandatoryProofOfEmployment().setValue(true);
        policy.numberOfEmploymentDocuments().setValue(2);

        ProofOfEmploymentDocumentType poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
        poe.name().setValue(i18n.tr("Letter of Employment"));
        poe.importance().setValue(Importance.Required);
        policy.allowedEmploymentDocuments().add(poe);

        poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
        poe.name().setValue(i18n.tr("Pay Stub"));
        poe.importance().setValue(Importance.Preferred);
        policy.allowedEmploymentDocuments().add(poe);

        // ---------------------------------------------------------

        policy.mandatoryProofOfIncome().setValue(false);
        policy.numberOfIncomeDocuments().setValue(2);

        ProofOfIncomeDocumentType poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
        poi.name().setValue(i18n.tr("Pension Confirmation"));
        poi.importance().setValue(Importance.Required);
        policy.allowedIncomeDocuments().add(poi);

        poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
        poi.name().setValue(i18n.tr("Social Assistance Confirmation"));
        poi.importance().setValue(Importance.Preferred);
        policy.allowedIncomeDocuments().add(poi);

        // ---------------------------------------------------------

        policy.mandatoryProofOfAsset().setValue(false);
        policy.numberOfAssetDocuments().setValue(1);

        ProofOfAssetDocumentType poa = EntityFactory.create(ProofOfAssetDocumentType.class);
        poa.name().setValue(i18n.tr("Bank Statement"));
        poa.importance().setValue(Importance.Preferred);
        policy.allowedAssetDocuments().add(poa);

        poa = EntityFactory.create(ProofOfAssetDocumentType.class);
        poa.name().setValue(i18n.tr("Benefit Statement"));
        poa.importance().setValue(Importance.Optional);
        policy.allowedAssetDocuments().add(poa);

        return policy;
    }
}
