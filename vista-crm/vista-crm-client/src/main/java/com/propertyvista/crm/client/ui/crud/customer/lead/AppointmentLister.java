/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.customer.lead;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentLister extends SiteDataTablePanel<Appointment> {

    public AppointmentLister() {
        super(Appointment.class, GWT.<AbstractCrudService<Appointment>> create(AppointmentCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().date()).build(), //
                new ColumnDescriptor.Builder(proto().time()).build(), //
                new ColumnDescriptor.Builder(proto().agent()).build(), //
                new ColumnDescriptor.Builder(proto().phone()).build(), //
                new ColumnDescriptor.Builder(proto().email()).build(), //
                new ColumnDescriptor.Builder(proto().status()).build());

        setDataTableModel(new DataTableModel<Appointment>());
    }

    @Override
    public void populate() {
        super.populate();

        ((AppointmentCrudService) getService()).getActiveState(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                setAddNewActionEnabled(result);
            }
        }, EntityFactory.createIdentityStub(Lead.class, getDataSource().getParentEntityId()));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().date(), false), new Sort(proto().time(), false));
    }

}
