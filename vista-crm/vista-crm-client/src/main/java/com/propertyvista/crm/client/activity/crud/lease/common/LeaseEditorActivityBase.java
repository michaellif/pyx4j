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

import java.math.BigDecimal;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseEditorPresenterBase;
import com.propertyvista.crm.rpc.services.lease.LeaseCrudServiceBase;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

public abstract class LeaseEditorActivityBase<DTO extends LeaseDTO> extends EditorActivityBase<DTO> implements LeaseEditorPresenterBase {

    public LeaseEditorActivityBase(CrudAppPlace place, IEditorView<DTO> view, AbstractCrudService<DTO> service, Class<DTO> entityClass) {
        super(place, view, service, entityClass);
    }

    @Override
    public void onPopulateSuccess(DTO result) {
        super.onPopulateSuccess(result);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((LeaseCrudServiceBase<DTO>) getService()).setSelectededUnit(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, selected.getPrimaryKey(), getEntityId());
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
        ((LeaseCrudServiceBase<DTO>) getService()).calculateChargeItemAdjustments(callback, item);
    }
}
