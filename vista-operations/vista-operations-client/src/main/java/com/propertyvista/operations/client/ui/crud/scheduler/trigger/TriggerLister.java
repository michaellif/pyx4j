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
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.TriggerDTO;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;

public class TriggerLister extends SiteDataTablePanel<TriggerDTO> {

    public TriggerLister() {
        super(TriggerDTO.class, GWT.<AbstractCrudService<TriggerDTO>> create(TriggerCrudService.class), true);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().triggerType()).build(), //
                new ColumnDescriptor.Builder(proto().options()).visible(false).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().name()).width("150px").build(), //
                new ColumnDescriptor.Builder(proto().scheduleSuspended()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().schedule()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().nextScheduledFireTime()).sortable(false).searchable(false).build(), //
                new ColumnDescriptor.Builder(proto().populationType()).visible(false).build(), //
                new ColumnDescriptor.Builder(proto().created()).build() //
        );

        DataTableModel<TriggerDTO> dataTableModel = new DataTableModel<TriggerDTO>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_LARGE);
        setDataTableModel(dataTableModel);

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
