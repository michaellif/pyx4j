/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.auditrecords;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.services.AuditRecordCrudService;

public class AuditRecordsLister extends SiteDataTablePanel<AuditRecordOperationsDTO> {

    public AuditRecordsLister() {
        super(AuditRecordOperationsDTO.class, GWT.<AuditRecordCrudService> create(AuditRecordCrudService.class), false, false);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().when()).build(), //
                new ColumnDescriptor.Builder(proto().worldTime()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().namespace()).build(), //
                new ColumnDescriptor.Builder(proto().pmc()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().application()).build(), //
                new ColumnDescriptor.Builder(proto().userKey()).build(), //
                new ColumnDescriptor.Builder(proto().userName()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().remoteAddr()).build(), //
                new ColumnDescriptor.Builder(proto().sessionId()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().event()).build(), //
                new ColumnDescriptor.Builder(proto().targetEntity()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().entityId()).build(), //
                new ColumnDescriptor.Builder(proto().entityClass()).build(), //
                new ColumnDescriptor.Builder(proto().details()).build());

        setDataTableModel(new DataTableModel<AuditRecordOperationsDTO>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().when(), true), new Sort(proto().pmc(), false));
    }

}
