/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-05-26
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.datagrid;

import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

import com.propertyvista.crm.rpc.dto.financial.moneyin.MoneyInCandidateDTO;

public class MoneyInCandidateLister extends DataTablePanel<MoneyInCandidateDTO> {

    public MoneyInCandidateLister() {
        super(MoneyInCandidateDTO.class);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().building()).filterAlwaysShown(true).build(), //
                new ColumnDescriptor.Builder(proto().unit()).build(), //
                new ColumnDescriptor.Builder(proto().leaseId()).build(), //
                new ColumnDescriptor.Builder(proto().payerCandidates().$().name()).filterAlwaysShown(true).searchableOnly().build(), //
                new ColumnDescriptor.Builder(proto().payerCandidates()).searchable(false).sortable(false).build(), //
                new ColumnDescriptor.Builder(proto().totalOutstanding()).build());

        DataTableModel<MoneyInCandidateDTO> dataTableModel = new DataTableModel<MoneyInCandidateDTO>();
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }
}
