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

import com.google.gwt.core.shared.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractListerView;
import com.propertyvista.operations.domain.eft.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.operations.rpc.services.simulator.DirectDebitSimRecordCrudService;

public class DirectDebitSimRecordListerViewImpl extends AbstractListerView<DirectDebitSimRecord> implements DirectDebitSimRecordListerView {

    public DirectDebitSimRecordListerViewImpl() {
        setDataTablePanel(new DirectDebitSimRecordLister());
    }

    public static class DirectDebitSimRecordLister extends SiteDataTablePanel<DirectDebitSimRecord> {

        public DirectDebitSimRecordLister() {
            super(DirectDebitSimRecord.class, GWT.<AbstractListCrudService<DirectDebitSimRecord>> create(DirectDebitSimRecordCrudService.class), true);

            setColumnDescriptors( //
                    new MemberColumnDescriptor.Builder(proto().accountNumber()).build(), //
                    new MemberColumnDescriptor.Builder(proto().amount()).build(), //
                    new MemberColumnDescriptor.Builder(proto().paymentReferenceNumber()).build(), //
                    new MemberColumnDescriptor.Builder(proto().customerName()).build(), //
                    new MemberColumnDescriptor.Builder(proto().receivedDate()).build());

            setDataTableModel(new DataTableModel<DirectDebitSimRecord>());
        }
    }

}
