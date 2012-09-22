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

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;

import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.IFilterDataProvider;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.dto.LeaseDTO;

public class LeasesDetailsFactory extends AbstractListerDetailsFactory<LeaseDTO, CounterGadgetFilter> {

    private static class LeasesDetailsLister extends AbstractDetailsLister<LeaseDTO> {

        public LeasesDetailsLister() {
            super(LeaseDTO.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().leaseId()).build(),
                    new Builder(proto().type()).build(),
                    
                    new Builder(proto().unit().building().propertyCode()).build(),
                    new Builder(proto().unit()).searchableOnly().build(),
                    new Builder(proto().unit().info().number()).searchableOnly().columnTitle(proto().unit().getMeta().getCaption()).build(),
                    
                    new Builder(proto().status()).build(),
                    new Builder(proto().completion()).build(),
                    new Builder(proto().billingAccount().accountNumber()).build(),
                    
                    new Builder(proto().leaseFrom()).build(),
                    new Builder(proto().leaseTo()).build(),
                    
                    new Builder(proto().expectedMoveIn()).build(),
                    new Builder(proto().expectedMoveOut()).build(),
                    new Builder(proto().actualMoveIn(), false).build(),
                    new Builder(proto().actualMoveOut(), false).build(),
                    new Builder(proto().moveOutNotice()).build(),
                    
                    new Builder(proto().approvalDate(), false).build(),
                    new Builder(proto().creationDate(), false).build()
            );//@formatter:on

        }
    }

    public LeasesDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterData, ICriteriaProvider<LeaseDTO, CounterGadgetFilter> criteriaProvider) {
        super(//@formatter:off
                LeaseDTO.class,
                new LeasesDetailsLister(),
                GWT.<LeaseViewerCrudService>create(LeaseViewerCrudService.class),
                filterData,
                criteriaProvider
        );//@formatter:on

    }

}
