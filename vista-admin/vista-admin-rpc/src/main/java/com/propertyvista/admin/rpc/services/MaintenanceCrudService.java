/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface MaintenanceCrudService extends AbstractCrudService<SystemMaintenanceState> {

    public void getSystemReadOnlyStatus(AsyncCallback<Boolean> callback);

    public void resetGlobalCache(AsyncCallback<VoidSerializable> callback);
}
