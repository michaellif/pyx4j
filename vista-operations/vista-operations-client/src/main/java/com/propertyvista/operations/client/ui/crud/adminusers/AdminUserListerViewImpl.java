/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 */
package com.propertyvista.operations.client.ui.crud.adminusers;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;

public class AdminUserListerViewImpl extends AbstractListerView<OperationsUserDTO> implements AdminUserListerView {

    public AdminUserListerViewImpl() {
        setDataTablePanel(new AdminUserLister());
    }

    public static class AdminUserLister extends SiteDataTablePanel<OperationsUserDTO> {

        public AdminUserLister() {
            super(OperationsUserDTO.class, GWT.<AdminUserCrudService> create(AdminUserCrudService.class), true);

            setColumnDescriptors( //
                    new ColumnDescriptor.Builder(proto().name()).build(), //
                    new ColumnDescriptor.Builder(proto().email()).build(), //
                    new ColumnDescriptor.Builder(proto().enabled()).build(), //
                    new ColumnDescriptor.Builder(proto().created()).build(), //
                    new ColumnDescriptor.Builder(proto().credentialUpdated()).build());

            setDataTableModel(new DataTableModel<OperationsUserDTO>());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false), new Sort(proto().created(), false));
        }
    }
}
