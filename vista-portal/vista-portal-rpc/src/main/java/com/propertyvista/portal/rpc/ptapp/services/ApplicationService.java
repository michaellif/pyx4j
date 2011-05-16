/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.portal.domain.ptapp.ApplicationProgress;
import com.propertyvista.portal.domain.ptapp.ApplicationWizardStep;
import com.propertyvista.portal.domain.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.CurrentApplication;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.UserRuntimeException;

/**
 * This is secure services, user need to lodged in to use application.
 */
public interface ApplicationService extends IService {

    /**
     * Find existing application for user.
     * 
     * If no application exists, new one will be created, together with the progress and
     * unit selection
     * For new applications:
     * search criteria must be provided
     * 
     * @throws UserRuntimeException
     *             if search criteria did not yield any results
     */
    public void getCurrentApplication(AsyncCallback<CurrentApplication> callback, UnitSelectionCriteria request);

    public void getApplicationProgress(AsyncCallback<ApplicationProgress> callback, ApplicationWizardStep currentStep, ApplicationWizardSubstep substep);

}
