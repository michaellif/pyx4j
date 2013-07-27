/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.web.client.themes.TenantDashboardTheme;

public class MaintenanceViewList extends VerticalPanel implements MaintenanceView {

    private static final I18n i18n = I18n.get(MaintenanceViewList.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private MaintenanceView.Presenter presenter;

    private final TwoColumnFlexFormPanel openRequestsPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel historyRequestsPanel = new TwoColumnFlexFormPanel();

    public MaintenanceViewList() {
        setWidth("100%");

        add(openRequestsPanel);
        openRequestsPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);

        add(historyRequestsPanel);
        historyRequestsPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populateOpenRequests(Vector<MaintenanceRequestDTO> openRequests) {
        openRequestsPanel.removeAllRows();
        int row = -1;

        openRequestsPanel.setH1(++row, 0, 5, i18n.tr("OPEN TICKETS"), newTicketAction());
        openRequestsPanel.setWidth("100%");

        int col = -1;
        if (openRequests.size() > 0) {
            openRequestsPanel.getColumnFormatter().setWidth(++col, "50px");
            openRequestsPanel.getColumnFormatter().setWidth(++col, "130px");
            openRequestsPanel.getColumnFormatter().setWidth(++col, "130px");
            openRequestsPanel.getColumnFormatter().setWidth(++col, "40px");
            openRequestsPanel.getColumnFormatter().setWidth(++col, "50px");

            row += 1;
            col = -1;
            openRequestsPanel.setHTML(row, ++col, i18n.tr("Id"));
            openRequestsPanel.getCellFormatter().getElement(row, col).getStyle().setPaddingLeft(4, Unit.PX);
            openRequestsPanel.setHTML(row, ++col, i18n.tr("Details"));
            openRequestsPanel.setHTML(row, ++col, i18n.tr("Summary"));
            openRequestsPanel.setHTML(row, ++col, i18n.tr("Status"));
            openRequestsPanel.setHTML(row, ++col, "Actions");

            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintenanceRequestDTO request : openRequests) {
                row += 1;
                col = -1;
                openRequestsPanel.setHTML(row, ++col, request.requestId().getStringView());
                openRequestsPanel.getCellFormatter().getElement(row, col).getStyle().setPaddingLeft(4, Unit.PX);
                openRequestsPanel.setHTML(row, ++col, issueDetails(request));
                openRequestsPanel.setHTML(row, ++col, request.summary().getStringView());
                openRequestsPanel.setHTML(row, ++col, issueStatus(request));
                openRequestsPanel.setWidget(row, ++col, issueActions(request));

                openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }
        } else {
            openRequestsPanel.setHTML(++row, 0, NoRecordsFound);
            openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());
            openRequestsPanel.setHTML(++row, 0, "");
            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
        }
    }

    private Widget newTicketAction() {
        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        newTicket.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.createNewRequest();
            }
        });

        return newTicket;
    }

    @Override
    public void populateClosedRequests(Vector<MaintenanceRequestDTO> historyRequests) {
        historyRequestsPanel.removeAllRows();
        int row = -1;

        historyRequestsPanel.setH1(++row, 0, 5, i18n.tr("HISTORY"));
        historyRequestsPanel.setWidth("100%");

        int col = -1;
        if (historyRequests.size() > 0) {
            historyRequestsPanel.getColumnFormatter().setWidth(++col, "50px");
            historyRequestsPanel.getColumnFormatter().setWidth(++col, "130px");
            historyRequestsPanel.getColumnFormatter().setWidth(++col, "130px");
            historyRequestsPanel.getColumnFormatter().setWidth(++col, "40px");
            historyRequestsPanel.getColumnFormatter().setWidth(++col, "50px");

            row += 1;
            col = -1;
            historyRequestsPanel.setHTML(row, ++col, i18n.tr("Id"));
            historyRequestsPanel.getCellFormatter().getElement(row, col).getStyle().setPaddingLeft(4, Unit.PX);
            historyRequestsPanel.setHTML(row, ++col, i18n.tr("Details"));
            historyRequestsPanel.setHTML(row, ++col, i18n.tr("Summary"));
            historyRequestsPanel.setHTML(row, ++col, i18n.tr("Status"));
            historyRequestsPanel.setHTML(row, ++col, i18n.tr("Rate Service"));
            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintenanceRequestDTO request : historyRequests) {
                row += 1;
                col = -1;
                historyRequestsPanel.setHTML(row, ++col, request.requestId().getStringView());
                historyRequestsPanel.getCellFormatter().getElement(row, col).getStyle().setPaddingLeft(4, Unit.PX);
                historyRequestsPanel.setHTML(row, ++col, issueDetails(request));
                historyRequestsPanel.setHTML(row, ++col, request.summary().getStringView());
                historyRequestsPanel.setHTML(row, ++col, issueStatus(request));
                historyRequestsPanel.setWidget(row, ++col, issueRating(request));

                historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
            }
        } else {
            historyRequestsPanel.setHTML(++row, 0, NoRecordsFound);
            historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());
            historyRequestsPanel.setHTML(++row, 0, "");
            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
        }
    }

    private String issueStatus(MaintenanceRequestDTO request) {
        StringBuilder result = new StringBuilder();
        result.append(request.status().getStringView());
        switch (request.status().phase().getValue()) {
        case Submitted:
            result.append("<p><i style='font-size:0.8em'>");
            result.append(request.submitted().getStringView());
            result.append("</i>");
            break;
        case Scheduled:
            result.append("<p><i style='font-size:0.8em'>");
            result.append(request.scheduledTimeFrom().getStringView());
            result.append(" - ");
            result.append(request.scheduledTimeTo().getStringView());
            result.append(",&nbsp");
            result.append(request.scheduledDate().getStringView());
            result.append("</i>");
            break;
        default:
            result.append("<p><i style='font-size:0.8em'>");
            result.append(request.submitted().getStringView());
            result.append("</i>");
            break;
        }
        return result.toString();
    }

    private String issueDetails(MaintenanceRequestDTO request) {
        try {
            // return slash-separated name list
            StringBuilder result = new StringBuilder();
            MaintenanceRequestCategory category = request.category();
            while (!category.parent().isNull()) {
                if (!category.name().isNull()) {
                    result.insert(0, result.length() > 0 ? "/" : "").insert(0, category.name().getValue());
                }
                category = category.parent();
            }
            return result.toString();
        } catch (Exception ignore) {
            return "Invalid Entry";
        }
    }

    private Widget issueActions(final MaintenanceRequestDTO request) {
        HorizontalPanel actions = new HorizontalPanel();

        Anchor cancelTicket = new Anchor(i18n.tr("Cancel"));
        cancelTicket.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm("You are about to cancel ticket '" + issueDetails(request) + "'")) {
                    presenter.cancelRequest(request.getPrimaryKey());
                }
            }
        });
        actions.add(cancelTicket);

        actions.add(new HTML("&nbsp"));
        actions.add(new HTML("&nbsp"));
        actions.add(new HTML("&nbsp"));

        Anchor viewlTicket = new Anchor(i18n.tr("View"));
        viewlTicket.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.viewRequest(request);
            }
        });
        actions.add(viewlTicket);

        return actions;
    }

    private Widget issueRating(final MaintenanceRequestDTO request) {
        RateIt rateIt = new RateIt(5);
        Integer rate = request.surveyResponse().rating().getValue();
        if (rate != null) {
            rateIt.setRating(rate);
        }

        rateIt.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                presenter.rateRequest(request.getPrimaryKey(), event.getValue());
            }
        });

        return rateIt;
    }
}
