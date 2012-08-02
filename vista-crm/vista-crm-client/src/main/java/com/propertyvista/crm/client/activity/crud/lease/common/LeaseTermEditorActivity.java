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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.lease.common.term.LeaseTermEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermEditorActivity extends EditorActivityBase<LeaseTermDTO> implements LeaseTermEditorView.Presenter {

    public LeaseTermEditorActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseTermEditorView.class), GWT.<LeaseTermCrudService> create(LeaseTermCrudService.class), LeaseTermDTO.class);
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
    protected void goToViewer(Key entityID) {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }
}
