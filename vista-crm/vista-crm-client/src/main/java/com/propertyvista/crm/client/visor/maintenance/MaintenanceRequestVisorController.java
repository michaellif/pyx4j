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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.AbstractVisorController;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorController extends AbstractVisorController {

    private static final I18n i18n = I18n.get(MaintenanceRequestVisorController.class);

    private final MaintenanceRequestVisorView visor;

    private final Presenter<MaintenanceRequestDTO> lister;

    public MaintenanceRequestVisorController(IPane parentView, Key buildingId) {
        this(parentView, buildingId, null);
    }

    public MaintenanceRequestVisorController(IPane parentView, final Key buildingId, final Key tenantId) {
        super(parentView);
        visor = new MaintenanceRequestVisorView(this);
        lister = new ListerController<MaintenanceRequestDTO>(visor.getLister(), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class),
                MaintenanceRequestDTO.class) {
            @Override
            public boolean canCreateNewItem() {
                return SecurityController.checkAnyBehavior(VistaCrmBehavior.Maintenance);
            }

            @Override
            public void editNew(final Class<? extends CrudAppPlace> openPlaceClass) {
                MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
                if (tenantId != null) {
                    id.tenant().set(EntityFactory.createIdentityStub(Tenant.class, tenantId));
                    lister.editNew(openPlaceClass, id);
                } else if (buildingId != null) {
                    id.building().set(EntityFactory.createIdentityStub(Building.class, buildingId));
                    lister.editNew(openPlaceClass, id);
                } else {
                    super.editNew(openPlaceClass);
                }
            }
        };

        lister.setParent(buildingId);
        if (tenantId != null) {
            lister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class).reporter().id(), tenantId));
        }
    }

    @Override
    public void show() {
        lister.populate();
        visor.setCaption(i18n.tr("Maintenance Requests"));
        getParentView().showVisor(visor);
    }
}
