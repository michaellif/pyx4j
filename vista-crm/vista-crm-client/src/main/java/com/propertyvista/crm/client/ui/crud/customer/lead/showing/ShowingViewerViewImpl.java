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
 */
package com.propertyvista.crm.client.ui.crud.customer.lead.showing;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingViewerViewImpl extends CrmViewerViewImplBase<ShowingDTO> implements ShowingViewerView {

    public ShowingViewerViewImpl() {
        setForm(new ShowingForm(this));
    }

    @Override
    public void populate(ShowingDTO value) {
        super.populate(value);

        setEditingEnabled(value.status().getValue() != Showing.Status.seen);
        setEditingVisible(value.appointment().status().getValue() != Appointment.Status.closed
                && value.appointment().lead().status().getValue() != Lead.Status.closed);
    }
}