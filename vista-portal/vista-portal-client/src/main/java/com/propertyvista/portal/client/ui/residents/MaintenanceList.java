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
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RateIt;

import com.propertyvista.portal.client.themes.TenantDashboardTheme;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

public class MaintenanceList extends VerticalPanel implements MaintenanceView {

    private static I18n i18n = I18n.get(MaintenanceList.class);

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
        openRequestsPanel.clear();

        openRequestsPanel.setWidth("100%");
        openRequestsPanel.getColumnFormatter().setWidth(0, "250px");
        openRequestsPanel.getColumnFormatter().setWidth(1, "75px");
        openRequestsPanel.getColumnFormatter().setWidth(2, "75px");

        int row = -1;

        Anchor newTicket = new Anchor(i18n.tr("New Ticket"));
        newTicket.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.createNewRequest();
            }
        });

        openRequestsPanel.setH1(++row, 0, 1, i18n.tr("OPEN TICKETS"), newTicket);
        openRequestsPanel.getFlexCellFormatter().setColSpan(row, 0, 3);

        openRequestsPanel.setHTML(++row, 0, i18n.tr("Open Tickets"));
        openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

        openRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
        openRequestsPanel.setHTML(row, 2, "");

        openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

        for (MaintananceDTO requests : openRequests) {
            openRequestsPanel.setHTML(++row, 0, requests.description().getStringView());
            openRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

            openRequestsPanel.setHTML(row, 1, requests.status().getStringView() + "<p><i style='font-size:0.8em'>" + requests.date().getStringView() + "</i>");

            Anchor cancelTicket = new Anchor(i18n.tr("Cancel"));
            openRequestsPanel.setWidget(row, 2, cancelTicket);

            openRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
        }

    }

    @Override
    public void populateHistoryRequests(Vector<MaintananceDTO> historyRequests) {
        historyRequestsPanel.clear();

        int row = -1;

        historyRequestsPanel.setWidth("100%");
        historyRequestsPanel.getColumnFormatter().setWidth(0, "250px");
        historyRequestsPanel.getColumnFormatter().setWidth(1, "75px");
        historyRequestsPanel.getColumnFormatter().setWidth(2, "75px");

        historyRequestsPanel.setH1(++row, 0, 1, i18n.tr("HISTORY"));
        historyRequestsPanel.getFlexCellFormatter().setColSpan(row, 0, 3);

        historyRequestsPanel.setHTML(++row, 0, i18n.tr("Open Tickets"));
        historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

        historyRequestsPanel.setHTML(row, 1, i18n.tr("Status"));
        historyRequestsPanel.setHTML(row, 2, i18n.tr("Rate Service"));
        historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableHeader.name());

        for (MaintananceDTO requests : historyRequests) {
            historyRequestsPanel.setHTML(++row, 0, requests.description().getStringView());
            historyRequestsPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);

            historyRequestsPanel.setHTML(row, 1, requests.status().getStringView() + "<p><i style='font-size:0.8em'>" + requests.date().getStringView()
                    + "</i>");

            RateIt rateIt = new RateIt(5);
            rateIt.setRating(requests.satisfactionSurvey().rating().getValue());
            historyRequestsPanel.setWidget(row, 2, rateIt);

            historyRequestsPanel.getRowFormatter().getElement(row).addClassName(TenantDashboardTheme.StyleName.TenantDashboardTableRow.name());
        }

    }

}
