/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.admin.client.ui.crud.maintenance.MaintenanceEditorView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.services.MaintenanceCrudService;

public class MaintenanceEditorActivity extends EditorActivityBase<SystemMaintenanceState> implements MaintenanceEditorView.Presenter {

    public MaintenanceEditorActivity(Place place) {
        super(place, AdministrationVeiwFactory.instance(MaintenanceEditorView.class), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class),
                SystemMaintenanceState.class);
    }
}
