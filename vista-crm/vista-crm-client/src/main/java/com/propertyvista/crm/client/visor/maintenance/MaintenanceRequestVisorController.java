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

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.backoffice.activity.AbstractVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

public class MaintenanceRequestVisorController extends AbstractVisorController {

    private final MaintenanceRequestVisorView visor;

    private final Key buildingId;

    private final Key tenantId;

    public MaintenanceRequestVisorController(IPrimePaneView parentView, Key buildingId) {
        this(parentView, buildingId, null);
    }

    public MaintenanceRequestVisorController(IPrimePaneView parentView, Key buildingId, final Key tenantId) {
        super(parentView);
        this.buildingId = buildingId;
        this.tenantId = tenantId;
        visor = new MaintenanceRequestVisorView(this);
    }

    @Override
    public void show() {
        visor.populate();
        getParentView().showVisor(visor);
    }

    public boolean canCreateNewItem() {
        return true;
    }

    public Key getBuildingId() {
        return buildingId;
    }

    public Key getTenantId() {
        return tenantId;
    }
}
