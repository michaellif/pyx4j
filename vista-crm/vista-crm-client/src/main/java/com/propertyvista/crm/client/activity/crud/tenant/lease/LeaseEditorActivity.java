/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectTenantCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    private final IListerView.Presenter tenantsLister;

    @SuppressWarnings("unchecked")
    public LeaseEditorActivity(Place place) {
        super((LeaseEditorView) TenantViewFactory.instance(LeaseEditorView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class),
                LeaseDTO.class);

        buildingsLister = new ListerActivityBase<Building>(((LeaseEditorView) view).getBuildingListerView(),
                (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);

        unitsLister = new ListerActivityBase<AptUnit>(((LeaseEditorView) view).getUnitListerView(),
                (AbstractCrudService<AptUnit>) GWT.create(SelectUnitCrudService.class), AptUnit.class);

        tenantsLister = new ListerActivityBase<Tenant>(((LeaseEditorView) view).getTenantListerView(),
                (AbstractCrudService<Tenant>) GWT.create(SelectTenantCrudService.class), Tenant.class);

        withPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return buildingsLister;
    }

    @Override
    public Presenter getUnitPresenter() {
        return unitsLister;
    }

    @Override
    public Presenter getTenantPresenter() {
        return tenantsLister;
    }

    @Override
    public void onPopulateSuccess(LeaseDTO result) {

        buildingsLister.populate(0);
        tenantsLister.populate(0);

        populateUnitLister(result.selectedBuilding());
        fillserviceItems(result);
        fillServiceEligibilityData(result, result.serviceAgreement().serviceItem().item());

        super.onPopulateSuccess(result);
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        ((LeaseCrudService) service).syncBuildingServiceCatalog(new AsyncCallback<Building>() {

            @Override
            public void onSuccess(Building building) {
                LeaseDTO currentValue = view.getValue();
                currentValue.selectedBuilding().set(building);

                populateUnitLister(building);
                fillserviceItems(currentValue);

                view.populate(currentValue);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected);
    }

    @Override
    public void setSelectedService(ServiceItem serviceItem) {
        LeaseDTO currentValue = view.getValue();
        if (fillServiceEligibilityData(currentValue, serviceItem)) {
            // clear currently selected dependable data:
            currentValue.serviceAgreement().featureItems().clear();
            currentValue.serviceAgreement().concessions().clear();

            view.populate(currentValue);
        }
    }

    @Override
    public void removeTenat(TenantInLease tenant) {
        ((LeaseCrudService) service).removeTenat(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, tenant);
    }

    public void populateUnitLister(Building selected) {
        if (!selected.isEmpty()) {
            unitsLister.setParentFiltering(selected.getPrimaryKey());
        }
        unitsLister.populate(0);
    }

    private void fillserviceItems(LeaseDTO currentValue) {
        currentValue.selectedServiceItems().clear();
        for (Service service : currentValue.selectedBuilding().serviceCatalog().services()) {
            if (service.type().equals(currentValue.type())) {
                currentValue.selectedServiceItems().addAll(service.items());
            }
        }
    }

    private boolean fillServiceEligibilityData(LeaseDTO currentValue, ServiceItem serviceItem) {
        if (serviceItem == null) {
            return false;
        }

        // find the service by Service item:
        Service selecteService = null;
        for (Service service : currentValue.selectedBuilding().serviceCatalog().services()) {
            for (ServiceItem item : service.items()) {
                if (item.equals(serviceItem)) {
                    selecteService = service;
                    break;
                }
            }
            if (selecteService != null) {
                break; // found!..
            }
        }

        // fill related features and concession:
        currentValue.selectedFeatureItems().clear();
        currentValue.selectedConcesions().clear();
        if (selecteService != null) {
            for (ServiceFeature feature : selecteService.features()) {
                for (ServiceItem item : feature.feature().items())
                    // filter out utilities included in price for selected building: 
                    if (currentValue.selectedBuilding().includedUtilities() != null && !currentValue.selectedBuilding().includedUtilities().isEmpty()) {
                        if (!currentValue.selectedBuilding().includedUtilities().contains(item.type())) {
                            currentValue.selectedFeatureItems().add(item);
                        }
                    } else {
                        currentValue.selectedFeatureItems().addAll(feature.feature().items());
                    }
            }
            for (ServiceConcession consession : selecteService.concessions()) {
                currentValue.selectedConcesions().add(consession.concession());
            }
        }

        return (selecteService != null);
    }
}
