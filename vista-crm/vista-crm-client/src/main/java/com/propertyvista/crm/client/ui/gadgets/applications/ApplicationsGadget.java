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
package com.propertyvista.crm.client.ui.gadgets.applications;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.ApplicationsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.AbstractListerDetailsFactory.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ApplicationsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ApplicationsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LeaseApplicationDTO;

public class ApplicationsGadget extends CounterGadgetInstanceBase<ApplicationsGadgetDataDTO, Vector<Building>, ApplicationsGadgetMetadata> {

    public ApplicationsGadget(GadgetMetadata metadata) {
        super(ApplicationsGadgetDataDTO.class, GWT.<ApplicationsGadgetService> create(ApplicationsGadgetService.class), new ApplicationsGadgetSummaryForm(),
                metadata, ApplicationsGadgetMetadata.class);
    }

    @Override
    protected Vector<Building> prepareSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        bind(proto().applications());
        bind(proto().approved());
        bind(proto().pending());
        bind(proto().declined());
        bind(proto().cancelled());
    }

    private void bind(IObject<?> member) {
        ICriteriaProvider<LeaseApplicationDTO, CounterGadgetFilter> criteriaProvider = new ICriteriaProvider<LeaseApplicationDTO, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<LeaseApplicationDTO>> callback, CounterGadgetFilter filterData) {
                GWT.<ApplicationsGadgetService> create(ApplicationsGadgetService.class).makeApplicaitonsCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember());
            }
        };
        bindDetailsFactory(member, new ApplicationsDetailsFactory(this, criteriaProvider));
    }
}
