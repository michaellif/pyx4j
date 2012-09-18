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

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase.CounterDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.customer.lead.AppointmentCrudService;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.AppointmentsCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentsDetailsFactory implements CounterDetailsFactory {

    private final AppointmentsDetailsLister lister;

    private final IBuildingFilterContainer buildingFilterProvider;

    private final String filterPreset;

    private final AppointmentsCriteriaProvider appointmentsCriteriaProvider;

    public AppointmentsDetailsFactory(AppointmentsCriteriaProvider appointmentsCriteriaProvider, IBuildingFilterContainer bulindgFilterProvider,
            String filterPreset) {

        this.lister = new AppointmentsDetailsLister();
        this.appointmentsCriteriaProvider = appointmentsCriteriaProvider;
        this.buildingFilterProvider = bulindgFilterProvider;
        this.filterPreset = filterPreset;

    }

    @Override
    public Widget createDetailsWidget() {
        appointmentsCriteriaProvider.makeAppointmentsCriteria(new DefaultAsyncCallback<EntityListCriteria<Appointment>>() {

            @Override
            public void onSuccess(EntityListCriteria<Appointment> result) {
                ListerDataSource<Appointment> listerDataSource = new ListerDataSource<Appointment>(Appointment.class, GWT
                        .<AbstractListService<Appointment>> create(AppointmentCrudService.class));
                List<Criterion> criteria = result.getFilters();
                if (criteria != null) {
                    listerDataSource.setPreDefinedFilters(criteria);
                } else {
                    listerDataSource.clearPreDefinedFilters();
                }
                lister.setDataSource(listerDataSource);
                lister.obtain(0);

            }

        }, new Vector<Building>(buildingFilterProvider.getSelectedBuildingsStubs()), filterPreset);

        return lister;

    }

}
