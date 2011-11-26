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
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.portal.rpc.portal.dto.BillInfoDTO;
import com.propertyvista.portal.rpc.portal.dto.ReservationDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantGeneralInfoDTO;

public class DashboardForm extends CEntityDecoratableEditor<TenantDashboardDTO> implements DashboardView {

    private static I18n i18n = I18n.get(DashboardForm.class);

    private Presenter presenter;

    public DashboardForm() {
        super(TenantDashboardDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {

        FlowPanel container = new FlowPanel();
        container.setHeight("100%");

        SimplePanel leftPanelHolder = new SimplePanel();
        SimplePanel leftPanelBorder = new SimplePanel();
        leftPanelBorder.getElement().getStyle().setProperty("minHeight", "500px");

        leftPanelHolder.setWidget(leftPanelBorder);
        FormFlexPanel leftPanel = new FormFlexPanel();
        leftPanelBorder.setWidget(leftPanel);
        container.add(leftPanelHolder);
        leftPanelHolder.getElement().getStyle().setFloat(Float.LEFT);
        leftPanelHolder.getElement().getStyle().setWidth(50, Unit.PCT);

        leftPanelBorder.getElement().getStyle().setProperty("borderRight", "groove 1px #666");

        int row = -1;

        leftPanel.setH1(++row, 0, 1, i18n.tr("COMMUNICATION"));
        leftPanel.setWidget(++row, 0, inject(proto().notifications(), new AlertsViewer()));

        SimplePanel rightPanelHolder = new SimplePanel();
        FormFlexPanel rightPanel = new FormFlexPanel();
        rightPanelHolder.setWidget(rightPanel);
        container.add(rightPanelHolder);
        rightPanelHolder.getElement().getStyle().setFloat(Float.RIGHT);
        rightPanelHolder.getElement().getStyle().setWidth(50, Unit.PCT);

        row = -1;

        if (false) {
            rightPanel.setH1(++row, 0, 1, i18n.tr("GENERAL INFO"));
            rightPanel.setWidget(++row, 0, inject(proto().general(), new GeneralInfoViewer()));
        }

        rightPanel.setH1(++row, 0, 1, i18n.tr("CURRENT BILL"));
        rightPanel.setWidget(++row, 0, inject(proto().currentBill(), new CurrentBillViewer()));

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("MAINTANANCE"), newTicket);
        rightPanel.setWidget(++row, 0, inject(proto().reservations(), new MaintananceViewer()));

        Anchor newReservations = new Anchor(i18n.tr("Order Service"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("SERVICES"), newReservations);
        rightPanel.setWidget(++row, 0, inject(proto().reservations(), new ReservationsViewer()));

        rightPanel.setWidget(++row, 0, new RateIt(2.5, 5));

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
                HTML html = new HTML(message.subject().getValue() + "-" + message.text().getValue() + "-" + message.type().getValue());

                html.getElement().getStyle().setProperty("height", "5em");

                container.add(html);
            }
            return container;
        }

    }

    class GeneralInfoViewer extends CEntityViewer<TenantGeneralInfoDTO> {

        @Override
        public IsWidget createContent(TenantGeneralInfoDTO value) {
            HTML content = new HTML(value.tenantName().getValue() + "<p>" + value.floorplanName().getValue() + "<p>" + value.tenantAddress().getValue() + "<p>"
                    + value.superIntendantPhone().getValue());
            content.getElement().getStyle().setPadding(20, Unit.PX);
            return content;
        }
    }

    class CurrentBillViewer extends CEntityViewer<BillInfoDTO> {

        @Override
        public IsWidget createContent(BillInfoDTO value) {
            HTML content = new HTML(value.message().getValue() + "<p>" + value.dueDate().getValue() + "<p>" + value.ammount().amount().getValue());
            content.getElement().getStyle().setPadding(20, Unit.PX);
            return content;
        }

    }

    class MaintananceViewer extends CEntityViewer<IList<ReservationDTO>> {

        @Override
        public IsWidget createContent(IList<ReservationDTO> value) {
            VerticalPanel content = new VerticalPanel();
            for (ReservationDTO reservation : value) {
                HTML html = new HTML(reservation.description().getValue() + "-" + reservation.date().getValue() + "-" + reservation.time().getValue());
                content.add(html);
            }
            content.getElement().getStyle().setPadding(20, Unit.PX);
            return content;
        }

    }

    class ReservationsViewer extends CEntityViewer<IList<ReservationDTO>> {

        @Override
        public IsWidget createContent(IList<ReservationDTO> value) {
            VerticalPanel content = new VerticalPanel();
            for (ReservationDTO reservation : value) {
                HTML html = new HTML(reservation.description().getValue() + "-" + reservation.date().getValue() + "-" + reservation.time().getValue());
                content.add(html);
            }
            content.getElement().getStyle().setPadding(20, Unit.PX);
            return content;
        }

    }

}
