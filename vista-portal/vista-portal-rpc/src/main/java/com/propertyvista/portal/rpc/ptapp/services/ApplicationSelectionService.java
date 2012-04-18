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
package com.propertyvista.portal.rpc.ptapp.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.ptapp.OnlineApplication;

/**
 * 
 * The extending of {@link AbstractListService} is just to support UI's {@link basic lister}
 */
public interface ApplicationSelectionService extends IService {

    void getApplications(AsyncCallback<Vector<OnlineApplication>> callback);

    /** set the chosen application as a working context for the PtApp */
    void setApplicationContext(AsyncCallback<VoidSerializable> callback, OnlineApplication applicationStub);

}
