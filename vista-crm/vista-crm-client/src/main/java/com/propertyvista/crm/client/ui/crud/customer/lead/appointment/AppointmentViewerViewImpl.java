/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.lead.appointment;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingLister;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentViewerViewImpl extends CrmViewerViewImplBase<Appointment> implements AppointmentViewerView {

    private final IListerView<ShowingDTO> showingsLister;

    public AppointmentViewerViewImpl() {
        super(Marketing.Appointment.class);

        showingsLister = new ListerInternalViewImplBase<ShowingDTO>(new ShowingLister());

        // set main form here:
        setForm(new AppointmentForm(true));
    }

    @Override
    public IListerView<ShowingDTO> getShowingsListerView() {
        return showingsLister;
    }

    @Override
    public void populate(Appointment value) {
        super.populate(value);

        getEditButton().setEnabled(value.status().getValue() != Appointment.Status.complete);
        getEditButton().setVisible(value.lead().status().getValue() != Lead.Status.closed);
    }
}