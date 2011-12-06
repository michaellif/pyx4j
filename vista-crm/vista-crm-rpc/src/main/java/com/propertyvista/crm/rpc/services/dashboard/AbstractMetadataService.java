/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public interface AbstractMetadataService extends IService {

    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback);

    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId);

    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata editableEntity);

    public void retrieveSettings(AsyncCallback<GadgetMetadata> callback, Key gadgetMetadataId);

    public void saveSettings(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata);

}
