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
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;

public class RunLister extends SiteDataTablePanel<Run> {

    private static List<ColumnDescriptor> INLINE_VIEW_COLUMN_DESCRIPTORS = createInlineViewColumnDescriptors();

    private static List<ColumnDescriptor> VIEW_COLUMN_DESCRIPTORS = createViewColumnDescriptors();

    static List<ColumnDescriptor> createInlineViewColumnDescriptors() {
        Run proto = EntityFactory.getEntityPrototype(Run.class);
        List<ColumnDescriptor> c = Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto.status()).build(),
                new ColumnDescriptor.Builder(proto.started()).width("130px").build(),
                new ColumnDescriptor.Builder(proto.completed()).width("130px").build(),
                new ColumnDescriptor.Builder(proto.forDate()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().total()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().processed()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().failed()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().erred()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().detailsErred()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().averageDuration()).build(),
                new ColumnDescriptor.Builder(proto.executionReport().totalDuration()).build(),
                new ColumnDescriptor.Builder(proto.created()).build(),
                new ColumnDescriptor.Builder(proto.updated()).build()
        );//@formatter:on
        return c;
    }

    static List<ColumnDescriptor> createViewColumnDescriptors() {
        Run proto = EntityFactory.getEntityPrototype(Run.class);
        List<ColumnDescriptor> c = new Vector<ColumnDescriptor>(Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto.trigger().name()).width("150px").columnTitle("Trigger Name").build(),
                new ColumnDescriptor.Builder(proto.trigger().triggerType()).width("130px").build(),
                new ColumnDescriptor.Builder(proto.trigger()).searchableOnly().build()
        ));//@formatter:on
        c.addAll(createInlineViewColumnDescriptors());
        return c;
    }

    public RunLister(boolean isInlineMode) {
        super(Run.class, GWT.<RunCrudService> create(RunCrudService.class), false);
        setColumnDescriptors(isInlineMode ? INLINE_VIEW_COLUMN_DESCRIPTORS : VIEW_COLUMN_DESCRIPTORS);
        setDataTableModel(new DataTableModel<Run>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().updated(), true));
    }
}
