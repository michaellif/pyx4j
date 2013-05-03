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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.AbstractVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;

import com.propertyvista.crm.client.activity.ListerControllerFactory;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorController extends AbstractVisorController {

    private static final I18n i18n = I18n.get(MaintenanceRequestVisorController.class);

    private final MaintenanceRequestVisorView visor;

    private final Presenter<MaintenanceRequestDTO> lister;

    public MaintenanceRequestVisorController(IPane parentView, Key buildingId) {
        super(parentView);
        visor = new MaintenanceRequestVisorView(this);
        lister = ListerControllerFactory.create(visor.getLister(), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class),
                MaintenanceRequestDTO.class, VistaCrmBehavior.Maintenance);
        lister.setParent(buildingId);
    }

    public MaintenanceRequestVisorController(IPane parentView, Key buildingId, Key tenantId) {
        this(parentView, buildingId);
        lister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class).reporter().id(), tenantId));
    }

    @Override
    public void show() {
        lister.populate();
        visor.setCaption(i18n.tr("Maintenance Requests"));
        getParentView().showVisor(visor);
    }
}
