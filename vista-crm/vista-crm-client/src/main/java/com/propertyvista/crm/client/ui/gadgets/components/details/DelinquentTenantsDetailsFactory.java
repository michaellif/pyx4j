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
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentTenantDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentTenantListService;

public class DelinquentTenantsDetailsFactory extends AbstractListerDetailsFactory<DelinquentTenantDTO, CounterGadgetFilter> {

    public static class DelinquentTenantsLister extends AbstractDetailsLister<DelinquentTenantDTO> {

        private static final I18n i18n = I18n.get(DelinquentTenantsDetailsFactory.DelinquentTenantsLister.class);

        public DelinquentTenantsLister() {
            super(DelinquentTenantDTO.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().leaseCustomer().participantId()).build(),
                    new Builder(proto().role()).build(),
                    
                    new Builder(proto().leaseCustomer().customer().person().name()).searchable(false).build(),
                    new Builder(proto().leaseCustomer().customer().person().name().firstName(), false).build(),
                    new Builder(proto().leaseCustomer().customer().person().name().lastName(), false).build(),
                    new Builder(proto().leaseCustomer().customer().person().sex(), false).build(),
                    new Builder(proto().leaseCustomer().customer().person().birthDate(), false).build(),
                                        
                    new Builder(proto().leaseCustomer().customer().person().mobilePhone()).build(),
                    new Builder(proto().leaseCustomer().customer().person().homePhone(), false).build(),
                    new Builder(proto().leaseCustomer().customer().person().workPhone(), false).build(),
                    new Builder(proto().leaseCustomer().customer().person().email()).build(),
                    
                    new Builder(proto().leaseTermV().holder().lease()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build(),
                    
                    new Builder(proto().arrears().bucketCurrent()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().bucketThisMonth()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().bucket30()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().bucket60()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().bucket90()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().bucketOver90()).searchable(false).sortable(false).build(),
                    new Builder(proto().arrears().arrearsAmount()).searchable(false).sortable(false).build()
// TODO the following two are implemented since prepayments are not yet implemented                    
//                    new Builder(proto().arrears().creditAmount(), false).searchable(false).sortable(false).build(),
//                    new Builder(proto().arrears().totalBalance()).searchable(false).sortable(false).build()
                    
             );// @formatter:on
        }

        @Override
        protected void onItemSelect(DelinquentTenantDTO item) {
            AppSite.getPlaceController().goTo(new CrmSiteMap.Tenants.Tenant().formViewerPlace(item.leaseCustomer().getPrimaryKey()));
        }
    }

    public DelinquentTenantsDetailsFactory(IFilterDataProvider<CounterGadgetFilter> filterDataProvider,
            ICriteriaProvider<DelinquentTenantDTO, CounterGadgetFilter> criteriaProvider) {
        super(//@formatter:off
                DelinquentTenantDTO.class,
                new DelinquentTenantsLister(),
                GWT.<DelinquentTenantListService>create(DelinquentTenantListService.class),
                filterDataProvider,
                criteriaProvider
        );//@formatter:on
    }

}
