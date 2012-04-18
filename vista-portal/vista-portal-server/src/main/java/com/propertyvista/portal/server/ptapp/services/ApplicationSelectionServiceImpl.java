/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.util.Vector;

import org.apache.commons.lang.NotImplementedException;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationSelectionService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApplicationSelectionServiceImpl implements ApplicationSelectionService {

    @Override
    public void setApplicationContext(AsyncCallback<VoidSerializable> callback, OnlineApplication applicationStub) {
        // TODO implement setting contexts of current application
        throw new NotImplementedException();
    }

    @Override
    public void getApplications(AsyncCallback<Vector<OnlineApplication>> callback) {
        callback.onSuccess(new Vector<OnlineApplication>(ServerSideFactory.create(OnlineApplicationFacade.class).getOnlineApplications(
                PtAppContext.getCurrentUser())));
    }

}
