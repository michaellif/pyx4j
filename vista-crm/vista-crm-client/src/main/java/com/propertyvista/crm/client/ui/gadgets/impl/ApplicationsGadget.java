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
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.ApplicationsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.ApplicationsGadgetSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ApplicationsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.ApplicationsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.LeaseApplicationDTO;

public class ApplicationsGadget extends CounterGadgetInstanceBase<ApplicationsGadgetDataDTO, Vector<Building>, ApplicationsGadgetMetadata> {

    public ApplicationsGadget(ApplicationsGadgetMetadata metadata) {
        super(ApplicationsGadgetDataDTO.class, GWT.<ApplicationsGadgetService> create(ApplicationsGadgetService.class), new ApplicationsGadgetSummaryForm(),
                metadata, ApplicationsGadgetMetadata.class);
    }

    @Override
    protected Vector<Building> makeSummaryQuery() {
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
        bindDetailsFactory(member, new ApplicationsDetailsFactory(this, criteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().applicationsListerSettings();
            }

            @Override
            public void save() {
                saveMetadata();
            }

            @Override
            public boolean isModifiable() {
                return ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(getMetadata().ownerUser().getPrimaryKey());
            }
        }));
    }
}
