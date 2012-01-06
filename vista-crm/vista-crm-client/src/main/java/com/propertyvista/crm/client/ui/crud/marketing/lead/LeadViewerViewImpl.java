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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadViewerViewImpl extends CrmViewerViewImplBase<Lead> implements LeadViewerView {

    private final Button btnconvert;

    private final IListerView<Appointment> appointmentLister;

    public LeadViewerViewImpl() {
        super(Marketing.Lead.class);

        btnconvert = new Button(i18n.tr("Convert to Lease"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeadViewerView.Presenter) presenter).convertToLease();
            }
        });
        btnconvert.addStyleName(btnconvert.getStylePrimaryName() + CrmTheme.StyleSuffixEx.ActionButton);
        addToolbarItem(btnconvert);

        appointmentLister = new ListerInternalViewImplBase<Appointment>(new AppointmentLister());

        // set main form here: 
        setForm(new LeadEditorForm(new CrmViewersComponentFactory()));
    }

    @Override
    public void populate(Lead value) {
        btnconvert.setVisible(value.lease().isEmpty());
        super.populate(value);
    }

    @Override
    public void onLeaseConvertionSuccess(Lease result) {
        MessageDialog.info(i18n.tr("Information"), i18n.tr("Conversion is succeeded!"));
        btnconvert.setVisible(false);
    }

    @Override
    public boolean onConvertionFail(Throwable caught) {
        if (caught instanceof Error) {
            MessageDialog.error(i18n.tr("Error"), caught.getMessage());
        } else {
            MessageDialog.error(i18n.tr("Error"), i18n.tr("Conversion is failed!"));
        }
        return true;
    }

    @Override
    public IListerView<Appointment> getAppointmentsListerView() {
        return appointmentLister;
    }
}