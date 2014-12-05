/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class LeaseParticipantUtils {

    public static LeaseParticipantScreeningTO getCustomerScreening(LeaseParticipant<?> participant, boolean forEdit) {
        CustomerScreening screening;

        if (forEdit) {
            PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(participant.lease());
            screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftForEdit(participant.customer(), policyNode);
            ServerSideFactory.create(ScreeningFacade.class).registerUploadedDocuments(screening);
        } else {
            screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningDraftOrFinal(participant.customer(), AttachLevel.Attached);
            if (screening == null) { // newly created tenant:
                screening = EntityFactory.create(CustomerScreening.class);
                screening.screene().set(participant.customer());
            }
        }

        Persistence.ensureRetrieve(screening.version().incomes(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().assets(), AttachLevel.Attached);
        Persistence.ensureRetrieve(screening.version().documents(), AttachLevel.Attached);
        Persistence.service().retrieve(screening.screene(), AttachLevel.ToStringMembers, false);

        return createScreeningTO(participant, screening, forEdit);
    }

    public static LeaseParticipantScreeningTO createScreeningTO(LeaseParticipant<?> participant, CustomerScreening screening, boolean forEdit) {
        LeaseParticipantScreeningTO to = EntityFactory.create(LeaseParticipantScreeningTO.class);

        to.leaseParticipantId().set(participant.<LeaseParticipant<?>> createIdentityStub());
        Persistence.ensureRetrieve(participant.lease(), AttachLevel.Attached);
        to.leaseStatus().setValue(participant.lease().status().getValue());
        to.data().set(screening);

        if (to.data().getPrimaryKey() != null) {
            to.setPrimaryKey(new Key(participant.getPrimaryKey().asLong(), screening.getPrimaryKey().getVersion()));
        }

        if (forEdit) {
            loadRestrictions(to, participant);
        }
        return to;
    }

    public static void persistScreeningAsDraft(CustomerScreening screening) {
        screening.saveAction().setValue(SaveAction.saveAsDraft);
        persistScreening(screening);
    }

    public static void persistScreeningAsNewVersion(CustomerScreening screening) {
//        screening = VersionedEntityUtils.createNextVersion(screening);/
        screening.saveAction().setValue(SaveAction.saveAsFinal);
        persistScreening(screening);
    }

    private static void persistScreening(CustomerScreening screening) {

        for (IdentificationDocument item : screening.version().documents()) {
            setVerification(item.files());
        }

        for (CustomerScreeningIncome item : screening.version().incomes()) {
            setVerification(item.files());
        }

        for (CustomerScreeningAsset item : screening.version().assets()) {
            setVerification(item.files());
        }

        Persistence.secureSave(screening);
    }

    private static <F extends ApplicationDocumentFile<?>> void setVerification(List<F> files) {
        for (F file : files) {
            if (file.verified().getValue(false) && file.verifiedBy().isNull()) {
                file.verifiedBy().set(CrmAppContext.getCurrentUserEmployee());
                file.verifiedOn().setValue(SystemDateManager.getLogicalDate());
            }
        }
    }

    private static void loadRestrictions(LeaseParticipantScreeningTO to, LeaseParticipant<?> participant) {
        PolicyNode policyNode = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(participant.lease());
        RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, RestrictionsPolicy.class);

        to.yearsToForcingPreviousAddress().setValue(restrictionsPolicy.yearsToForcingPreviousAddress().getValue());
    }

    // --------------------------------------------------------------------------------------------------------------------------------

    public static LeaseParticipantScreeningTO getCustomerScreeningPointer(LeaseParticipant<?> participant) {
        return createScreeningPointer(participant,
                ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(participant.customer(), AttachLevel.ToStringMembers));
    }

    public static LeaseParticipantScreeningTO createScreeningPointer(LeaseParticipant<?> participant, CustomerScreening screening) {
        LeaseParticipantScreeningTO to = EntityFactory.create(LeaseParticipantScreeningTO.class);
        to.data().set(screening);
        if (to.data().getPrimaryKey() != null) {
            to.setPrimaryKey(new Key(participant.getPrimaryKey().asLong(), screening.getPrimaryKey().getVersion()));
            to.data().setAttachLevel(AttachLevel.ToStringMembers);
        }
        return to;
    }

    public static LeaseParticipantScreeningTO getLeaseTermEffectiveScreeningPointer(Lease lease, LeaseTermParticipant<?> termParticipant) {
        if (isApplicationInPogress(lease, termParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            return getCustomerScreeningPointer(termParticipant.leaseParticipant());
        } else {
            if (!termParticipant.screening().isNull()) {
                CustomerScreening screening = termParticipant.screening().duplicate();
                Persistence.service().retrieve(screening, AttachLevel.ToStringMembers, false);
                return createScreeningPointer(termParticipant.leaseParticipant(), screening);
            } else {
                return null;
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------

    public static CustomerScreening retrieveLeaseTermEffectiveScreening(Lease lease, LeaseTermParticipant<?> termParticipant) {
        CustomerScreening screening;
        if (isApplicationInPogress(lease, termParticipant.leaseTermV())) {
            // Take customer's Screening, Prefers draft version.
            screening = ServerSideFactory.create(ScreeningFacade.class).retrivePersonScreeningFinalOrDraft(termParticipant.leaseParticipant().customer(),
                    AttachLevel.Attached);
        } else {
            screening = termParticipant.screening();
            if (!screening.isNull()) {
                Persistence.service().retrieve(screening, AttachLevel.Attached, false);
            }

        }
        if ((screening != null) && !screening.isNull()) {
            Persistence.service().retrieve(screening.version().incomes());
            Persistence.service().retrieve(screening.version().assets());
            Persistence.service().retrieve(screening.version().documents());
        }
        return screening;
    }

    public static boolean isApplicationInPogress(Lease lease, LeaseTermV leaseTermV) {
        return (lease.status().getValue() != Lease.Status.Cancelled) && VersionedEntityUtils.isDraft(leaseTermV) && lease.status().getValue().isDraft();
    }
}
