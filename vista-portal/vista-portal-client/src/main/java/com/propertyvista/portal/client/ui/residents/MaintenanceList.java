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
package com.propertyvista.portal.client.ui.residents;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.portal.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

public class MaintenanceList extends VerticalPanel implements MaintenanceView {

    private static I18n i18n = I18n.get(MaintenanceList.class);

    public static final String NoRecordsFound = i18n.tr("No Records Found");

    private MaintenanceView.Presenter presenter;

    private final FormFlexPanel openRequestsPanel;

    private final FormFlexPanel historyRequestsPanel;

    public MaintenanceList() {
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
    public void populateOpenRequests(Vector<MaintananceDTO> openRequests) {
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

        openRequestsPanel.setH1(++row, 0, 1, i18n.tr("OPEN TICKETS"), newTicket);

        if (openRequests.size() > 0) {
            openRequestsPanel.getFlexCellFormatter().setColSpan(row, 0, 3);

            openRequestsPanel.getColumnFormatter().setWidth(0, "250px");
            openRequestsPanel.getColumnFormatter().setWidth(1, "75px");
            openRequestsPanel.getColumnFormatter().setWidth(2, "75px");

            openRequestsPanel.setHTML(++row, 0, i18n.tr("Open Tickets"));
            openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

            openRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
            openRequestsPanel.setHTML(row, 2, "");

            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintananceDTO request : openRequests) {
                openRequestsPanel.setHTML(++row, 0, request.description().getStringView());
                openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

                openRequestsPanel
                        .setHTML(row, 1, request.status().getStringView() + "<p><i style='font-size:0.8em'>" + request.date().getStringView() + "</i>");

                Anchor cancelTicket = new Anchor(i18n.tr("Cancel"));
                cancelTicket.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.cancelRequest(request);
                    }
                });

                openRequestsPanel.setWidget(row, 2, cancelTicket);

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
    public void populateHistoryRequests(Vector<MaintananceDTO> historyRequests) {
        historyRequestsPanel.removeAllRows();

        int row = -1;

        historyRequestsPanel.setWidth("100%");

        historyRequestsPanel.setH1(++row, 0, 1, i18n.tr("HISTORY"));

        if (historyRequests.size() > 0) {
            historyRequestsPanel.getFlexCellFormatter().setColSpan(row, 0, 3);

            historyRequestsPanel.getColumnFormatter().setWidth(0, "250px");
            historyRequestsPanel.getColumnFormatter().setWidth(1, "75px");
            historyRequestsPanel.getColumnFormatter().setWidth(2, "75px");

            historyRequestsPanel.setHTML(++row, 0, i18n.tr("Ticket"));
            historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

            historyRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
            historyRequestsPanel.setHTML(row, 2, i18n.tr("Rate Service"));
            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

            for (final MaintananceDTO request : historyRequests) {
                historyRequestsPanel.setHTML(++row, 0, request.description().getStringView());
                historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

                historyRequestsPanel.setHTML(row, 1, request.status().getStringView() + "<p><i style='font-size:0.8em'>" + request.date().getStringView()
                        + "</i>");

                RateIt rateIt = new RateIt(5);
                Integer rate = request.satisfactionSurvey().rating().getValue();
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

}
