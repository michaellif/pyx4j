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

import com.google.gwt.core.client.GWT;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentLeaseDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentLeaseListService;

public class DelinquentLeasesDetailsFactory extends AbstractListerDetailsFactory<DelinquentLeaseDTO, CounterGadgetFilter> {

    public static class DelinquentLeasesLister extends AbstractDetailsLister<DelinquentLeaseDTO> {

        private static final I18n i18n = I18n.get(DelinquentLeasesDetailsFactory.DelinquentLeasesLister.class);

        public DelinquentLeasesLister() {
            super(DelinquentLeaseDTO.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().buildingPropertyCode()).build(),                    
                    new Builder(proto().unitNumber()).build(),
                  
                    new Builder(proto().leaseId()).build(),

                    // TODO fix sortable when primary tenant is a normalized field in the lease (and not in current term)
                    new Builder(proto().participantId()).searchable(true).sortable(false).build(),                                        
                    new Builder(proto().primaryApplicantsFirstName()).searchable(true).sortable(false).build(),
                    new Builder(proto().primaryApplicantsLastName()).searchable(true).sortable(false).build(),
                    new Builder(proto().mobilePhone()).searchable(true).sortable(false).build(),
                    new Builder(proto().homePhone()).searchable(true).sortable(false).visible(false).build(),
                    new Builder(proto().workPhone()).searchable(true).sortable(false).visible(false).build(),
                    new Builder(proto().email()).searchable(true).sortable(false).build(),
                                                                                
                    new Builder(proto().arrears().bucketThisMonth()).searchable(true).sortable(true).build(),
                    new Builder(proto().arrears().bucket30()).searchable(true).sortable(true).build(),
                    new Builder(proto().arrears().bucket60()).searchable(true).sortable(true).build(),
                    new Builder(proto().arrears().bucket90()).searchable(true).sortable(true).build(),
                    new Builder(proto().arrears().bucketOver90()).searchable(true).sortable(true).build(),
                    new Builder(proto().arrears().arrearsAmount()).searchable(true).sortable(true).build()
                    
             );// @formatter:on
        }

        @Override
        protected void onItemSelect(DelinquentLeaseDTO item) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Lease().formViewerPlace(item.getPrimaryKey()));
        }
    }

    public DelinquentLeasesDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<DelinquentLeaseDTO, CounterGadgetFilter> criteriaProvider) {
        super(//@formatter:off
                DelinquentLeaseDTO.class,
                new DelinquentLeasesLister(),
                GWT.<DelinquentLeaseListService>create(DelinquentLeaseListService.class),
                filterDataProvider,
                criteriaProvider
        );//@formatter:on
    }

}
