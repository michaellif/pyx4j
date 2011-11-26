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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
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
import com.propertyvista.domain.communication.Message.MessageType;
import com.propertyvista.portal.client.resources.PortalImages;
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
        leftPanel.setWidget(++row, 0, inject(proto().notifications(), new CommunicationViewer()));

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

        rightPanel.setWidget(++row, 0, new RateIt(5));

        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    class CommunicationViewer extends CEntityViewer<IList<Message>> {

        @Override
        public IsWidget createContent(IList<Message> value) {

            FlexTable container = new FlexTable();

            container.getElement().getStyle().setProperty("borderCollapse", "collapse");

            container.setWidth("100%");
            container.getColumnFormatter().setWidth(0, "35px");
            container.getColumnFormatter().setWidth(1, "250px");
            container.getColumnFormatter().setWidth(2, "75px");

            container.setHTML(0, 1, i18n.tr("Subject"));
            container.setHTML(0, 2, i18n.tr("Date"));
            container.getRowFormatter().getElement(0).getStyle().setBackgroundColor("#eee");
            container.getRowFormatter().getElement(0).getStyle().setProperty("lineHeight", "35px");
            container.getRowFormatter().getElement(0).getStyle().setProperty("color", "#888");

            int row = 0;

            for (Message message : value) {
                container.setWidget(++row, 0, getIcon(message.type().getValue()));
                container.getCellFormatter().getElement(row, 0).getStyle().setPadding(4, Unit.PX);
                container.setHTML(row, 1, message.subject().getValue());
                container.setHTML(row, 2, message.date().getStringView());
                container.getRowFormatter().getElement(row).getStyle().setProperty("height", "45px");
                container.getRowFormatter().getElement(row).getStyle().setProperty("borderBottom", "dotted 1px #aaa");
            }
            return container;
        }

        private Image getIcon(MessageType messageType) {
            switch (messageType) {
            case communication:
                return new Image(PortalImages.INSTANCE.communicationMessage());
            case maintananceAlert:
                return new Image(PortalImages.INSTANCE.communicationMaintanancee());
            case paymnetPastDue:
            case paymentMethodExpired:
                return new Image(PortalImages.INSTANCE.communicationAlert());
            default:
                break;
            }
            return new Image(PortalImages.INSTANCE.communicationMessage());
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
