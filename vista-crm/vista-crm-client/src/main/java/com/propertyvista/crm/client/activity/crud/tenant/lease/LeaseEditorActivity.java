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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.tenant.application.LeaseCrudService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LeaseEditorActivity(Place place) {
        super(place, TenantViewFactory.instance(LeaseEditorView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class), LeaseDTO.class);
    }

    @Override
    public void onPopulateSuccess(LeaseDTO result) {
        fillserviceItems(result);
        fillServiceEligibilityData(result, result.serviceAgreement().serviceItem().item());

        super.onPopulateSuccess(result);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<LeaseDTO> callback) {
        ((LeaseEditorView) view).showSelectTypePopUp(new AsyncCallback<Service.Type>() {
            @Override
            public void onSuccess(Service.Type type) {
                LeaseDTO entity = EntityFactory.create(LeaseDTO.class);
                entity.createDate().setValue(new LogicalDate());
                entity.status().setValue(Lease.Status.Draft);
                entity.type().setValue(type);

                callback.onSuccess(entity);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((LeaseCrudService) service).setSelectededUnit(new AsyncCallback<AptUnit>() {

            @Override
            public void onSuccess(AptUnit unit) {
                LeaseDTO currentValue = view.getValue().duplicate();

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
    public void setSelectedService(ProductItem serviceItem) {
        LeaseDTO currentValue = view.getValue().duplicate();
        if (fillServiceEligibilityData(currentValue, serviceItem)) {

            // clear current dependable data:
            clearServiceAgreement(currentValue, false);

            // set selected service:
            currentValue.serviceAgreement().serviceItem().set(createBillableItem(serviceItem));

            // pre-populate utilities for the new service:
            for (ProductItem item : currentValue.selectedUtilityItems()) {
                currentValue.serviceAgreement().featureItems().add(createBillableItem(item));
            }

            view.populate(currentValue);
        }
    }

    @Override
    public void removeTenat(TenantInLease tenant) {
        ((LeaseCrudService) service).removeTenat(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
            }
        }, tenant.getPrimaryKey());
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
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
                for (ProductItem item : service.items()) {
                    if (currentValue.unit().equals(item.element())) {
                        currentValue.selectedServiceItems().add(item);
                    }
                }
            }
        }
    }

    private boolean fillServiceEligibilityData(LeaseDTO currentValue, ProductItem serviceItem) {
        if (serviceItem == null) {
            return false;
        }

        // find the service by Service item:
        Service selectedService = null;
        for (Service service : currentValue.selectedBuilding().serviceCatalog().services()) {
            for (ProductItem item : service.items()) {
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
            ProductCatalog catalog = currentValue.selectedBuilding().serviceCatalog();
            List<ProductItemType> utilitiesToExclude = new ArrayList<ProductItemType>(catalog.includedUtilities().size() + catalog.externalUtilities().size());
            utilitiesToExclude.addAll(catalog.includedUtilities());
            utilitiesToExclude.addAll(catalog.externalUtilities());

            for (Feature feature : selectedService.features()) {
                for (ProductItem item : feature.items()) {
                    switch (feature.type().getValue()) {
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
            currentValue.selectedConcessions().addAll(selectedService.concessions());
        }

        return (selectedService != null);
    }

    private BillableItem createBillableItem(ProductItem item) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem._currentPrice().setValue(item.price().getValue());
        newItem.effectiveDate().setValue(new LogicalDate());
        newItem.expirationDate().setValue(new LogicalDate());
        return newItem;
    }
}
