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
package com.propertyvista.crm.client.ui.crud.tenant.lead;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Showing;

public class AppointmentViewerViewImpl extends CrmViewerViewImplBase<Appointment> implements AppointmentViewerView {

    private final IListerView<Showing> showingsLister;

    public AppointmentViewerViewImpl() {
        super(CrmSiteMap.Tenants.Appointment.class);

        showingsLister = new ListerInternalViewImplBase<Showing>(new ShowingLister());

        // create/init/set main form here: 
        CrmEntityForm<Appointment> form = new AppointmentEditorForm(new CrmViewersComponentFactory(), this);
        form.initContent();
        setForm(form);
    }

    @Override
    public IListerView<Showing> getShowingsListerView() {
        return showingsLister;
    }
}