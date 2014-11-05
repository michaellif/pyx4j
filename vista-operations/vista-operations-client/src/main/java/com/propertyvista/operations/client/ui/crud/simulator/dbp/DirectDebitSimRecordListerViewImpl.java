/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractPrimeLister;

import com.propertyvista.operations.client.ui.crud.OperationsListerViewImplBase;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimRecordListerViewImpl extends OperationsListerViewImplBase<DirectDebitSimRecord> implements DirectDebitSimRecordListerView {

    public DirectDebitSimRecordListerViewImpl() {
        setLister(new DirectDebitSimRecordLister());
    }

    public static class DirectDebitSimRecordLister extends AbstractPrimeLister<DirectDebitSimRecord> {

        public DirectDebitSimRecordLister() {
            super(DirectDebitSimRecord.class, true);
            setDataTableModel(new DataTableModel<DirectDebitSimRecord>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().accountNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().paymentReferenceNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().customerName()).build(),
                    new MemberColumnDescriptor.Builder(proto().receivedDate()).build()
            ));//@formatter:on
        }
    }

}
