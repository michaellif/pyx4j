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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilterProvider;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.PaymentCriteriaProvider;
import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentDetailsFactory extends AbstractListerDetailsFactory<PaymentRecordDTO, CounterGadgetFilter> {

    private static class PaymentsDetailsLister extends AbstractDetailsLister<PaymentRecordDTO> {

        public PaymentsDetailsLister() {
            super(PaymentRecordDTO.class);
            setColumnDescriptors(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().id()).build(),
                    new MemberColumnDescriptor.Builder(proto().leaseParticipant().customer().customerId()).build(),
                    new MemberColumnDescriptor.Builder(proto().leaseParticipant().customer().person().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().leaseParticipant().role()).build(),
                    new MemberColumnDescriptor.Builder(proto().amount()).build(),
                    new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().createdDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().receivedDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().lastStatusChangeDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().targetDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().paymentStatus()).build()
                );//@formatter:on
        }

    }

    public PaymentDetailsFactory(final PaymentCriteriaProvider criteriaProvider, IBuildingFilterContainer buildingFilterContainer, IObject<?> member) {
        super(//@formatter:off
                PaymentRecordDTO.class,
                new PaymentsDetailsLister(),
                GWT.<PaymentCrudService>create(PaymentCrudService.class),
                new CounterGadgetFilterProvider(buildingFilterContainer, member.getPath()),
                new ICriteriaProvider<PaymentRecordDTO, CounterGadgetFilter>() {
                    @Override
                    public void makeCriteria(AsyncCallback<EntityListCriteria<PaymentRecordDTO>> callback, CounterGadgetFilter filterData) {
                        criteriaProvider.makePaymentCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
                    }                    
                }
        );//@formatter:on
    }

}
