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
package com.propertyvista.crm.client.activity.crud.lease.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.lease.common.term.LeaseTermEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermEditorActivity extends CrmEditorActivity<LeaseTermDTO> implements LeaseTermEditorView.Presenter {

    public static final String ARG_NAME_RETURN_BH = "rbh";

    public static enum ReturnBehaviour {

        Default, Application, Lease;
    }

    private final ReturnBehaviour returnBehaviour;

    public LeaseTermEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LeaseTermEditorView.class), GWT.<LeaseTermCrudService> create(LeaseTermCrudService.class), LeaseTermDTO.class);

        String val;
        if ((val = place.getFirstArg(ARG_NAME_RETURN_BH)) != null) {
            returnBehaviour = ReturnBehaviour.valueOf(val);
        } else {
            returnBehaviour = ReturnBehaviour.Default;
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        // if new Entity - set pre-selected unit if supplied: 
        if (isNewEntity() && getPlace().getNewItem() != null && !((LeaseTermDTO) getPlace().getNewItem()).newParentLease().isNull()) {
            AptUnit unit = ((LeaseTermDTO) getPlace().getNewItem()).newParentLease().unit();
            if (!unit.isNull()) {
                setSelectedUnit(unit);
            }
        }
    }

    @Override
    protected void goToViewer(final Key entityID) {
        if (returnBehaviour != ReturnBehaviour.Default) {
            getService().retrieve(new DefaultAsyncCallback<LeaseTermDTO>() {
                @Override
                public void onSuccess(LeaseTermDTO result) {
                    switch (returnBehaviour) {
                    case Application:
                        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.LeaseApplication().formViewerPlace(result.lease().getPrimaryKey()));
                        break;
                    case Lease:
                        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(result.lease().getPrimaryKey()));
                        break;
                    case Default:
                        LeaseTermEditorActivity.super.goToViewer(entityID);
                        break;
                    }
                }
            }, entityID, RetrieveTarget.View);
        } else {
            super.goToViewer(entityID);
        }
    }

    @Override
    protected void goToEditor(Key entityID) {
        if (returnBehaviour != ReturnBehaviour.Default) {
            AppSite.getPlaceController().goTo(
                    new CrmSiteMap.Tenants.LeaseTerm().formEditorPlace(entityID).queryArg(LeaseTermEditorActivity.ARG_NAME_RETURN_BH, returnBehaviour.name()));
        } else {
            super.goToEditor(entityID);
        }
    }

    @Override
    public void setSelectedBuilding(Building item) {
        ((LeaseTermEditorView) getView()).updateBuildingValue(item);
    }

    @Override
    public void setSelectedUnit(AptUnit item) {
        ((LeaseTermCrudService) getService()).setSelectedUnit(new DefaultAsyncCallback<LeaseTermDTO>() {
            @Override
            public void onSuccess(LeaseTermDTO result) {
                ((LeaseTermEditorView) getView()).updateUnitValue(result);
            }
        }, EntityFactory.createIdentityStub(AptUnit.class, item.getPrimaryKey()), getView().getValue());
    }

    @Override
    public void setSelectedService(ProductItem item) {
        ((LeaseTermCrudService) getService()).setSelectedService(new DefaultAsyncCallback<LeaseTermDTO>() {
            @Override
            public void onSuccess(LeaseTermDTO result) {
                ((LeaseTermEditorView) getView()).updateServiceValue(result);
            }
        }, EntityFactory.createIdentityStub(ProductItem.class, item.getPrimaryKey()), getView().getValue());
    }

    @Override
    public void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem item) {
        ((LeaseTermCrudService) getService()).createBillableItem(callback, EntityFactory.createIdentityStub(ProductItem.class, item.getPrimaryKey()), getView()
                .getValue());
    }

    @Override
    public void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item) {
        ((LeaseTermCrudService) getService()).createDeposit(callback, depositType, item, getView().getValue());
    }

    @Override
    public ReturnBehaviour getReturnBehaviour() {
        return returnBehaviour;
    }
}
