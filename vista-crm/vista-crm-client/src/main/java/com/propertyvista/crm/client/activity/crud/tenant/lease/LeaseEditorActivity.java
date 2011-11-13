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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.FilterData;
import com.pyx4j.site.client.ui.crud.lister.FilterData.Operands;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectTenantCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    private final IListerView.Presenter tenantsLister;

    private LogicalDate leaseFrom, leaseTo;

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

        setPlace(place);
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

        setSelectedDates(result.leaseFrom().getValue(), result.leaseTo().getValue());
        populateUnitLister(result.selectedBuilding());

        fillserviceItems(result);
        fillServiceEligibilityData(result, result.serviceAgreement().serviceItem().item());

        super.onPopulateSuccess(result);
    }

    @Override
    public void setSelectedDates(LogicalDate from, LogicalDate to) {
        leaseFrom = from;
        leaseTo = to;
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        populateUnitLister(selected);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((LeaseCrudService) service).setSelectededUnit(new AsyncCallback<AptUnit>() {

            @Override
            public void onSuccess(AptUnit unit) {
                LeaseDTO currentValue = view.getValue();

                currentValue.unit().set(unit);
                currentValue.selectedBuilding().set(unit.belongsTo());

                clearServiceAgreement(currentValue, true);
                fillserviceItems(currentValue);

                view.populate(currentValue);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected.getPrimaryKey());
    }

    @Override
    public void setSelectedService(ServiceItem serviceItem) {
        LeaseDTO currentValue = view.getValue();
        if (fillServiceEligibilityData(currentValue, serviceItem)) {

            // clear current dependable data:
            clearServiceAgreement(currentValue, false);

            // set selected service: 
            currentValue.serviceAgreement().serviceItem().set(createChargeItem(serviceItem));

            // pre-populate utilities for the new service: 
            for (ServiceItem item : currentValue.selectedUtilityItems()) {
                currentValue.serviceAgreement().featureItems().add(createChargeItem(item));
            }

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
        }, tenant.getPrimaryKey());
    }

    public void populateUnitLister(Building selected) {
        if (!selected.isEmpty()) {
            unitsLister.setParentFiltering(selected.getPrimaryKey());
        }
        if (leaseFrom != null && leaseTo != null) {
            List<FilterData> filters = new ArrayList<FilterData>(2);
            filters.add(new FilterData(EntityFactory.getEntityPrototype(AptUnit.class).availableForRent().getPath(), Operands.greaterThen, leaseFrom));
            filters.add(new FilterData(EntityFactory.getEntityPrototype(AptUnit.class).availableForRent().getPath(), Operands.lessThen, leaseTo));
            unitsLister.setPreDefinedFilters(filters);
        }
        unitsLister.populate(0);
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<Double> callback, ChargeItem item) {
        ((LeaseCrudService) service).calculateChargeItemAdjustments(callback, item);
    }

    private void clearServiceAgreement(LeaseDTO currentValue, boolean all) {
        if (all) {
            currentValue.serviceAgreement().serviceItem().set(null);
        }
        currentValue.serviceAgreement().featureItems().clear();
        currentValue.serviceAgreement().concessions().clear();
    }

    private void fillserviceItems(LeaseDTO currentValue) {
        currentValue.selectedServiceItems().clear();
        for (Service service : currentValue.selectedBuilding().serviceCatalog().services()) {
            if (service.type().equals(currentValue.type())) {
                for (ServiceItem item : service.items()) {
                    if (currentValue.unit().equals(item.element())) {
                        currentValue.selectedServiceItems().add(item);
                    }
                }
            }
        }
    }

    private boolean fillServiceEligibilityData(LeaseDTO currentValue, ServiceItem serviceItem) {
        if (serviceItem == null) {
            return false;
        }

        // find the service by Service item:
        Service selectedService = null;
        for (Service service : currentValue.selectedBuilding().serviceCatalog().services()) {
            for (ServiceItem item : service.items()) {
                if (item.equals(serviceItem)) {
                    selectedService = service;
                    break;
                }
            }
            if (selectedService != null) {
                break; // found!..
            }
        }

        // fill related features and concession:
        currentValue.selectedFeatureItems().clear();
        currentValue.selectedUtilityItems().clear();
        currentValue.selectedConcessions().clear();

        if (selectedService != null) {
            ServiceCatalog catalog = currentValue.selectedBuilding().serviceCatalog();
            List<ServiceItemType> utilitiesToExclude = new ArrayList<ServiceItemType>(catalog.includedUtilities().size() + catalog.externalUtilities().size());
            utilitiesToExclude.addAll(catalog.includedUtilities());
            utilitiesToExclude.addAll(catalog.externalUtilities());

            for (ServiceFeature feature : selectedService.features()) {
                for (ServiceItem item : feature.feature().items()) {
                    switch (feature.feature().type().getValue()) {
                    case utility:
                        // filter out utilities included in price for selected building:
                        if (!utilitiesToExclude.contains(item.type())) {
                            currentValue.selectedUtilityItems().add(item);
                        }
                        break;
                    default:
                        currentValue.selectedFeatureItems().add(item);
                    }
                }
            }
            // fill concessions:
            for (ServiceConcession consession : selectedService.concessions()) {
                currentValue.selectedConcessions().add(consession.concession());
            }
        }

        return (selectedService != null);
    }

    private ChargeItem createChargeItem(ServiceItem serviceItem) {
        ChargeItem chargeItem = EntityFactory.create(ChargeItem.class);
        chargeItem.item().set(serviceItem);
        chargeItem.price().setValue(serviceItem.price().getValue());
        return chargeItem;
    }
}
