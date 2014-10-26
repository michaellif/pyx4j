/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.tenant.insurance.TenantInsuranceFacade;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionStatus;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionType;

class MoveInManager {

    Collection<PortalResidentBehavior> getMoveInBehaviors(Lease leaseId, LeaseTermParticipant<?> termParticipant,
            Collection<PortalResidentBehavior> leaseBehaviors) {
        Collection<PortalResidentBehavior> behaviors = new HashSet<>();

        if (leaseBehaviors.contains(PortalResidentBehavior.LeaseAgreementSigningRequired)) {
            behaviors.add(PortalResidentBehavior.MoveInWizardCompletionRequired);
        } else {
            Map<MoveInActionType, LeaseParticipantMoveInAction> moveInActionsByType = getMoveInActionsByType(termParticipant.leaseParticipant());

            // If this grows bigger, change it to call to getActiveMoveInActions

            if (!leaseBehaviors.contains(PortalResidentBehavior.AutopayAgreementPresent)) {
                LeaseParticipantMoveInAction a = getActiveMoveInAction(moveInActionsByType, MoveInActionType.autoPay);
                if (a != null) {
                    behaviors.add(PortalResidentBehavior.MoveInWizardCompletionRequired);
                }
            }

            if (!leaseBehaviors.contains(PortalResidentBehavior.InsurancePresent)) {
                LeaseParticipantMoveInAction a = getActiveMoveInAction(moveInActionsByType, MoveInActionType.insurance);
                if (a != null) {
                    behaviors.add(PortalResidentBehavior.MoveInWizardCompletionRequired);
                }
            }

        }

        return behaviors;
    }

    private LeaseParticipantMoveInAction getActiveMoveInAction(Map<MoveInActionType, LeaseParticipantMoveInAction> moveInActionsByType,
            MoveInActionType moveInActionType) {
        LeaseParticipantMoveInAction a = getMoveInAction(moveInActionsByType, moveInActionType);
        if (a.status().getValue() != null) {
            return null;
        } else {
            return a;
        }
    }

    private LeaseParticipantMoveInAction getMoveInAction(Map<MoveInActionType, LeaseParticipantMoveInAction> moveInActionsByType,
            MoveInActionType moveInActionType) {
        LeaseParticipantMoveInAction a = moveInActionsByType.get(moveInActionType);
        if (a == null) {
            a = EntityFactory.create(LeaseParticipantMoveInAction.class);
            a.type().setValue(moveInActionType);
            a.status().setValue(null);
        }
        return a;
    }

    Collection<LeaseParticipantMoveInAction> getMoveInActions(LeaseParticipant<?> leaseParticipant) {
        Collection<LeaseParticipantMoveInAction> r = new ArrayList<>();
        Map<MoveInActionType, LeaseParticipantMoveInAction> moveInActionsByType = getMoveInActionsByType(leaseParticipant);

        if (!leaseParticipant.isAssignableFrom(Guarantor.class)) {
            LeaseParticipantMoveInAction a = getMoveInAction(moveInActionsByType, MoveInActionType.autoPay);
            if (ServerSideFactory.create(PaymentMethodFacade.class).isAutopayAgreementsPresent(leaseParticipant.lease())) {
                a.status().setValue(MoveInActionStatus.completed);
            }
            r.add(a);
        }

        {
            LeaseParticipantMoveInAction a = getMoveInAction(moveInActionsByType, MoveInActionType.insurance);
            if (ServerSideFactory.create(TenantInsuranceFacade.class).isInsurancePresent(leaseParticipant.lease())) {
                a.status().setValue(MoveInActionStatus.completed);
            }
            r.add(a);
        }

        return r;
    }

    void skipMoveInAction(LeaseParticipant<?> leaseParticipant, MoveInActionType moveInActionType) {
        Map<MoveInActionType, LeaseParticipantMoveInAction> moveInActionsByType = getMoveInActionsByType(leaseParticipant);
        LeaseParticipantMoveInAction a = moveInActionsByType.get(moveInActionsByType);
        if (a == null) {
            a = EntityFactory.create(LeaseParticipantMoveInAction.class);
            a.leaseParticipant().set(leaseParticipant);
            a.type().setValue(moveInActionType);
        }
        a.status().setValue(MoveInActionStatus.doItLater);
        Persistence.service().persist(a);
    }

    private Map<MoveInActionType, LeaseParticipantMoveInAction> getMoveInActionsByType(LeaseParticipant<?> leaseParticipant) {
        Persistence.ensureRetrieve(leaseParticipant.moveInActions(), AttachLevel.Attached);
        Map<MoveInActionType, LeaseParticipantMoveInAction> m = new HashMap<>();
        for (LeaseParticipantMoveInAction a : leaseParticipant.moveInActions()) {
            m.put(a.type().getValue(), a);
        }
        return m;
    }
}
