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
package com.propertyvista.portal.rpc.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.CurrentApplication;

import com.pyx4j.rpc.shared.IService;

/**
 * This is secure services, user need to lodged in to use application.
 */
public interface ApplicationServices extends IService {

    /**
     * Find of create Application object for current user. For now we have only one.
     */
    public void getCurrentApplication(AsyncCallback<CurrentApplication> callback, UnitSelectionCriteria request);

    public void getApplicationProgress(AsyncCallback<ApplicationProgress> callback, ApplicationWizardStep currentStep);

}
