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
package com.propertyvista.crm.client.ui.gadgets.impl;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.components.AppointmentsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.LeadsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.LeasesFromLeadsDetailsFactory;
import com.propertyvista.crm.client.ui.gadgets.components.details.CounterGadgetFilter;
import com.propertyvista.crm.client.ui.gadgets.components.details.ICriteriaProvider;
import com.propertyvista.crm.client.ui.gadgets.forms.LeadsAndRentalsSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Proxy;
import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.LeadsAndRentalsGadgetService;
import com.propertyvista.domain.dashboard.gadgets.type.LeadsAndRentalsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadsAndRentalsGadget extends CounterGadgetInstanceBase<LeadsAndRentalsGadgetDataDTO, Vector<Building>, LeadsAndRentalsGadgetMetadata> {

    public LeadsAndRentalsGadget(LeadsAndRentalsGadgetMetadata metadata) {
        super(//@formatter:off
                LeadsAndRentalsGadgetDataDTO.class,
                GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class),
                new LeadsAndRentalsSummaryForm(),
                metadata,
                LeadsAndRentalsGadgetMetadata.class
        );//@formatter:on
    }

    @Override
    protected Vector<Building> makeSummaryQuery() {
        return new Vector<Building>(buildingsFilterContainer.getSelectedBuildingsStubs());
    }

    @Override
    protected void bindDetailsFactories() {
        ICriteriaProvider<Lead, CounterGadgetFilter> leadsCriteriaProvider = new ICriteriaProvider<Lead, CounterGadgetFilter>() {

            @Override
            public void makeCriteria(final AsyncCallback<EntityListCriteria<Lead>> callback, CounterGadgetFilter filterData) {
                GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class).makeLeadFilterCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(proto().leads(), new LeadsDetailsFactory(this, leadsCriteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().leadsListerDetails();
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

        ICriteriaProvider<Appointment, CounterGadgetFilter> appointmentCriteriaProvider = new ICriteriaProvider<Appointment, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<Appointment>> callback, CounterGadgetFilter filterData) {
                GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class).makeAppointmentsCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(proto().appointmentsLabel(), new AppointmentsDetailsFactory(this, appointmentCriteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().appointmentsListerDetails();
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

        ICriteriaProvider<Lead, CounterGadgetFilter> leaseFromLeadCriteriaProvider = new ICriteriaProvider<Lead, CounterGadgetFilter>() {
            @Override
            public void makeCriteria(AsyncCallback<EntityListCriteria<Lead>> callback, CounterGadgetFilter filterData) {
                GWT.<LeadsAndRentalsGadgetService> create(LeadsAndRentalsGadgetService.class).makeLeaseFromLeadCriteria(callback, filterData.getBuildings(),
                        filterData.getCounterMember().toString());
            }
        };
        bindDetailsFactory(proto().rentalsLabel(), new LeasesFromLeadsDetailsFactory(this, leaseFromLeadCriteriaProvider, new Proxy<ListerUserSettings>() {

            @Override
            public ListerUserSettings get() {
                return getMetadata().leasesFromLeadsListerSettings();
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
