/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.communicationcenter;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.themes.TenantDashboardTheme;

public class CommunicationCenterViewList extends VerticalPanel implements CommunicationCenterView {

    private static final I18n i18n = I18n.get(CommunicationCenterViewList.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private CommunicationCenterView.Presenter presenter;

    private final FormFlexPanel openRequestsPanel;

    private final FormFlexPanel historyRequestsPanel;

    public CommunicationCenterViewList() {
        setWidth("100%");

        openRequestsPanel = new FormFlexPanel();
        openRequestsPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        add(openRequestsPanel);

        historyRequestsPanel = new FormFlexPanel();
        historyRequestsPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        add(historyRequestsPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populateOpenRequests(Vector<MaintenanceRequestDTO> openRequests) {
        openRequestsPanel.removeAllRows();

        openRequestsPanel.setWidth("100%");

        int row = -1;

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        newTicket.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.createNewRequest();
            }
        });

        openRequestsPanel.setH1(++row, 0, 4, i18n.tr("OPEN TICKETS"), newTicket);

        if (openRequests.size() > 0) {
            openRequestsPanel.getColumnFormatter().setWidth(0, "240px");
            openRequestsPanel.getColumnFormatter().setWidth(1, "75px");
            openRequestsPanel.getColumnFormatter().setWidth(2, "45px");
            openRequestsPanel.getColumnFormatter().setWidth(3, "40px");

            openRequestsPanel.setHTML(++row, 0, i18n.tr("Ticket"));
            openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            openRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
            openRequestsPanel.setHTML(row, 2, "");
            openRequestsPanel.setHTML(row, 3, "");

            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintenanceRequestDTO request : openRequests) {
                openRequestsPanel.setHTML(++row, 0, issueDetails(request, true));
                openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

                switch (request.status().getValue()) {
                case Submitted:
                    openRequestsPanel.setHTML(row, 1, request.status().getStringView() + "<p><i style='font-size:0.8em'>" + request.submitted().getStringView()
                            + "</i>");
                    break;
                case Scheduled:
                    openRequestsPanel.setHTML(row, 1, request.status().getStringView() + "<p><i style='font-size:0.8em'>"
                            + request.scheduledTime().getStringView() + ",&nbsp" + request.scheduledDate().getStringView() + "</i>");
                    break;
                }

                Anchor cancelTicket = new Anchor(i18n.tr("Cancel"));
                cancelTicket.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        if (Window.confirm("You are about to cancel ticket '" + issueDetails(request, false) + "'")) {
                            presenter.cancelRequest(request);
                        }
                    }
                });
                openRequestsPanel.setWidget(row, 2, cancelTicket);

                Anchor viewlTicket = new Anchor(i18n.tr("View"));
                viewlTicket.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.editRequest(request);
                    }
                });
                openRequestsPanel.setWidget(row, 3, viewlTicket);

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

    @Override
    public void populateHistoryRequests(Vector<MaintenanceRequestDTO> historyRequests) {
        historyRequestsPanel.removeAllRows();

        int row = -1;

        historyRequestsPanel.setWidth("100%");

        historyRequestsPanel.setH1(++row, 0, 3, i18n.tr("HISTORY"));

        if (historyRequests.size() > 0) {
            historyRequestsPanel.getColumnFormatter().setWidth(0, "240px");
            historyRequestsPanel.getColumnFormatter().setWidth(1, "75px");
            historyRequestsPanel.getColumnFormatter().setWidth(2, "85px");

            historyRequestsPanel.setHTML(++row, 0, i18n.tr("Ticket"));
            historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            historyRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
            historyRequestsPanel.setHTML(row, 2, i18n.tr("Rate Service"));
            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintenanceRequestDTO request : historyRequests) {
                historyRequestsPanel.setHTML(++row, 0, issueDetails(request, true));
                historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

                historyRequestsPanel.setHTML(row, 1, request.status().getStringView() + "<p><i style='font-size:0.8em'>" + request.submitted().getStringView()
                        + "</i>");

                RateIt rateIt = new RateIt(5);
                Integer rate = request.surveyResponse().rating().getValue();
                if (rate != null) {
                    rateIt.setRating(rate);
                }
                rateIt.addValueChangeHandler(new ValueChangeHandler<Integer>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Integer> event) {
                        presenter.rateRequest(request, event.getValue());
                    }

                });
                historyRequestsPanel.setWidget(row, 2, rateIt);

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

    private String issueDetails(MaintenanceRequestDTO request, boolean withDescription) {
        String details = "Invalid Entry";
        IssueClassification ic;
        if (request == null || (ic = request.issueClassification()) == null) {
            return details;
        } else if (!ic.issue().isNull()) {
            details = ic.issue().getValue();
        } else if (!ic.subjectDetails().name().isNull()) {
            details = ic.subjectDetails().name().getValue();
        } else if (!ic.subjectDetails().subject().name().isNull()) {
            details = ic.subjectDetails().subject().name().getValue();
        } else if (!ic.subjectDetails().subject().issueElement().name().isNull()) {
            details = ic.subjectDetails().subject().issueElement().name().getValue();
        } else {
            return details;
        }

        if (withDescription && !request.description().isNull() && request.description().getStringView().length() > 0) {
            details += " - " + request.description().getStringView();
        }
        return details;
    }

}
