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
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.form.IEditorView;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.DepositLifecycle.DepositType;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseEditorViewBase<DTO extends LeaseDTO> extends IEditorView<DTO> {

    interface Presenter extends IEditorView.Presenter {

        void setSelectedUnit(AptUnit item);

        void setSelectedService(ProductItem item);

        void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem item);

        void createDeposit(AsyncCallback<DepositLifecycle> callback, DepositType depositType, BillableItem item);
    }

    void updateUnitValue(DTO value);

    void updateServiceValue(DTO value);
}
