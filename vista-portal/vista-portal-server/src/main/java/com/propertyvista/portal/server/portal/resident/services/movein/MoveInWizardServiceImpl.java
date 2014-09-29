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
package com.propertyvista.portal.server.portal.resident.services.movein;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionType;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStatusTO;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepStatusTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class MoveInWizardServiceImpl implements MoveInWizardService {

    @Override
    public void obtainIncompleteSteps(AsyncCallback<MoveInWizardStatusTO> callback) {
        MoveInWizardStatusTO wizardStatus = EntityFactory.create(MoveInWizardStatusTO.class);

        if (SecurityController.check(PortalResidentBehavior.LeaseAgreementSigningRequired)) {
            MoveInWizardStepStatusTO stepStatus = EntityFactory.create(MoveInWizardStepStatusTO.class);
            stepStatus.step().setValue(MoveInWizardStep.leaseSigning);
            stepStatus.complete().setValue(false);
            wizardStatus.steps().add(stepStatus);
        }

        for (LeaseParticipantMoveInAction moveInAction : ServerSideFactory.create(CustomerFacade.class).getActiveMoveInActions(
                ResidentPortalContext.getLeaseParticipant())) {
            MoveInWizardStepStatusTO stepStatus = EntityFactory.create(MoveInWizardStepStatusTO.class);
            switch (moveInAction.type().getValue()) {
            case autoPay:
                stepStatus.step().setValue(MoveInWizardStep.pap);
                stepStatus.complete().setValue(false);
                wizardStatus.steps().add(stepStatus);
                break;
            case insurance:
                stepStatus.step().setValue(MoveInWizardStep.insurance);
                stepStatus.complete().setValue(false);
                wizardStatus.steps().add(stepStatus);
                break;
            }
        }

        callback.onSuccess(wizardStatus);
    }

    @Override
    public void skipStep(AsyncCallback<VoidSerializable> callback, MoveInWizardStep step) {
        switch (step) {
        case insurance:
            ServerSideFactory.create(CustomerFacade.class).skipMoveInAction(ResidentPortalContext.getLeaseParticipant(), MoveInActionType.insurance);
            Persistence.service().commit();
            break;
        case pap:
            ServerSideFactory.create(CustomerFacade.class).skipMoveInAction(ResidentPortalContext.getLeaseParticipant(), MoveInActionType.autoPay);
            Persistence.service().commit();
            break;
        default:
            throw new IllegalArgumentException();
        }
        ServerContext.getVisit().setAclRevalidationRequired();
        callback.onSuccess(null);
    }

}
