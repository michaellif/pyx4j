/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.rpc.services.DashboardMetadataService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardMetadataServiceImpl implements DashboardMetadataService {

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {
        // TODO Auto-generated method stub
    }

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieveSettings(AsyncCallback<AbstractGadgetSettings> callback, Key gadgetMetadataId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveSettings(AsyncCallback<AbstractGadgetSettings> callback, Key gadgetMetadataId, AbstractGadgetSettings editableEntity) {
        // TODO Auto-generated method stub

    }

}
