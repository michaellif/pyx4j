/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.billing.PaymentRecordCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentDetailsFactory extends AbstractListerDetailsFactory<PaymentRecordDTO, CounterGadgetFilter> {

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        PaymentRecordDTO proto = EntityFactory.getEntityPrototype(PaymentRecordDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new ColumnDescriptor.Builder(proto.id()).build(),
                    new ColumnDescriptor.Builder(proto.leaseTermParticipant().leaseParticipant().customer().customerId()).build(),
                    new ColumnDescriptor.Builder(proto.leaseTermParticipant().leaseParticipant().customer().person().name()).build(),
                    new ColumnDescriptor.Builder(proto.leaseTermParticipant().role()).build(),
                    new ColumnDescriptor.Builder(proto.amount()).build(),
                    new ColumnDescriptor.Builder(proto.paymentMethod().type()).build(),
                    new ColumnDescriptor.Builder(proto.created()).build(),
                    new ColumnDescriptor.Builder(proto.receivedDate()).build(),
                    new ColumnDescriptor.Builder(proto.lastStatusChangeDate()).build(),
                    new ColumnDescriptor.Builder(proto.targetDate()).build(),
                    new ColumnDescriptor.Builder(proto.paymentStatus()).build()
                );//@formatter:on
    }

    public PaymentDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                PaymentRecordDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<PaymentRecordCrudService>create(PaymentRecordCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }

}
