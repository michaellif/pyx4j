/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tenantsure;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.TenantSureDTO;
import com.propertyvista.operations.rpc.services.TenantSureCrudService;

public class TenantSureLister extends SiteDataTablePanel<TenantSureDTO> {

    public TenantSureLister() {
        super(TenantSureDTO.class, GWT.<AbstractCrudService<TenantSureDTO>> create(TenantSureCrudService.class), false, false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().pmc()).build(), //
                new ColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(), //

                new ColumnDescriptor.Builder(proto().propertyCode()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().propertySuspended()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().certificateNumber()).build(), //

                new ColumnDescriptor.Builder(proto().policy().status()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().policy().cancellation()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().policy().cancellationDate()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().policy().certificate().inceptionDate()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().policy().tenant().customer().person().name()).searchable(false).sortable(false).build() //
        );

        setDataTableModel(new DataTableModel<TenantSureDTO>());
    }
}
