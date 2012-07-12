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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerView;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerViewImpl;
import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class AppointmentViewerViewImpl extends CrmViewerViewImplBase<Appointment> implements AppointmentViewerView {

    private static final I18n i18n = I18n.get(AppointmentViewerViewImpl.class);

    private final ShowingListerView showingsLister;

    private final Button closeAction;

    public AppointmentViewerViewImpl() {
        super(Marketing.Appointment.class);

        showingsLister = new ShowingListerViewImpl();

        closeAction = new Button(i18n.tr("Close"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ActionBox(i18n.tr("Close Appointment")) {
                    @Override
                    public boolean onClickOk() {
                        ((AppointmentViewerView.Presenter) getPresenter()).close(getReason());
                        return true;
                    }
                }.show();
            }
        });
        addHeaderToolbarTwoItem(closeAction.asWidget());

        // set main form here:
        setForm(new AppointmentForm(true));
    }

    @Override
    public ShowingListerView getShowingsListerView() {
        return showingsLister;
    }

    @Override
    public void populate(Appointment value) {
        super.populate(value);

        getEditButton().setEnabled(value.status().getValue() != Appointment.Status.closed);
        getEditButton().setVisible(value.lead().status().getValue() != Lead.Status.closed);

        closeAction.setVisible(value.status().getValue() != Appointment.Status.closed);
    }

    private abstract class ActionBox extends OkCancelDialog {

        private final CTextArea reason = new CTextArea();

        public ActionBox(String title) {
            super(title);
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please fill the reason") + ":"));
            content.add(reason);

            reason.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public String getReason() {
            return reason.getValue();
        }
    }
}