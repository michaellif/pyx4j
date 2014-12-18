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
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.dto.gadgets.LeadsAndRentalsGadgetDataDTO;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.AppointmentsCriteriaProvider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.LeadCriteriaProvider;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.filters.LeaseFromLeadCriteriaProvider;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public interface LeadsAndRentalsGadgetService extends AbstractCounterGadgetBaseService<LeadsAndRentalsGadgetDataDTO, Vector<Building>>, LeadCriteriaProvider,
        AppointmentsCriteriaProvider, LeaseFromLeadCriteriaProvider {

    @Override
    public void countData(AsyncCallback<LeadsAndRentalsGadgetDataDTO> callback, Vector<Building> queryParams);

    @Override
    public void makeLeadFilterCriteria(AsyncCallback<EntityListCriteria<Lead>> callback, Vector<Building> buildings, String filterPreset);

    @Override
    public void makeAppointmentsCriteria(AsyncCallback<EntityListCriteria<Appointment>> callback, Vector<Building> buildingsFilter, String filterPreset);

    @Override
    public void makeLeaseFromLeadCriteria(AsyncCallback<EntityListCriteria<Lead>> callback, Vector<Building> buildings, String encodedFilterPreset);

}
