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
package com.propertyvista.operations.client.ui.crud.pmc;

import java.util.Arrays;

import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;

public class DirectDebitRecordLister extends EntityDataTablePanel<DirectDebitRecord> {

    public DirectDebitRecordLister() {
        super(DirectDebitRecord.class, true, false);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().paymentReferenceNumber()).build(),
                new MemberColumnDescriptor.Builder(proto().customerName()).build(),
                new MemberColumnDescriptor.Builder(proto().receivedDate()).build(),
                new MemberColumnDescriptor.Builder(proto().processingStatus()).build()
        );//@formatter:on

    }

    public void setParentPmc(Pmc pmc) {
        this.getDataSource().setPreDefinedFilters(Arrays.<Criterion> asList(//@formatter:off
                PropertyCriterion.eq(proto().pmc(), pmc)
        ));//@formatter:on
    }

}
