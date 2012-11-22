/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.ui.crud.IView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.activity.ListerActivityFactory;
import com.propertyvista.crm.client.visor.IVisorController;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorController implements IVisorController {

    private final MaintenanceRequestVisorView view;

    private final Presenter<MaintenanceRequestDTO> lister;

    public MaintenanceRequestVisorController(Key tenantId) {
        view = new MaintenanceRequestVisorView(this);
        lister = ListerActivityFactory.create(null, view.getLister(), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class),
                MaintenanceRequestDTO.class, VistaCrmBehavior.Maintenance);
        lister.setParent(tenantId);
    }

    @Override
    public void show(final IView parentView) {
        lister.populate();
        parentView.showVisor(getView(), "Maintenance Requests");
    }

    @Override
    public void hide(final IView parentView) {
        parentView.hideVisor();
    }

    @Override
    public boolean isShown(IView parentView) {
        return parentView.isVisorShown();
    }

    @Override
    public IsWidget getView() {
        return view;
    }
}
