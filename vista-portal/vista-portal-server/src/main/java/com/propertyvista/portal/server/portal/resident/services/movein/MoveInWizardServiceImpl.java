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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionType;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class MoveInWizardServiceImpl implements MoveInWizardService {

    @Override
    public void obtainIncompleteSteps(AsyncCallback<Vector<MoveInWizardStepTO>> callback) {
        Vector<MoveInWizardStepTO> r = new Vector<>();

        if (SecurityController.check(PortalResidentBehavior.LeaseAgreementSigningRequired)) {
            MoveInWizardStepTO to = EntityFactory.create(MoveInWizardStepTO.class);
            to.step().setValue(MoveInWizardStep.leaseSigning);
            to.canSkip().setValue(false);
            r.add(to);
        }

        for (LeaseParticipantMoveInAction moveInAction : ServerSideFactory.create(CustomerFacade.class).getActiveMoveInActions(
                ResidentPortalContext.getLeaseParticipant())) {
            MoveInWizardStepTO to = EntityFactory.create(MoveInWizardStepTO.class);
            switch (moveInAction.type().getValue()) {
            case autoPay:
                to.step().setValue(MoveInWizardStep.pap);
                to.canSkip().setValue(true);
                r.add(to);
                break;
            case insurance:
                to.step().setValue(MoveInWizardStep.insurance);
                to.canSkip().setValue(true);
                r.add(to);
                break;
            }
        }

        callback.onSuccess(r);
    }

    @Override
    public void skipSteps(AsyncCallback<Void> callback, MoveInWizardStep step) {
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
        callback.onSuccess(null);
    }

}
