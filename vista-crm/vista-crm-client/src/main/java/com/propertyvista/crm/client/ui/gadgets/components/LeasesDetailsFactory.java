/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.dto.LeaseDTO;

public class LeasesDetailsFactory extends AbstractListerDetailsFactory<LeaseDTO, CounterGadgetFilter> {

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;

    static {
        LeaseDTO proto = EntityFactory.getEntityPrototype(LeaseDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new Builder(proto.leaseId()).build(),
                    new Builder(proto.type()).build(),
                    
                    new Builder(proto.unit().building().propertyCode()).build(),
                    new Builder(proto.unit()).searchableOnly().build(),
                    new Builder(proto.unit().info().number()).searchableOnly().columnTitle(proto.unit().getMeta().getCaption()).build(),
                    
                    new Builder(proto.status()).build(),
                    new Builder(proto.completion()).build(),
                    new Builder(proto.billingAccount().accountNumber()).build(),
                    
                    new Builder(proto.leaseFrom()).build(),
                    new Builder(proto.leaseTo()).build(),
                    
                    new Builder(proto.expectedMoveIn()).build(),
                    new Builder(proto.expectedMoveOut()).build(),
                    new Builder(proto.actualMoveIn(), false).build(),
                    new Builder(proto.actualMoveOut(), false).build(),
                    new Builder(proto.moveOutSubmissionDate()).build(),
                    
                    new Builder(proto.approvalDate(), false).build(),
                    new Builder(proto.creationDate(), false).build()
            );//@formatter:on
    }

    public LeasesDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterData, ICriteriaProvider<LeaseDTO, CounterGadgetFilter> criteriaProvider,
            Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                LeaseDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<LeaseViewerCrudService>create(LeaseViewerCrudService.class),
                filterData,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on

    }

}
