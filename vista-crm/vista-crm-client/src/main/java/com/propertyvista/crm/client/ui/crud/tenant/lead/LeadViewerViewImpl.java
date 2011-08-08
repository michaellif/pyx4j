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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeadViewerViewImpl extends CrmViewerViewImplBase<Lead> implements LeadViewerView {

    private final AnchorButton btnconvert;

    private final IListerView<Appointment> appointmentLister;

    public LeadViewerViewImpl() {
        super(CrmSiteMap.Tenants.Lead.class);

        btnconvert = new AnchorButton(i18n.tr("Convert to Lease"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeadViewerView.Presenter) presenter).convertToLease();
            }
        });
        btnconvert.addStyleName(btnconvert.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.ActionButton);
        btnconvert.setWordWrap(false);
        addActionButton(btnconvert);

        appointmentLister = new ListerInternalViewImplBase<Appointment>(new AppointmentLister());

        // create/init/set main form here: 
        CrmEntityForm<Lead> form = new LeadEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
    }

    @Override
    public void populate(Lead value) {
        btnconvert.setVisible(!value.convertedToLease().isBooleanTrue());
        super.populate(value);
    }

    @Override
    public void onLeaseConvertionSuccess(Lease result) {
        MessageDialog.info("Information", "Conversion is succeeded!");
        btnconvert.setVisible(false);
    }

    @Override
    public boolean onConvertionFail(Throwable caught) {
        if (caught instanceof Error) {
            MessageDialog.error("Error", caught.getMessage());
        } else {
            MessageDialog.error("Error", "Conversion is failed!");
        }
        return true;
    }

    @Override
    public IListerView<Appointment> getAppointmentsListerView() {
        return appointmentLister;
    }
}