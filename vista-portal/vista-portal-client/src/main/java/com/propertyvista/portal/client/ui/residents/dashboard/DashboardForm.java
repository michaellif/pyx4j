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
package com.propertyvista.portal.client.ui.residents.dashboard;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.Message.MessageType;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.domain.dto.BillSummaryDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.ReservationDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantGeneralInfoDTO;

public class DashboardForm extends CEntityDecoratableForm<TenantDashboardDTO> implements DashboardView {

    private static final I18n i18n = I18n.get(DashboardForm.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private Presenter presenter;

    public DashboardForm() {
        super(TenantDashboardDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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

        if (false) {
            leftPanel.setH1(++row, 0, 1, i18n.tr("COMMUNICATION"));
            leftPanel.setWidget(++row, 0, inject(proto().notifications(), new CommunicationViewer()));
        }

        leftPanel.setH1(++row, 0, 1, i18n.tr("GENERAL INFO"));
        leftPanel.setWidget(++row, 0, inject(proto().general(), new GeneralInfoViewer()));

        leftPanel.setH1(++row, 0, 1, i18n.tr("BILL SUMMARY"));
        leftPanel.setWidget(++row, 0, inject(proto().billSummary(), new BillSummaryViewer()));

        // =============================================================================================

        SimplePanel rightPanelHolder = new SimplePanel();
        rightPanelHolder.setStyleName(TenantDashboardTheme.StyleName.TenantDashboardRight.name());

        FormFlexPanel rightPanel = new FormFlexPanel();
        rightPanelHolder.setWidget(rightPanel);
        container.add(rightPanelHolder);
        container.setCellWidth(rightPanelHolder, "50%");
        container.setCellHeight(rightPanelHolder, "100%");

        row = -1;

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        newTicket.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance.NewMaintenanceRequest());
            }
        });
        rightPanel.setH1(++row, 0, 1, i18n.tr("MAINTENANCE"), newTicket);
        rightPanel.setWidget(++row, 0, inject(proto().maintanances(), new MaintananceViewer()));

        if (false) {
            Anchor newReservations = new Anchor(i18n.tr("Order Service"));
            rightPanel.setH1(++row, 0, 1, i18n.tr("SERVICES"), newReservations);
            rightPanel.setWidget(++row, 0, inject(proto().reservations(), new ReservationsViewer()));
        }

        return container;
    }

    class CommunicationViewer extends CEntityViewer<IList<Message>> {
        @Override
        public IsWidget createContent(IList<Message> value) {
            FlexTable container = new FlexTable();

            if (value.size() > 0) {
                container.getColumnFormatter().setWidth(0, "35px");
                container.getColumnFormatter().setWidth(1, "250px");
                container.getColumnFormatter().setWidth(2, "75px");

                container.setHTML(0, 1, i18n.tr("Subject"));
                container.setHTML(0, 2, i18n.tr("Date"));
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

                int row = 0;
                for (Message message : value) {
                    container.setWidget(++row, 0, getIcon(message.type().getValue()));
                    container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
                    container.setHTML(row, 1, message.subject().getValue());
                    container.setHTML(row, 2, message.date().getStringView());
                    container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
                }
            } else {
                container.setHTML(0, 0, NoRecordsFound);
                container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(1, Unit.EM);
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());
                container.setHTML(1, 0, "");
                container.getRowFormatter().getElement(1).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }

            container.setWidth("100%");
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
            FlexTable content = new FlexTable();
            int row = -1;

            content.setHTML(++row, 0, value.tenantName().getValue());
            content.setHTML(++row, 0, value.floorplanName().getValue());
            content.setHTML(++row, 0, value.tenantAddress().getValue());

            content.getElement().getStyle().setMargin(1, Unit.EM);
            return content;
        }
    }

    class BillSummaryViewer extends CEntityViewer<BillSummaryDTO> {
        @Override
        public IsWidget createContent(BillSummaryDTO value) {
            FlexTable dataPanel = new FlexTable();

            dataPanel.setWidth("100%");
            dataPanel.getColumnFormatter().setWidth(0, "250px");
            dataPanel.getColumnFormatter().setWidth(1, "75px");

            int row = -1;

            dataPanel.setHTML(++row, 0, value.currentBalance().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
            dataPanel.setHTML(row, 1, "$" + value.currentBalance().getValue());

            dataPanel.setHTML(++row, 0, value.currentBill().dueDate().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
            dataPanel.setHTML(row, 1, value.currentBill().dueDate().getStringView());

            VerticalPanel content = new VerticalPanel();
            content.add(dataPanel);
            content.setCellWidth(dataPanel, "100%");

            HorizontalPanel actions = new HorizontalPanel();
            actions.getElement().getStyle().setMargin(1, Unit.EM);

            Anchor viewBill = new Anchor(i18n.tr("View Current Bill")) {
            };
            viewBill.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.viewCurrentBill();
                }
            });
            viewBill.getElement().getStyle().setPaddingRight(20, Unit.PX);
            actions.add(viewBill);

            Button payButton = new Button(i18n.tr("Pay Now"));
            payButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.payNow();
                }
            });
            actions.add(payButton);

            content.add(actions);
            content.setCellHorizontalAlignment(actions, HorizontalPanel.ALIGN_RIGHT);
            content.setWidth("100%");
            return content;
        }
    }

    class MaintananceViewer extends CEntityViewer<IList<MaintenanceRequestDTO>> {
        @Override
        public IsWidget createContent(IList<MaintenanceRequestDTO> value) {
            FlexTable container = new FlexTable();

            if (value.size() > 0) {
                container.getColumnFormatter().setWidth(0, "250px");
                container.getColumnFormatter().setWidth(1, "75px");

                container.setHTML(0, 0, i18n.tr("Ticket"));
                container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(1, Unit.EM);
                container.setHTML(0, 1, i18n.tr("Status"));
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

                int row = 0;
                for (MaintenanceRequestDTO mr : value) {
                    container.setHTML(++row, 0, issueDetail(mr.issueClassification()));
                    container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
                    if (MaintenanceRequestStatus.Resolved.equals(mr.status().getValue())) {
                        RateIt rateIt = new RateIt(5);
                        rateIt.setRating(4);
                        container.setWidget(row, 1, rateIt);
                    } else {
                        container.setHTML(row, 1, mr.status().getStringView() + "<p><i style='font-size:0.8em'>" + mr.submitted().getStringView() + "</i>");
                    }
                    container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
                }
            } else {
                container.setHTML(0, 0, NoRecordsFound);
                container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(1, Unit.EM);
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());
                container.setHTML(1, 0, "");
                container.getRowFormatter().getElement(1).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }

            container.setWidth("100%");
            return container;
        }

        private String issueDetail(IssueClassification ic) {
            if (!ic.issue().isNull()) {
                return ic.issue().getValue();
            } else if (!ic.subjectDetails().name().isNull()) {
                return ic.subjectDetails().name().getValue();
            } else if (!ic.subjectDetails().subject().name().isNull()) {
                return ic.subjectDetails().subject().name().getValue();
            } else if (!ic.subjectDetails().subject().issueElement().name().isNull()) {
                return ic.subjectDetails().subject().issueElement().name().getValue();
            } else {
                return "Invalid Entry";
            }
        }
    }

    class ReservationsViewer extends CEntityViewer<IList<ReservationDTO>> {
        @Override
        public IsWidget createContent(IList<ReservationDTO> value) {
            FlexTable container = new FlexTable();

            if (value.size() > 0) {
                container.getColumnFormatter().setWidth(0, "250px");
                container.getColumnFormatter().setWidth(1, "75px");

                container.setHTML(0, 0, i18n.tr("Service"));
                container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(1, Unit.EM);
                container.setHTML(0, 1, i18n.tr("Status"));
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

                int row = 0;
                for (ReservationDTO reservation : value) {
                    container.setHTML(++row, 0, reservation.description().getValue());
                    container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
                    container.setHTML(row, 1, reservation.status().getStringView() + "<p><i style='font-size:0.8em'>" + reservation.date().getStringView()
                            + "</i>");
                    container.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
                }
            } else {
                container.setHTML(0, 0, NoRecordsFound);
                container.getCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(1, Unit.EM);
                container.getRowFormatter().getElement(0).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());
                container.setHTML(1, 0, "");
                container.getRowFormatter().getElement(1).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }

            container.setWidth("100%");
            return container;
        }
    }
}
