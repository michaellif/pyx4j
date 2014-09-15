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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.backoffice.ui.prime.form.IEditor;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseTermEditorActivity.ReturnBehaviour;
import com.propertyvista.crm.rpc.dto.lease.financial.DepositListDTO;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.dto.LeaseTermDTO;

public interface LeaseTermEditorView extends IEditor<LeaseTermDTO> {

    interface Presenter extends IEditor.Presenter {

        void setSelectedBuilding(Building item);

        void setSelectedUnit(AptUnit item);

        void setSelectedService(ProductItem item);

        void createBillableItem(AsyncCallback<BillableItem> callback, ProductItem item);

        void retirveAvailableDeposits(AsyncCallback<DepositListDTO> callback, BillableItem item);

        void recalculateDeposits(AsyncCallback<DepositListDTO> callback, BillableItem item);

        void createDeposit(AsyncCallback<Deposit> callback, DepositType depositType, BillableItem item);

        ReturnBehaviour getReturnBehaviour();
    }

    void updateBuildingValue(Building value);

    void updateUnitValue(LeaseTermDTO value);

    void updateServiceValue(LeaseTermDTO value);
}
