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
package com.propertyvista.portal.server.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.services.ApplicationServices;

public class ApplicationServicesImpl extends ApplicationEntityServicesImpl implements ApplicationServices {

    @Override
    public void getCurrentApplication(AsyncCallback<CurrentApplication> callback, UnitSelectionCriteria request) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveApplicationProgress(AsyncCallback<ApplicationProgress> callback, ApplicationProgress request) {
        applyApplication(request);
        secureSave(request);
        callback.onSuccess(request);
    }

}
