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

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;
import com.pyx4j.site.client.ui.visor.AbstractVisorViewer;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorView extends AbstractVisorViewer {

    private final ILister<MaintenanceRequestDTO> lister;

    public MaintenanceRequestVisorView(MaintenanceRequestVisorController controller) {
        this.lister = new ListerInternalViewImplBase<MaintenanceRequestDTO>(new MaintenanceRequestLister());

        // UI:
        setContentPane(new ScrollPanel(lister.asWidget()));
        getElement().getStyle().setProperty("padding", "6px");
    }

    public ILister<MaintenanceRequestDTO> getLister() {
        return lister;
    }
}
