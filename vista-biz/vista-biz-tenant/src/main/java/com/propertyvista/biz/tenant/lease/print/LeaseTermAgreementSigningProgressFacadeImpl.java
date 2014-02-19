/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2014
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;
import com.propertyvista.domain.tenant.lease.AgreementDigitalSignatures;
import com.propertyvista.domain.tenant.lease.AgreementInkSignatures;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;
import com.propertyvista.dto.LeaseAgreementSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO.SignatureType;

public class LeaseTermAgreementSigningProgressFacadeImpl implements LeaseTermAgreementSigningProgressFacade {

    private static final I18n i18n = I18n.get(LeaseTermAgreementSigningProgressFacadeImpl.class);

    @Override
    public boolean shouldSign(LeaseTermParticipant<?> participant) {
        return participant.role().getValue() == Role.Applicant || participant.role().getValue() == Role.CoApplicant
                || participant.role().getValue() == Role.Guarantor;
    }

    @Override
    public LeaseAgreementSigningProgressDTO getSigningProgress(Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.currentTerm(), AttachLevel.Attached);

        List<LeaseTermParticipant<?>> stakeholderParticipants = getStakeholderParticipants(lease.currentTerm());

        LeaseAgreementSigningProgressDTO progress = EntityFactory.create(LeaseAgreementSigningProgressDTO.class);

        for (LeaseTermParticipant<?> participant : stakeholderParticipants) {
            Persistence.ensureRetrieve(participant.agreementSignatures(), AttachLevel.Attached);

            LeaseAgreementStakeholderSigningProgressDTO stakeholdersProgress = EntityFactory.create(LeaseAgreementStakeholderSigningProgressDTO.class);
            stakeholdersProgress.stakeholderLeaseParticipant().set(participant);

            stakeholdersProgress.name().setValue(participant.leaseParticipant().customer().person().name().getStringView());
            stakeholdersProgress.role().setValue(participant.role().getStringView());

            boolean hasSigned = true;
            Iterator<LeaseAgreementLegalTerm> legalTerms = lease.currentTerm().version().agreementLegalTerms().iterator();

            while (hasSigned && legalTerms.hasNext()) {
                LeaseAgreementLegalTerm legalTerm = legalTerms.next();
                if (legalTerm.signatureFormat().getValue() != SignatureFormat.None) {
                    if (!participant.agreementSignatures().isNull()
                            && participant.agreementSignatures().getInstanceValueClass().equals(AgreementInkSignatures.class)) {
                        stakeholdersProgress.singatureType().setValue(SignatureType.Ink);
                    } else if (participant.agreementSignatures().getInstanceValueClass().equals(AgreementDigitalSignatures.class)) {
                        stakeholdersProgress.singatureType().setValue(SignatureType.Digital);
                        AgreementDigitalSignatures signatures = participant.agreementSignatures().duplicate(AgreementDigitalSignatures.class);

                        boolean foundSignedTerm = false;
                        for (SignedAgreementLegalTerm signedTerm : signatures.legalTermsSignatures()) {
                            if (signedTerm.term().getPrimaryKey().equals(legalTerm.getPrimaryKey())) {
                                foundSignedTerm = true;
                                break;
                            }
                        }
                        if (!foundSignedTerm) {
                            hasSigned = false;
                            break;
                        }
                    } else {
                        hasSigned = false;
                        break;
                    }
                }
            }
            if (!lease.currentTerm().version().agreementLegalTerms().isEmpty()) {
                stakeholdersProgress.hasSigned().setValue(hasSigned);
            }
            progress.stackholdersProgressBreakdown().add(stakeholdersProgress);
        }

        if (!lease.currentTerm().version().employeeSignature().isEmpty()) {
            LeaseAgreementStakeholderSigningProgressDTO landlordsProgress = EntityFactory.create(LeaseAgreementStakeholderSigningProgressDTO.class);
            landlordsProgress.stakeholderUser().set(lease.currentTerm().version().employeeSignature().signingUser());
            landlordsProgress.name().setValue(lease.currentTerm().version().employeeSignature().signingUser().name().getStringView());
            landlordsProgress.role().setValue(i18n.tr("Landlord"));
            landlordsProgress.hasSigned().setValue(true);
            landlordsProgress.singatureType().setValue(SignatureType.Digital);
            progress.stackholdersProgressBreakdown().add(landlordsProgress);
        }

        return progress;
    }

    private List<LeaseTermParticipant<?>> getStakeholderParticipants(LeaseTerm leaseTerm) {
        List<LeaseTermParticipant<?>> stakeholderParticipants = new ArrayList<>();
        Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);

        stakeholderParticipants.addAll(leaseTerm.version().tenants());
        stakeholderParticipants.addAll(leaseTerm.version().guarantors());
        Iterator<LeaseTermParticipant<?>> i = stakeholderParticipants.iterator();
        while (i.hasNext()) {
            LeaseTermParticipant<?> participant = i.next();
            Persistence.ensureRetrieve(participant.leaseParticipant(), AttachLevel.Attached);
            if (!shouldSign(participant)) {
                i.remove();
            }
        }
        return stakeholderParticipants;
    }

}
