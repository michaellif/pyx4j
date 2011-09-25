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
package com.propertyvista.admin.client.ui.administration;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;

import com.propertyvista.admin.client.ui.crud.AdminEditorViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class MaintenanceViewImpl extends AdminEditorViewImplBase<SystemMaintenanceState> implements MaintenanceView {

    public MaintenanceViewImpl() {
        super(AdminSiteMap.Administration.Maintenance.class, new MaintenanceEditorForm());

        // hide save button:
        btnSave.setVisible(false);
    }
}
