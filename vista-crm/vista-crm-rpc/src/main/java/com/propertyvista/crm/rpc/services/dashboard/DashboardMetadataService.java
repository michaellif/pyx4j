/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-22
 * @author Vlad
 */
package com.propertyvista.crm.rpc.services.dashboard;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;

public interface DashboardMetadataService extends IService {

    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId);

    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata editableEntity);

    public void takeOwnership(AsyncCallback<VoidSerializable> callback, DashboardMetadata dashboardMetadataStub);

    public void changeOwnership(AsyncCallback<VoidSerializable> callback, DashboardMetadata dashboardMetadataStub, CrmUser updatedOwnerStub);

}
