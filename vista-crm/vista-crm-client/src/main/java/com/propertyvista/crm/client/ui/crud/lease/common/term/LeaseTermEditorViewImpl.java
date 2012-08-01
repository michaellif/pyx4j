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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.pyx4j.entity.client.CEntityForm;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseTermDTO;
import com.propertyvista.misc.VistaTODO;

public class LeaseTermEditorViewImpl extends CrmEditorViewImplBase<LeaseTermDTO> implements LeaseTermEditorView {

    public LeaseTermEditorViewImpl() {
        super(CrmSiteMap.Tenants.LeaseTerm.class);
    }

    @Override
    public void updateUnitValue(LeaseTermDTO value) {
        LeaseTermForm form = (LeaseTermForm) getForm();

        form.get(form.proto().lease().unit()).setValue(value.lease().unit());
        form.get(form.proto().lease().unit().building()).setValue(value.lease().unit().building());

        updateServiceValue(value);
    }

    @Override
    public void updateServiceValue(LeaseTermDTO value) {
        LeaseTermForm form = (LeaseTermForm) getForm();

        @SuppressWarnings("unchecked")
        CEntityForm<BillableItem> billableItemForm = (CEntityForm<BillableItem>) form.get(form.proto().version().leaseProducts().serviceItem());

//TODO     revert after investigation:    
//        billableItemForm.setValue(value.version().leaseProducts().serviceItem());
        billableItemForm.populate(value.version().leaseProducts().serviceItem());

        billableItemForm.get(billableItemForm.proto().item()).refresh(true);

        form.get(form.proto().version().leaseProducts().featureItems()).setValue(value.version().leaseProducts().featureItems());
        if (!VistaTODO.removedForProduction) {
            form.get(form.proto().version().leaseProducts().concessions()).setValue(value.version().leaseProducts().concessions());
        }

        // update runtime non-editable data:
// TODO : _2 uncomment then        
//        form.getValue().billingAccount().deposits().clear();
//        form.getValue().billingAccount().deposits().addAll(value.billingAccount().deposits());

        form.getValue().selectedServiceItems().clear();
        form.getValue().selectedServiceItems().addAll(value.selectedServiceItems());

        form.getValue().selectedFeatureItems().clear();
        form.getValue().selectedFeatureItems().addAll(value.selectedFeatureItems());

        form.getValue().selectedConcessions().clear();
        form.getValue().selectedConcessions().addAll(value.selectedConcessions());
    }
}
