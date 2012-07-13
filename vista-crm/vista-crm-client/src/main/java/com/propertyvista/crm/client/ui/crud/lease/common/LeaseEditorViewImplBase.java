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

        form.get(form.proto().version().leaseProducts().serviceItem()).setValue(value.version().leaseProducts().serviceItem());
        CEntityForm<BillableItem> billableItemForm = (CEntityForm<BillableItem>) form.get(form.proto().version().leaseProducts().serviceItem());
        billableItemForm.get(billableItemForm.proto().item()).refresh(true);

        form.get(form.proto().version().leaseProducts().featureItems()).setValue(value.version().leaseProducts().featureItems());
        form.get(form.proto().version().leaseProducts().concessions()).setValue(value.version().leaseProducts().concessions());
    }
}
