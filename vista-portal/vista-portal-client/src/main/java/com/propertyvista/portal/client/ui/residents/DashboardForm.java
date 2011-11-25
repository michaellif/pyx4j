/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;

public class DashboardForm extends CEntityDecoratableEditor<TenantDashboardDTO> implements DashboardView {

    private static I18n i18n = I18n.get(DashboardForm.class);

    private Presenter presenter;

    public DashboardForm() {
        super(TenantDashboardDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {

        VerticalPanel container = new VerticalPanel();
        container.setWidth("100%");
        container.setHeight("100%");
        container.add(inject(proto().notifications(), new AlertsViewer()));

        FlowPanel gadgetsPanel = new FlowPanel();
        container.add(gadgetsPanel);
        container.setCellHeight(gadgetsPanel, "100%");

        SimplePanel leftPanelHolder = new SimplePanel();
        SimplePanel leftPanelBorder = new SimplePanel();
        leftPanelBorder.getElement().getStyle().setProperty("minHeight", "500px");

        leftPanelHolder.setWidget(leftPanelBorder);
        FormFlexPanel leftPanel = new FormFlexPanel();
        leftPanelBorder.setWidget(leftPanel);
        gadgetsPanel.add(leftPanelHolder);
        leftPanelHolder.getElement().getStyle().setFloat(Float.LEFT);
        leftPanelHolder.getElement().getStyle().setWidth(50, Unit.PCT);

        leftPanelBorder.getElement().getStyle().setProperty("borderRight", "groove 2px #ddd");

        int row = -1;

        leftPanel.setH1(++row, 0, 1, i18n.tr("General Info"));
        leftPanel.setWidget(++row, 0, new HTML("General Info<p>General Info<p>General Info<p>General Info<p>General Info<p>"));

        leftPanel.setH1(++row, 0, 1, i18n.tr("Current Bill"));
        leftPanel.setWidget(++row, 0, new HTML("Current Bill<p>Current Bill<p>Current Bill<p>"));

        SimplePanel rightPanelHolder = new SimplePanel();
        FormFlexPanel rightPanel = new FormFlexPanel();
        rightPanelHolder.setWidget(rightPanel);
        gadgetsPanel.add(rightPanelHolder);
        rightPanelHolder.getElement().getStyle().setFloat(Float.RIGHT);
        rightPanelHolder.getElement().getStyle().setWidth(50, Unit.PCT);

        row = -1;

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("Maintanance"), newTicket);
        rightPanel.setWidget(++row, 0, new HTML("Maintanance"));

        Anchor newReservations = new Anchor(i18n.tr("New Reservations"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("Reservations"), newReservations);
        rightPanel.setWidget(++row, 0, new HTML("Reservations"));

        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    class AlertsViewer extends CEntityViewer<IList<Message>> {

        @Override
        public IsWidget createContent(IList<Message> value) {
            VerticalPanel container = new VerticalPanel();
            for (Message message : value) {
                HTML html = new HTML(message.subject().getValue());
                container.add(html);
            }
            return container;
        }

    }
}
