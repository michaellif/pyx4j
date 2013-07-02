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
package com.propertyvista.portal.web.client.ui.residents.dashboard;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.domain.dto.financial.FinancialSummaryDTO;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.dto.ReservationDTO;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.web.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.web.client.ui.components.CurrentBalanceFormat;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.shared.config.VistaFeatures;

public class DashboardForm extends CEntityDecoratableForm<TenantDashboardDTO> {

    private static final I18n i18n = I18n.get(DashboardForm.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private DashboardView.Presenter presenter;

    private Button payButton;

    public DashboardForm() {
        super(TenantDashboardDTO.class, new VistaViewersComponentFactory());
    }

    public void setPresenter(DashboardView.Presenter presenter) {
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

        leftPanel.setH1(++row, 0, 1, i18n.tr("RENT BALANCE"));
        leftPanel.setWidget(++row, 0, inject(proto().billSummary(), new FinancialSummaryViewer()));
        get(proto().billSummary()).asWidget().addStyleName(TenantDashboardTheme.StyleName.TenantDashboardSection.name());
        get(proto().billSummary()).setHeight("");

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
                AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance.NewMaintenanceRequest());
            }
        });

        rightPanel.setH1(++row, 0, 1, i18n.tr("MAINTENANCE"), newTicket);
        rightPanel.setWidget(++row, 0, inject(proto().maintanances(), new MaintananceViewer()));
        get(proto().maintanances()).asWidget().addStyleName(TenantDashboardTheme.StyleName.TenantDashboardSection.name());
        get(proto().maintanances()).setHeight("");

        if (false) {
            Anchor newReservations = new Anchor(i18n.tr("Order Service"));
            rightPanel.setH1(++row, 0, 1, i18n.tr("SERVICES"), newReservations);
            rightPanel.setWidget(++row, 0, inject(proto().reservations(), new ReservationsViewer()));
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            rightPanel.setH1(++row, 0, 1, i18n.tr("TENANT INSURANCE"));
            rightPanel.setWidget(++row, 0, inject(proto().tenantInsuranceStatus(), new TenantInsuranceStatusViewer()));
            get(proto().tenantInsuranceStatus()).asWidget().addStyleName(TenantDashboardTheme.StyleName.TenantDashboardSection.name());
            get(proto().tenantInsuranceStatus()).setHeight("");
        }
        return container;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        payButton.setVisible(SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values()));
    }

    class FinancialSummaryViewer extends CEntityViewer<FinancialSummaryDTO> {

        private final CurrentBalanceFormat currentBalanceFormat = new CurrentBalanceFormat();

        @Override
        public IsWidget createContent(FinancialSummaryDTO value) {
            FlexTable dataPanel = new FlexTable();

            dataPanel.setWidth("100%");
            dataPanel.getColumnFormatter().setWidth(0, "250px");
            dataPanel.getColumnFormatter().setWidth(1, "75px");

            int row = -1;

            dataPanel.setHTML(++row, 0, value.currentBalance().getMeta().getCaption());
            dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
            dataPanel.setHTML(row, 1, currentBalanceFormat.format(value.currentBalance().getValue()));

            // TODO wrong polymorphism
            if (value.isInstanceOf(PvBillingFinancialSummaryDTO.class)) {
                PvBillingFinancialSummaryDTO pvBillingSumarry = value.duplicate(PvBillingFinancialSummaryDTO.class);
                dataPanel.setHTML(++row, 0, pvBillingSumarry.currentBill().dueDate().getMeta().getCaption());
                dataPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
                dataPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
                dataPanel.setHTML(row, 1, pvBillingSumarry.currentBill().dueDate().getStringView());
            }
            VerticalPanel content = new VerticalPanel();
            content.add(dataPanel);
            content.setCellWidth(dataPanel, "100%");

            HorizontalPanel actions = new HorizontalPanel();
            actions.getElement().getStyle().setMargin(1, Unit.EM);

            // TODO wrong polymorphism
            if (value.isInstanceOf(PvBillingFinancialSummaryDTO.class)) {
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
            }

            payButton = new Button(i18n.tr("Pay Now"), new Command() {
                @Override
                public void execute() {
                    // TODO Auto-generated method stub
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
                    container.setHTML(++row, 0, issueDetail(mr));
                    container.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(1, Unit.EM);
                    if (MaintenanceRequestStatus.StatusPhase.Resolved.equals(mr.status().phase().getValue())) {
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
            ScrollPanel scrollPanel = new ScrollPanel(container);
            scrollPanel.setSize("100%", "100%");
            return scrollPanel;
        }

        private String issueDetail(MaintenanceRequestDTO request) {
            try {
                return request.summary().getStringView();
            } catch (Exception ignore) {
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
