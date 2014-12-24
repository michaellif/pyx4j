/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseLister extends SiteDataTablePanel<EvictionCaseDTO> {

    private Key leaseId;

    public EvictionCaseLister() {
        super(EvictionCaseDTO.class, GWT.<EvictionCaseCrudService> create(EvictionCaseCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().createdOn()).build(), //
                new ColumnDescriptor.Builder(proto().createdBy()).build(), //
                new ColumnDescriptor.Builder(proto().closedOn()).build(), //
                new ColumnDescriptor.Builder(proto().note()).build());

        setDataTableModel(new DataTableModel<EvictionCaseDTO>());
    }

    public void setLeaseId(Key leaseId) {
        this.leaseId = leaseId;
    }

    @Override
    protected void onItemNew() {
        ((EvictionCaseCrudService) getService()).hasEvictionFlow(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean confirmed) {
                if (confirmed && canCreateNewItem()) {
                    AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getItemOpenPlaceClass()).formNewItemPlace(leaseId));
                }
            }
        }, leaseId);
    }

}
