/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcomewizard;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep.Status;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcomewizard.ResetWizardService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ResetWizardServiceImpl implements ResetWizardService {

    @Override
    public void resetWizard(AsyncCallback<VoidSerializable> callback) {
        OnlineApplication application = PtAppContext.retrieveCurrentUserApplication();

        boolean isFirst = true;
        for (ApplicationWizardStep step : application.steps()) {
            if (isFirst) {
                step.status().setValue(Status.latest);
                isFirst = false;
            } else {
                step.status().setValue(Status.notVisited);
            }
            Persistence.service().persist(step);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
