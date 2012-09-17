/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
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
import com.propertyvista.crm.rpc.services.customer.TenantCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.TenantCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TenantDTO;

public class TenantsDetailsFactory implements CounterDetailsFactory {

    private final TenantCriteriaProvider tenantsCriteriaProvider;

    private final IBuildingFilterContainer buildingsFilterProvider;

    private final String filterPreset;

    private final TenantsDetailsLister lister;

    public TenantsDetailsFactory(TenantCriteriaProvider tenantsCriteriaProvider, IBuildingFilterContainer buildingsFilterProvider, String tentantFilterPreset) {
        this.tenantsCriteriaProvider = tenantsCriteriaProvider;
        this.buildingsFilterProvider = buildingsFilterProvider;
        this.filterPreset = tentantFilterPreset;
        this.lister = new TenantsDetailsLister();
    }

    @Override
    public Widget createDetailsWidget() {
        tenantsCriteriaProvider.makeTenantCriteria(new DefaultAsyncCallback<EntityListCriteria<TenantDTO>>() {

            @Override
            public void onSuccess(EntityListCriteria<TenantDTO> result) {
                ListerDataSource<TenantDTO> dataSource = new ListerDataSource<TenantDTO>(TenantDTO.class, GWT
                        .<TenantCrudService> create(TenantCrudService.class));
                List<Criterion> criteria = result.getFilters();
                if (criteria != null) {
                    dataSource.setPreDefinedFilters(criteria);
                }
                lister.setDataSource(dataSource);
                lister.obtain(0);
            }

        }, new Vector<Building>(buildingsFilterProvider.getSelectedBuildingsStubs()), filterPreset);
        return lister;
    }
}
