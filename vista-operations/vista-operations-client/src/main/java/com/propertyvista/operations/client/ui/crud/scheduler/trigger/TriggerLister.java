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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.scheduler.trigger;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.TriggerDTO;

public class TriggerLister extends AbstractLister<TriggerDTO> {

    public TriggerLister() {
        super(TriggerDTO.class, true);

        DataTableModel<TriggerDTO> dataTableModel = new DataTableModel<TriggerDTO>(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().triggerType()).build(),
            new MemberColumnDescriptor.Builder(proto().options()).visible(false).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().name()).build(),
            new MemberColumnDescriptor.Builder(proto().scheduleSuspended()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().schedule()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().nextScheduledFireTime()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().populationType(), false).build(),
            new MemberColumnDescriptor.Builder(proto().created()).build()
        );//@formatter:on
        dataTableModel.setPageSize(PAGESIZE_LARGE);
        setDataTableModel(dataTableModel);

    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }
}
