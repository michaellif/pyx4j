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
package com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.rpc.services.PmcCardTransactionRecordCrudService;

public class CardTransactionRecordLister extends SiteDataTablePanel<CardTransactionRecord> {

    public CardTransactionRecordLister(boolean addPmcColumn) {
        super(CardTransactionRecord.class, GWT.<AbstractCrudService<CardTransactionRecord>> create(PmcCardTransactionRecordCrudService.class), false, false);

        if (addPmcColumn) {
            setColumnDescriptors(new ColumnDescriptor.Builder(proto().pmc()).build(), //
                    new ColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(), //
                    new ColumnDescriptor.Builder(proto().merchantTerminalId()).build(), //
                    new ColumnDescriptor.Builder(proto().cardType()).build(), //
                    new ColumnDescriptor.Builder(proto().amount()).build(), //
                    new ColumnDescriptor.Builder(proto().feeAmount()).build(), //
                    new ColumnDescriptor.Builder(proto().paymentTransactionId()).build(), //
                    new ColumnDescriptor.Builder(proto().completionDate()).build(), //
                    new ColumnDescriptor.Builder(proto().saleResponseCode()).build(), //
                    new ColumnDescriptor.Builder(proto().voided()).build(), //
                    new ColumnDescriptor.Builder(proto().feeResponseCode()).build(), //
                    new ColumnDescriptor.Builder(proto().creationDate()).build());
        } else {
            setColumnDescriptors(new ColumnDescriptor.Builder(proto().cardType()).build(), //
                    new ColumnDescriptor.Builder(proto().amount()).build(), //
                    new ColumnDescriptor.Builder(proto().feeAmount()).build(), //
                    new ColumnDescriptor.Builder(proto().paymentTransactionId()).build(), //
                    new ColumnDescriptor.Builder(proto().completionDate()).build(), //
                    new ColumnDescriptor.Builder(proto().saleResponseCode()).build(), //
                    new ColumnDescriptor.Builder(proto().voided()).build(), //
                    new ColumnDescriptor.Builder(proto().feeResponseCode()).build(), //
                    new ColumnDescriptor.Builder(proto().creationDate()).build());
        }
        setDataTableModel(new DataTableModel<CardTransactionRecord>());
    }

    public void setParentPmc(Pmc pmc) {
        this.getDataSource().setPreDefinedFilters(Arrays.<Criterion> asList(//@formatter:off
                PropertyCriterion.eq(proto().pmc(), pmc)
        ));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), true));
    }

}
