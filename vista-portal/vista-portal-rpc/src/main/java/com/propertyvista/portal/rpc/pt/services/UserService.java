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
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

/**
 * This is secure services, user need to be lodged in to do this actions.
 */
public interface UserService extends IService {

    public void passwordReset(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request);

}
