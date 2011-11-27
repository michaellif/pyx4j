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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.Message.MessageType;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.rpc.portal.dto.BillInfoDTO;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;
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

        HorizontalPanel container = new HorizontalPanel();
        container.setStyleName(TenantDashboardTheme.StyleName.TenantDashboard.name());

        SimplePanel leftPanelHolder = new SimplePanel();
        SimplePanel leftPanelBorder = new SimplePanel();
        leftPanelHolder.setStyleName(TenantDashboardTheme.StyleName.TenantDashboardLeft.name());

        leftPanelHolder.setWidget(leftPanelBorder);

        FormFlexPanel leftPanel = new FormFlexPanel();
        leftPanelBorder.setWidget(leftPanel);
        container.add(leftPanelHolder);
        container.setCellWidth(leftPanelHolder, "50%");
        container.setCellHeight(leftPanelHolder, "100%");

        int row = -1;

        leftPanel.setH1(++row, 0, 1, i18n.tr("COMMUNICATION"));
        leftPanel.setWidget(++row, 0, inject(proto().notifications(), new CommunicationViewer()));

        SimplePanel rightPanelHolder = new SimplePanel();
        rightPanelHolder.setStyleName(TenantDashboardTheme.StyleName.TenantDashboardRight.name());

        FormFlexPanel rightPanel = new FormFlexPanel();
        rightPanelHolder.setWidget(rightPanel);
        container.add(rightPanelHolder);
        container.setCellWidth(rightPanelHolder, "50%");
        container.setCellHeight(rightPanelHolder, "100%");

        row = -1;

        if (false) {
            rightPanel.setH1(++row, 0, 1, i18n.tr("GENERAL INFO"));
            rightPanel.setWidget(++row, 0, inject(proto().general(), new GeneralInfoViewer()));
        }

        rightPanel.setH1(++row, 0, 1, i18n.tr("CURRENT BILL"));
        rightPanel.setWidget(++row, 0, inject(proto().currentBill(), new CurrentBillViewer()));

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("MAINTANANCE"), newTicket);
        rightPanel.setWidget(++row, 0, inject(proto().maintanances(), new MaintananceViewer()));

        Anchor newReservations = new Anchor(i18n.tr("Order Service"));
        rightPanel.setH1(++row, 0, 1, i18n.tr("SERVICES"), newReservations);
        rightPanel.setWidget(++row, 0, inject(proto().reservations(), new ReservationsViewer()));

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

            container.setWidth("100%");
            container.getColumnFormatter().setWidth(0, "35px");
            container.getColumnFormatter().setWidth(1, "250px");
            container.getColumnFormatter().setWidth(2, "75px");

            container.setHTML(0, 1, i18n.tr("Subject"));
            container.setHTML(0, 2, i18n.tr("Date"));
            container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            int row = 0;

            for (Message message : value) {
                container.setWidget(++row, 0, getIcon(message.type().getValue()));
                container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
                container.setHTML(row, 1, message.subject().getValue());
                container.setHTML(row, 2, message.date().getStringView());
                container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
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

            VerticalPanel content = new VerticalPanel();
            content.setWidth("100%");

            FlexTable dataPanel = new FlexTable();

            dataPanel.setWidth("100%");
            dataPanel.getColumnFormatter().setWidth(0, "250px");
            dataPanel.getColumnFormatter().setWidth(1, "75px");

            int row = 0;

            dataPanel.setHTML(++row, 0, value.ammount().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            dataPanel.setHTML(row, 1, "$" + value.ammount().amount().getValue());

            dataPanel.setHTML(++row, 0, value.dueDate().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            dataPanel.setHTML(row, 1, value.dueDate().getStringView());

            dataPanel.setHTML(++row, 0, value.lastPayment().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            dataPanel.setHTML(row, 1, "$" + value.lastPayment().amount().getValue());

            dataPanel.setHTML(++row, 0, value.receivedOn().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            dataPanel.setHTML(row, 1, value.receivedOn().getStringView());

            content.add(dataPanel);
            content.setCellWidth(dataPanel, "100%");

            HorizontalPanel actions = new HorizontalPanel();
            actions.getElement().getStyle().setMargin(20, Unit.PX);

            Anchor viewBill = new Anchor(i18n.tr("View Bill"));
            viewBill.getElement().getStyle().setPaddingRight(20, Unit.PX);
            actions.add(viewBill);

            Button payButton = new Button(i18n.tr("Pay Now"));
            actions.add(payButton);

            content.add(actions);
            content.setCellHorizontalAlignment(actions, HorizontalPanel.ALIGN_RIGHT);

            return content;
        }
    }

    class MaintananceViewer extends CEntityViewer<IList<MaintananceDTO>> {

        @Override
        public IsWidget createContent(IList<MaintananceDTO> value) {
            FlexTable container = new FlexTable();

            container.setWidth("100%");
            container.getColumnFormatter().setWidth(0, "250px");
            container.getColumnFormatter().setWidth(1, "75px");

            container.setHTML(0, 0, i18n.tr("Ticket"));
            container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(4, Unit.PX);
            container.setHTML(0, 1, i18n.tr("Status"));
            container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            int row = 0;

            for (MaintananceDTO maintanance : value) {
                container.setHTML(++row, 0, maintanance.description().getValue());
                container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
                if (MaintananceDTO.Status.Completed.equals(maintanance.status().getValue())) {
                    RateIt rateIt = new RateIt(5);
                    rateIt.setRating(4);
                    container.setWidget(row, 1, rateIt);
                } else {
                    container.setHTML(row, 1, maintanance.status().getValue().name() + "<p><i style='font-size:0.8em'>" + maintanance.date().getStringView()
                            + "</i>");
                }
                container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }
            return container;

        }

    }

    class ReservationsViewer extends CEntityViewer<IList<ReservationDTO>> {

        @Override
        public IsWidget createContent(IList<ReservationDTO> value) {
            FlexTable container = new FlexTable();

            container.setWidth("100%");
            container.getColumnFormatter().setWidth(0, "250px");
            container.getColumnFormatter().setWidth(1, "75px");

            container.setHTML(0, 0, i18n.tr("Service"));
            container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(4, Unit.PX);
            container.setHTML(0, 1, i18n.tr("Status"));
            container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            int row = 0;

            for (ReservationDTO reservation : value) {
                container.setHTML(++row, 0, reservation.description().getValue());
                container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
                container.setHTML(row, 1, reservation.status().getValue().name() + "<p><i style='font-size:0.8em'>" + reservation.date().getStringView()
                        + "</i>");
                container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }
            return container;
        }

    }

}
