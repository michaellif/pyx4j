/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.unit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyManagerService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerActivity extends CrmViewerActivity<AptUnitDTO> implements UnitViewerView.Presenter {

    private final UnitOccupancyManagerService occupancyManagerService;

    private AptUnitDTO currentValue;

    public UnitViewerActivity(CrudAppPlace place) {
        super(AptUnitDTO.class, place, CrmSite.getViewFactory().getView(UnitViewerView.class), GWT.<UnitCrudService> create(UnitCrudService.class));

        occupancyManagerService = GWT.create(UnitOccupancyManagerService.class);
    }

    @Override
    public void onPopulateSuccess(AptUnitDTO result) {
        super.onPopulateSuccess(result);
        currentValue = result;

        final UnitViewerView view = (UnitViewerView) getView();

        occupancyManagerService.getMakeVacantConstraints(new DefaultAsyncCallback<MakeVacantConstraintsDTO>() {
            @Override
            public void onSuccess(MakeVacantConstraintsDTO result) {
                view.setMakeVacantConstraints(result);
            }
        }, result.getPrimaryKey());

        occupancyManagerService.canScopeAvailable(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                view.setCanScopeAvailable(result);
            }
        }, result.getPrimaryKey());

        occupancyManagerService.canScopeOffMarket(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                view.setCanScopeOffMarket(result);
            }
        }, result.getPrimaryKey());

        occupancyManagerService.canScopeRenovation(new DefaultAsyncCallback<LogicalDate>() {
            @Override
            public void onSuccess(LogicalDate result) {
                view.setMinRenovationEndDate(result);
            }
        }, result.getPrimaryKey());
    }

    @Override
    public void scopeOffMarket(OffMarketType type) {
        occupancyManagerService.scopeOffMarket(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

        }, getEntityId(), type);
    }

    @Override
    public void scopeRenovation(LogicalDate renovationEndDate) {
        occupancyManagerService.scopeRenovation(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

        }, getEntityId(), renovationEndDate);
    }

    @Override
    public void scopeAvailable() {
        occupancyManagerService.scopeAvailable(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

        }, getEntityId());
    }

    @Override
    public void makeVacant(LogicalDate vacantFrom) {
        occupancyManagerService.makeVacant(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

        }, getEntityId(), vacantFrom);
    }

    @Override
    public void createMaintenanceRequest() {
        MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
        id.unit().set(EntityFactory.createIdentityStub(AptUnit.class, currentValue.getPrimaryKey()));
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.MaintenanceRequest().formNewItemPlace(id));
    }

    @Override
    public void updateAvailabilityFromYardi() {
        occupancyManagerService.updateAvailabilityFromYardi(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }

        }, getEntityId());
    }

}
