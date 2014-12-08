/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.rpc.services.PmcDirectDebitRecordCrudService;

public class DirectDebitRecordLister extends SiteDataTablePanel<DirectDebitRecord> {

    public DirectDebitRecordLister(boolean addPmcColumn) {
        super(DirectDebitRecord.class, GWT.<AbstractCrudService<DirectDebitRecord>> create(PmcDirectDebitRecordCrudService.class), false, false);

        List<ColumnDescriptor> columns = new ArrayList<>();

        if (addPmcColumn) {
            columns.add(new MemberColumnDescriptor.Builder(proto().pmc()).build());
            columns.add(new MemberColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build());
        }

        columns.add(new MemberColumnDescriptor.Builder(proto().accountNumber()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().amount()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().paymentReferenceNumber()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().directDebitFile().fileSerialNumber()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().directDebitFile().fileName()).visible(false).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().directDebitFile().fileSerialDate()).visible(false).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().customerName()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().receivedDate()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().processingStatus()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().trace().collection()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().trace().locationCode()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().trace().sourceCode()).build());
        columns.add(new MemberColumnDescriptor.Builder(proto().trace().traceNumber()).build());

        setColumnDescriptors(columns);

        setDataTableModel(new DataTableModel<DirectDebitRecord>());

    }

    public void setParentPmc(Pmc pmc) {
        this.getDataSource().setPreDefinedFilters(Arrays.<Criterion> asList(PropertyCriterion.eq(proto().pmc(), pmc)));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), true));
    }

}
