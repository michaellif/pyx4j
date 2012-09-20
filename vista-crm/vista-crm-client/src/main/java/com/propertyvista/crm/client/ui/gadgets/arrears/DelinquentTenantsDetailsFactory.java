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
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractDetailsLister;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilterProvider;
import com.propertyvista.crm.rpc.dto.gadgets.DelinquentTenantDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.DelinquentTenantListService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.DelinquentTenantCriteriaProvider;

public class DelinquentTenantsDetailsFactory extends AbstractListerDetailsFactory<DelinquentTenantDTO, CounterGadgetFilter> {

    public static class DelinquentTenantsLister extends AbstractDetailsLister<DelinquentTenantDTO> {

        private static final I18n i18n = I18n.get(DelinquentTenantsDetailsFactory.DelinquentTenantsLister.class);

        public DelinquentTenantsLister() {
            super(DelinquentTenantDTO.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().participantId()).build(),
                    new Builder(proto().role()).build(),
                    
                    new Builder(proto().customer().person().name()).searchable(false).build(),
                    new Builder(proto().customer().person().name().firstName(), false).build(),
                    new Builder(proto().customer().person().name().lastName(), false).build(),
                    new Builder(proto().customer().person().sex(), false).build(),
                    new Builder(proto().customer().person().birthDate(), false).build(),
                    
                    new Builder(proto().customer().person().homePhone()).build(),
                    new Builder(proto().customer().person().mobilePhone()).build(),
                    new Builder(proto().customer().person().workPhone()).build(),
                    new Builder(proto().customer().person().email()).build(),
                    
                    new Builder(proto().leaseTermV().holder().lease().unit()).columnTitle(i18n.tr("unit")).build(),
                    new Builder(proto().leaseTermV().holder().lease().unit().info().number()).columnTitle(i18n.tr("unit")).searchableOnly().build(),
                    new Builder(proto().leaseTermV().holder().lease().leaseId()).columnTitle(i18n.tr("Lease Id")).searchableOnly().build(),
                    
                    new Builder(proto().arrears().bucketThisMonth()).searchable(false).build(),
                    new Builder(proto().arrears().bucket30()).searchable(false).build(),
                    new Builder(proto().arrears().bucket60()).searchable(false).build(),
                    new Builder(proto().arrears().bucket90()).searchable(false).build(),
                    new Builder(proto().arrears().bucketOver90()).searchable(false).build(),
                    new Builder(proto().arrears().totalBalance()).searchable(false).build()
                    
             );// @formatter:on
        }

    }

    public DelinquentTenantsDetailsFactory(final DelinquentTenantCriteriaProvider tenantCriteriaProvider, IBuildingFilterContainer buildingFilterContainer,
            IObject<?> member) {
        super(//@formatter:off
                DelinquentTenantDTO.class,
                new DelinquentTenantsLister(),
                GWT.<DelinquentTenantListService>create(DelinquentTenantListService.class),
                new CounterGadgetFilterProvider(buildingFilterContainer, member.getPath()),
                new ICriteriaProvider<DelinquentTenantDTO, CounterGadgetFilter>() {

                    @Override
                    public void makeCriteria(AsyncCallback<EntityListCriteria<DelinquentTenantDTO>> callback, CounterGadgetFilter filterData) {
                        tenantCriteriaProvider.makeTenantCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
                    }
                }
        );//@formatter:on
    }

}
