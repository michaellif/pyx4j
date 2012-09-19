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
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.ApplicationsCriteriaProvider;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.misc.VistaTODO;

public class ApplicationsDetailsFactory extends AbstractListerDetailsFactory<LeaseApplicationDTO, CounterGadgetFilter> {

    private static class ApplicationsLister extends AbstractDetailsLister<LeaseApplicationDTO> {

        private static final I18n i18n = I18n.get(ApplicationsDetailsFactory.ApplicationsLister.class);

        public ApplicationsLister() {
            super(LeaseApplicationDTO.class);
            setColumnDescriptors(//@formatter:off
                    new Builder(proto().leaseId()).build(),
                    new Builder(proto().type()).build(),
                    
                    new Builder(proto().unit().building().propertyCode()).build(),
                    new Builder(proto().unit()).build(),
                    
                    new Builder(proto().leaseApplication().status(), true).build(),
                    
                    new Builder(proto().currentTerm().termFrom()).build(),
                    new Builder(proto().currentTerm().termTo()).build(),
                    
                    new Builder(proto().expectedMoveIn()).build(),
                    new Builder(proto().expectedMoveOut(), false).build(),
                    new Builder(proto().actualMoveIn(), false).build(),
                    new Builder(proto().actualMoveOut(), false).build(),
                    new Builder(proto().moveOutNotice(), false).build(),
                    
                    new Builder(proto().creationDate(), false).build(),
                    
                    new Builder(proto().mainApplicant()).sortable(!VistaTODO.complextQueryCriteria).searchable(!VistaTODO.complextQueryCriteria).build(),
                    

                    new Builder(proto().leaseApplication().equifaxApproval().percenrtageApproved(), false).build(),
                    new Builder(proto().leaseApplication().equifaxApproval().suggestedDecision(), false).build(),

                    new Builder(proto().numberOfOccupants(), false).sortable(false).searchable(false).title(i18n.tr("Occupants")).build(),
                    new Builder(proto().numberOfApplicants(), true).sortable(false).searchable(false).title(i18n.tr("Applicants")).build(),
                    new Builder(proto().numberOfGuarantors(), true).sortable(false).searchable(false).title(i18n.tr("Guarantors")).build(),
                    
                    new Builder(proto().currentTerm().version().tenants()).build()
                );//@formatter:on

        }

    }

    public ApplicationsDetailsFactory(final ApplicationsCriteriaProvider criteriaProvider, IBuildingFilterContainer buildingFilterProvider,
            IObject<?> filterPreset) {
        super(//@formatter:off
                LeaseApplicationDTO.class,
                new ApplicationsLister(),
                GWT.<LeaseApplicationViewerCrudService>create(LeaseApplicationViewerCrudService.class),
                new CounterGadgetFilterProvider(buildingFilterProvider, filterPreset.getPath()),
                new ICriteriaProvider<LeaseApplicationDTO, CounterGadgetFilter>() {
                    @Override
                    public void makeCriteria(AsyncCallback<EntityListCriteria<LeaseApplicationDTO>> callback, CounterGadgetFilter filterData) {
                        criteriaProvider.makeApplicaitonsCriteria(callback, filterData.getBuildings(), filterData.getCounterMember().toString());
                    }
                }
        );//@formatter:on
    }
}
