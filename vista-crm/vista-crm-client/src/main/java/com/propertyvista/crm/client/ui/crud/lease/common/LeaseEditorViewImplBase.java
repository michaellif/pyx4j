/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorViewImplBase<DTO extends LeaseDTO> extends CrmEditorViewImplBase<DTO> implements LeaseEditorViewBase<DTO> {

    public LeaseEditorViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(placeClass);
    }

    @Override
    public void updateUnitValue(DTO value) {
        LeaseFormBase<DTO> form = (LeaseFormBase<DTO>) getForm();

        form.get(form.proto().unit()).setValue(value.unit());
        form.get(form.proto().unit().building()).setValue(value.unit().building());

        updateServiceValue(value);
    }

    @Override
    public void updateServiceValue(DTO value) {
        LeaseFormBase<DTO> form = (LeaseFormBase<DTO>) getForm();

        @SuppressWarnings("unchecked")
        CEntityForm<BillableItem> billableItemForm = (CEntityForm<BillableItem>) form.get(form.proto().version().leaseProducts().serviceItem());
        billableItemForm.setValue(value.version().leaseProducts().serviceItem());
        billableItemForm.get(billableItemForm.proto().item()).refresh(true);

        form.get(form.proto().version().leaseProducts().featureItems()).reset();
        form.get(form.proto().version().leaseProducts().featureItems()).populate(value.version().leaseProducts().featureItems());

        form.get(form.proto().version().leaseProducts().concessions()).reset();
        form.get(form.proto().version().leaseProducts().concessions()).populate(value.version().leaseProducts().concessions());

        // update runtime non-editable data:
        form.getValue().billingAccount().deposits().clear();
        form.getValue().billingAccount().deposits().addAll(value.billingAccount().deposits());

        form.getValue().selectedServiceItems().clear();
        form.getValue().selectedServiceItems().addAll(value.selectedServiceItems());

        form.getValue().selectedFeatureItems().clear();
        form.getValue().selectedFeatureItems().addAll(value.selectedFeatureItems());

        form.getValue().selectedConcessions().clear();
        form.getValue().selectedConcessions().addAll(value.selectedConcessions());
    }
}
