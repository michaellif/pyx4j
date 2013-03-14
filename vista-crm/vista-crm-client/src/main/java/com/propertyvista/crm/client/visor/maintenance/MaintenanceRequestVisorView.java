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

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.IVisorViewer;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorView extends ScrollPanel implements IVisorViewer {

    private static final I18n i18n = I18n.get(MaintenanceRequestVisorView.class);

    private final MaintenanceRequestVisorController controller;

    private final IListerView<MaintenanceRequestDTO> lister;

    public MaintenanceRequestVisorView(MaintenanceRequestVisorController controller) {
        this.controller = controller;
        this.lister = new ListerInternalViewImplBase<MaintenanceRequestDTO>(new MaintenanceRequestLister());

        // UI:
        setWidget(lister.asWidget());
        getElement().getStyle().setProperty("padding", "6px");
    }

    public IListerView<MaintenanceRequestDTO> getLister() {
        return lister;
    }
}
