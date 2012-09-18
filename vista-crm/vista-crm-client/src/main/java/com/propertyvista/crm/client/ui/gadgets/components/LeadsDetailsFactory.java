/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.LeadCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadsDetailsFactory implements CounterDetailsFactory {

    private final LeadCriteriaProvider leadCriteriaProvider;

    private final IBuildingFilterContainer buildingsFilerProvider;

    private final String filterPreset;

    private final LeadsDetailsLister lister;

    public LeadsDetailsFactory(LeadCriteriaProvider leadCriteriaProvider, IBuildingFilterContainer builindgsFilterContainer, String filter) {
        this.leadCriteriaProvider = leadCriteriaProvider;
        this.buildingsFilerProvider = builindgsFilterContainer;
        this.filterPreset = filter;
        this.lister = new LeadsDetailsLister();
    }

    @Override
    public Widget createDetailsWidget() {
        leadCriteriaProvider.makeLeadFilterCriteria(new DefaultAsyncCallback<EntityListCriteria<Lead>>() {

            @Override
            public void onSuccess(EntityListCriteria<Lead> result) {
                ListerDataSource<Lead> dataSource = new ListerDataSource<Lead>(Lead.class, GWT.<LeadCrudService> create(LeadCrudService.class));
                List<Criterion> criteria = result.getFilters();
                if (criteria != null && !criteria.isEmpty()) {
                    dataSource.setPreDefinedFilters(criteria);
                } else {
                    dataSource.clearPreDefinedFilters();
                }
                lister.setDataSource(dataSource);
                lister.obtain(0);
            }

        }, new Vector<Building>(buildingsFilerProvider.getSelectedBuildingsStubs()), filterPreset);
        return lister;
    }

}
