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
import com.propertyvista.crm.rpc.services.tenant.lease.LeaseCrudService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Concession.Status;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LeaseEditorActivity(Place place) {
        super(place, TenantViewFactory.instance(LeaseEditorView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class), LeaseDTO.class);
    }

    @Override
    public void onPopulateSuccess(LeaseDTO result) {
        fillserviceItems(result);
        fillServiceEligibilityData(result, result.version().leaseProducts().serviceItem().item());

        super.onPopulateSuccess(result);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<LeaseDTO> callback) {
        ((LeaseEditorView) getView()).showSelectTypePopUp(new DefaultAsyncCallback<Service.Type>() {
            @Override
            public void onSuccess(Service.Type type) {
                LeaseDTO entity = EntityFactory.create(LeaseDTO.class);
                entity.createDate().setValue(new LogicalDate());
                entity.paymentFrequency().setValue(PaymentFrequency.Monthly);
                entity.version().status().setValue(Lease.Status.Created);
                entity.type().setValue(type);

                callback.onSuccess(entity);
            }
        });
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((LeaseCrudService) getService()).setSelectededUnit(new AsyncCallback<AptUnit>() {

            @Override
            public void onSuccess(AptUnit unit) {
                LeaseDTO currentValue = getView().getValue().duplicate();

                currentValue.unit().set(unit);
                currentValue.selectedBuilding().set(unit.belongsTo());

                clearServiceAgreement(currentValue, true);
                fillserviceItems(currentValue);

                getView().populate(currentValue);

                // if there is only one service for the selected unit - pre-set it:
                if (currentValue.selectedServiceItems().size() == 1) {
                    setSelectedService(currentValue.selectedServiceItems().get(0));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected.getPrimaryKey());
    }

    @Override
    public void setSelectedService(ProductItem serviceItem) {
        LeaseDTO currentValue = getView().getValue().duplicate();
        if (fillServiceEligibilityData(currentValue, serviceItem)) {

            // clear current dependable data:
            clearServiceAgreement(currentValue, false);

            // set selected service:
            currentValue.version().leaseProducts().serviceItem().set(createBillableItem(serviceItem));
            currentValue.version().leaseProducts().serviceItem().effectiveDate().setValue(currentValue.leaseFrom().getValue());

            // pre-populate utilities for the new service:
            for (ProductItem item : currentValue.selectedUtilityItems()) {
                currentValue.version().leaseProducts().featureItems().add(createBillableItem(item));
            }

            getView().populate(currentValue);
        }
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
        ((LeaseCrudService) getService()).calculateChargeItemAdjustments(callback, item);
    }

    private void clearServiceAgreement(LeaseDTO currentValue, boolean all) {
        if (all) {
            currentValue.version().leaseProducts().serviceItem().set(null);
        }
        currentValue.version().leaseProducts().featureItems().clear();
        currentValue.version().leaseProducts().concessions().clear();
    }

    private void fillserviceItems(LeaseDTO currentValue) {
        currentValue.selectedServiceItems().clear();
        for (Service service : currentValue.selectedBuilding().productCatalog().services()) {
            if (service.version().type().equals(currentValue.type())) {
                for (ProductItem item : service.version().items()) {
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
        for (Service service : currentValue.selectedBuilding().productCatalog().services()) {
            for (ProductItem item : service.version().items()) {
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
            ProductCatalog catalog = currentValue.selectedBuilding().productCatalog();
            List<FeatureItemType> utilitiesToExclude = new ArrayList<FeatureItemType>(catalog.includedUtilities().size() + catalog.externalUtilities().size());
            utilitiesToExclude.addAll(catalog.includedUtilities());
            utilitiesToExclude.addAll(catalog.externalUtilities());

            // fill features:
            for (Feature feature : selectedService.version().features()) {
                for (ProductItem item : feature.version().items()) {
                    switch (feature.version().type().getValue()) {
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

            // fill relevant concessions:
            for (Concession concession : selectedService.version().concessions()) {
                if (concession.version().status().getValue() == Status.approved) {
                    currentValue.selectedConcessions().add(concession);
                }
            }
        }

        return (selectedService != null);
    }

    private BillableItem createBillableItem(ProductItem item) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem._currentPrice().setValue(item.price().getValue());
        newItem.effectiveDate().setValue(new LogicalDate());
        return newItem;
    }
}
