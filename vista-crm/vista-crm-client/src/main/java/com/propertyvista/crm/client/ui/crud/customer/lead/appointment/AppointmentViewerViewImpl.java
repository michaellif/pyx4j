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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerViewImpl;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentViewerViewImpl extends CrmViewerViewImplBase<Appointment> implements AppointmentViewerView {

    private static final I18n i18n = I18n.get(AppointmentViewerViewImpl.class);

    private final ShowingListerView showingsLister;

    private final MenuItem closeAction;

    public AppointmentViewerViewImpl() {
        showingsLister = new ShowingListerViewImpl();

        closeAction = new SecureMenuItem(i18n.tr("Close"), new Command() {
            @Override
            public void execute() {
                new ReasonBox(i18n.tr("Close Appointment")) {
                    @Override
                    public boolean onClickOk() {
                        if (CommonsStringUtils.isEmpty(getReason())) {
                            MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                            return false;
                        }
                        ((AppointmentViewerView.Presenter) getPresenter()).close(getReason());
                        return true;
                    }
                }.show();
            }
        }, DataModelPermission.permissionUpdate(Appointment.class));
        addAction(closeAction);

        // set main form here:
        setForm(new AppointmentForm(this));
    }

    @Override
    public ShowingListerView getShowingsListerView() {
        return showingsLister;
    }

    @Override
    public void reset() {
        setActionVisible(closeAction, false);
        super.reset();
    }

    @Override
    public void populate(Appointment value) {
        super.populate(value);

        setEditingEnabled(value.status().getValue() != Appointment.Status.closed);
        setEditingVisible(value.lead().status().getValue() != Lead.Status.closed);

        setActionVisible(closeAction, value.status().getValue() != Appointment.Status.closed);
    }
}