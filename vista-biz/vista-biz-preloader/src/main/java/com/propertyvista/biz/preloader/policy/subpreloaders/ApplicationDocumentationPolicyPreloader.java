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
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationDocumentType.Importance;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Type;
import com.propertyvista.domain.policy.policies.domain.ProofOfAssetDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfEmploymentDocumentType;
import com.propertyvista.domain.policy.policies.domain.ProofOfIncomeDocumentType;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset.AssetType;
import com.propertyvista.domain.tenant.income.IncomeSource;

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

        return updatePolicy(policy);
    }

    public ApplicationDocumentationPolicy updatePolicy(ApplicationDocumentationPolicy policy) {

        policy.mandatoryProofOfEmployment().setValue(true);
        {
            ProofOfEmploymentDocumentType poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
            poe.importance().setValue(Importance.Optional);
            poe.incomeSource().setValue(IncomeSource.fulltime);
            poe.notes().setValue(
                    i18n.tr("Letter of Employment (salary or hourly wage and hours worked per week, and length of employment) and Pay Stub are required"));
            policy.allowedEmploymentDocuments().add(poe);

            poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
            poe.importance().setValue(Importance.Optional);
            poe.incomeSource().setValue(IncomeSource.parttime);
            poe.notes().setValue(
                    i18n.tr("Letter of Employment (salary or hourly wage and hours worked per week, and length of employment) and Pay Stub are required"));
            policy.allowedEmploymentDocuments().add(poe);

            poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
            poe.importance().setValue(Importance.Optional);
            poe.incomeSource().setValue(IncomeSource.seasonallyEmployed);
            poe.notes().setValue(i18n.tr("Last Letter of Employment is required"));
            policy.allowedEmploymentDocuments().add(poe);

            poe = EntityFactory.create(ProofOfEmploymentDocumentType.class);
            poe.importance().setValue(Importance.Optional);
            poe.incomeSource().setValue(IncomeSource.selfemployed);
            poe.notes().setValue(i18n.tr("Most recent Tax Assessment is required"));
            policy.allowedEmploymentDocuments().add(poe);
        }

        // ---------------------------------------------------------

        policy.mandatoryProofOfIncome().setValue(false);
        {
            ProofOfIncomeDocumentType poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
            poi.importance().setValue(Importance.Optional);
            poi.incomeSource().setValue(IncomeSource.pension);
            poi.notes().setValue(i18n.tr("Pension Confirmation is required"));
            policy.allowedIncomeDocuments().add(poi);

            poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
            poi.importance().setValue(Importance.Optional);
            poi.incomeSource().setValue(IncomeSource.socialServices);
            poi.notes().setValue(i18n.tr("Social Assistance Confirmation is required"));
            policy.allowedIncomeDocuments().add(poi);

            poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
            poi.importance().setValue(Importance.Optional);
            poi.incomeSource().setValue(IncomeSource.disabilitySupport);
            poi.notes().setValue(i18n.tr("Benefit Statement is required"));
            policy.allowedIncomeDocuments().add(poi);

            poi = EntityFactory.create(ProofOfIncomeDocumentType.class);
            poi.importance().setValue(Importance.Optional);
            poi.incomeSource().setValue(IncomeSource.student);
            poi.notes().setValue(i18n.tr("Student Loan Confirmation is required"));
            policy.allowedIncomeDocuments().add(poi);
        }

        // ---------------------------------------------------------

        policy.mandatoryProofOfAsset().setValue(false);
        {
            ProofOfAssetDocumentType poa = EntityFactory.create(ProofOfAssetDocumentType.class);
            poa.importance().setValue(Importance.Optional);
            poa.assetType().setValue(AssetType.bankAccounts);
            poa.notes().setValue(i18n.tr("Bank Statement is required"));
            policy.allowedAssetDocuments().add(poa);

            poa = EntityFactory.create(ProofOfAssetDocumentType.class);
            poa.importance().setValue(Importance.Optional);
            poa.assetType().setValue(AssetType.businesses);
            poa.notes().setValue(i18n.tr("Business registration is required"));
            policy.allowedAssetDocuments().add(poa);
        }

        return policy;
    }
}
