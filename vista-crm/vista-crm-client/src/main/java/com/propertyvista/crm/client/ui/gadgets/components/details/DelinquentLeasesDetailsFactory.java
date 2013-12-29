/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components.details;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class DelinquentLeasesDetailsFactory extends AbstractListerDetailsFactory<DelinquentLeaseDTO, CounterGadgetFilter> {

    private static List<ColumnDescriptor> DEFAULT_COLUMN_DESCRIPTORS;
    static {
        DelinquentLeaseDTO proto = EntityFactory.getEntityPrototype(DelinquentLeaseDTO.class);
        DEFAULT_COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                    new Builder(proto.buildingPropertyCode()).build(),                    
                    new Builder(proto.unitNumber()).build(),
                  
                    new Builder(proto.leaseId()).build(),

                    new Builder(proto.participantId()).searchable(true).sortable(true).build(),                                        
                    new Builder(proto.primaryApplicantsFirstName()).searchable(true).sortable(true).build(),
                    new Builder(proto.primaryApplicantsLastName()).searchable(true).sortable(true).build(),                    
                    new Builder(proto.mobilePhone()).searchable(true).sortable(true).build(),
                    new Builder(proto.homePhone()).searchable(true).sortable(true).visible(false).build(),
                    new Builder(proto.workPhone()).searchable(true).sortable(true).visible(false).build(),
                    new Builder(proto.email()).searchable(true).sortable(true).build(),
                                                                                
                    new Builder(proto.arrears().bucketThisMonth()).searchable(true).sortable(true).build(),
                    new Builder(proto.arrears().bucket30()).searchable(true).sortable(true).build(),
                    new Builder(proto.arrears().bucket60()).searchable(true).sortable(true).build(),
                    new Builder(proto.arrears().bucket90()).searchable(true).sortable(true).build(),
                    new Builder(proto.arrears().bucketOver90()).searchable(true).sortable(true).build(),
                    new Builder(proto.arrears().arrearsAmount()).searchable(true).sortable(true).build()
                    
             );// @formatter:on
    }

    public DelinquentLeasesDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter> criteriaProvider, Proxy<ListerUserSettings> listerSettingsProxy) {
        super(//@formatter:off
                DelinquentLeaseDTO.class,
                DEFAULT_COLUMN_DESCRIPTORS,
                GWT.<DelinquentLeaseListService>create(DelinquentLeaseListService.class),
                filterDataProvider,
                criteriaProvider,
                listerSettingsProxy
        );//@formatter:on
    }

    @Override
    protected void onItemSelected(DelinquentLeaseDTO item) {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(item.leasePrimaryKey().getValue()));
    }

}
