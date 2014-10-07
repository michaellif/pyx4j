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

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;

public class CardTransactionRecordLister extends AbstractLister<CardTransactionRecord> {

    public CardTransactionRecordLister(boolean addPmcColumn) {
        super(CardTransactionRecord.class, false, false);

        if (addPmcColumn) {
            setDataTableModel(new DataTableModel<CardTransactionRecord>(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().pmc()).build(),
                new MemberColumnDescriptor.Builder(proto().pmc().namespace()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto().merchantTerminalId()).build(),
                new MemberColumnDescriptor.Builder(proto().cardType()).build(),
                new MemberColumnDescriptor.Builder(proto().amount()).build(),
                new MemberColumnDescriptor.Builder(proto().feeAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().paymentTransactionId()).build(),
                new MemberColumnDescriptor.Builder(proto().completionDate()).build(),
                new MemberColumnDescriptor.Builder(proto().saleResponseCode()).build(),
                new MemberColumnDescriptor.Builder(proto().voided()).build(),
                new MemberColumnDescriptor.Builder(proto().feeResponseCode()).build(),
                new MemberColumnDescriptor.Builder(proto().creationDate()).build()
            ));//@formatter:on
        } else {
            setDataTableModel(new DataTableModel<CardTransactionRecord>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().cardType()).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().feeAmount()).build(),
                    new MemberColumnDescriptor.Builder(proto().paymentTransactionId()).build(),
                    new MemberColumnDescriptor.Builder(proto().completionDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().saleResponseCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().voided()).build(),
                    new MemberColumnDescriptor.Builder(proto().feeResponseCode()).build(),
                    new MemberColumnDescriptor.Builder(proto().creationDate()).build()
            ));//@formatter:on
        }

    }

    public void setParentPmc(Pmc pmc) {
        this.getDataSource().setPreDefinedFilters(Arrays.<Criterion> asList(//@formatter:off
                PropertyCriterion.eq(proto().pmc(), pmc)
        ));//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), true));
    }

}
