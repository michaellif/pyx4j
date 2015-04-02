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
package com.propertyvista.operations.client.ui.crud.scheduler.run;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;

public class RunViewerDataLister extends SiteDataTablePanel<RunData> {

    public RunViewerDataLister(boolean isInlineMode) {
        super(RunData.class, GWT.<RunDataCrudService> create(RunDataCrudService.class), false);
        setColumnDescriptors(isInlineMode ? createInlineViewColumnDescriptors() : createViewColumnDescriptors());
        setDataTableModel(new DataTableModel<RunData>());
    }

    private List<ColumnDescriptor> createViewColumnDescriptors() {
        List<ColumnDescriptor> c = Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().pmc()).build(),
                new ColumnDescriptor.Builder(proto().pmc().namespace()).columnTitle("Pmc namespace").searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().execution().trigger()).width("150px").build(),
                new ColumnDescriptor.Builder(proto().execution().trigger().name()).columnTitle("Trigger Name").searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().execution().trigger().triggerType()).visible(false).build(),
                new ColumnDescriptor.Builder(proto().started()).width("130px").build(),
                new ColumnDescriptor.Builder(proto().status()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().total()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().processed()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().failed()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().erred()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().detailsErred()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().totalDuration()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().message()).build(),
                new ColumnDescriptor.Builder(proto().errorMessage()).build(),
                new ColumnDescriptor.Builder(proto().updated()).build()
        );//@formatter:on
        return c;
    }

    private List<ColumnDescriptor> createInlineViewColumnDescriptors() {
        List<ColumnDescriptor> c = Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().pmc()).build(),
                new ColumnDescriptor.Builder(proto().pmc().namespace()).columnTitle("Pmc namespace").searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().started()).width("130px").build(),
                new ColumnDescriptor.Builder(proto().status()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().total()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().processed()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().failed()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().erred()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().detailsErred()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().totalDuration()).build(),
                new ColumnDescriptor.Builder(proto().executionReport().message()).build(),
                new ColumnDescriptor.Builder(proto().errorMessage()).build(),
                new ColumnDescriptor.Builder(proto().updated()).build()
        );//@formatter:on
        return c;
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().started(), true));
    }
}
