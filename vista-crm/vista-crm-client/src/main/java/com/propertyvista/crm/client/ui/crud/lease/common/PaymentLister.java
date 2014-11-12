/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class PaymentLister extends SiteDataTablePanel<PaymentRecordDTO> {

    public PaymentLister() {
        super(PaymentRecordDTO.class, GWT.<PaymentRecordCrudService> create(PaymentRecordCrudService.class), false);

        List<ColumnDescriptor> cd = new ArrayList<>();
        cd.add(new MemberColumnDescriptor.Builder(proto().id()).build());
        if (VistaFeatures.instance().yardiIntegration()) {
            cd.add(new MemberColumnDescriptor.Builder(proto().yardiDocumentNumber(), false).build());
            cd.add(new MemberColumnDescriptor.Builder(proto().yardiBatches().$().externalBatchNumber()).searchableOnly().build());
        }
        cd.add(new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().customerId()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name()).searchable(false).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name().firstName()).searchableOnly()
                .build());
        cd.add(new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name().lastName()).searchableOnly()
                .build());
        cd.add(new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().role()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().amount()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().convenienceFee(), false).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().createdDate()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().receivedDate()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().lastStatusChangeDate()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().targetDate()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().paymentStatus()).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().finalizedDate(), false).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().rejectedWithNSF()).visible(false).build());
        cd.add(new MemberColumnDescriptor.Builder(proto().transactionErrorMessage()).visible(false).build());
        setDataTableModel(new DataTableModel<PaymentRecordDTO>(cd));
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), true));
    }
}
