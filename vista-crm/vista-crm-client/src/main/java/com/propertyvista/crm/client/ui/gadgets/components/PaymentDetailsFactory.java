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
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentDetailsFactory extends AbstractListerDetailsFactory<PaymentRecordDTO, CounterGadgetFilter> {

    private static final List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        PaymentRecordDTO proto = EntityFactory.getEntityPrototype(PaymentRecordDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.id()).build(),
                    new MemberColumnDescriptor.Builder(proto.leaseTermParticipant().leaseParticipant().customer().customerId()).build(),
                    new MemberColumnDescriptor.Builder(proto.leaseTermParticipant().leaseParticipant().customer().person().name()).build(),
                    new MemberColumnDescriptor.Builder(proto.leaseTermParticipant().role()).build(),
                    new MemberColumnDescriptor.Builder(proto.amount()).build(),
                    new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).build(),
                    new MemberColumnDescriptor.Builder(proto.createdDate()).build(),
                    new MemberColumnDescriptor.Builder(proto.receivedDate()).build(),
                    new MemberColumnDescriptor.Builder(proto.lastStatusChangeDate()).build(),
                    new MemberColumnDescriptor.Builder(proto.targetDate()).build(),
                    new MemberColumnDescriptor.Builder(proto.paymentStatus()).build()
                );//@formatter:on
    }

    public PaymentDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                PaymentRecordDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<PaymentCrudService>create(PaymentCrudService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }

}
