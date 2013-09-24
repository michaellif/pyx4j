/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewDataGridViewImpl extends AbstractPrimePane implements AutoPayReviewView {

    private final AutoPayReviewDataGrid dataGrid;

    private com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewView.Presenter presenter;

    public AutoPayReviewDataGridViewImpl() {
        setSize("100%", "100%");
        LayoutPanel panel = new LayoutPanel();

        panel.setSize("100%", "100%");

        dataGrid = new AutoPayReviewDataGrid();
        dataGrid.setSize("100%", "100%");
        panel.add(dataGrid);
        panel.setWidgetTopBottom(dataGrid, 30, Unit.PX, 30, Unit.PX);
        panel.setWidgetLeftRight(dataGrid, 30, Unit.PX, 30, Unit.PX);

        Button more = new Button("More", new Command() {
            @Override
            public void execute() {
                presenter.onRangeChanged();
            }
        });
        panel.add(more);
        panel.setWidgetBottomHeight(more, 0, Unit.PX, 20, Unit.PX);
        panel.setWidgetLeftRight(more, 0, Unit.PX, 0, Unit.PX);

        setContentPane(panel);
    }

    @Override
    public void setPresenter(com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewView.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.onRangeChanged();
    }

    @Override
    public void setRowData(int start, int total, List<PapReviewDTO> values) {
        List<PapChargeReviewDTO> charges = new LinkedList<PapChargeReviewDTO>();
        for (PapReviewDTO review : values) {
            charges.addAll(review.charges());
        }
        dataGrid.populate(charges);
    }

    @Override
    public Range getVisibleRange() {
        return new Range(0, Integer.MAX_VALUE);
    }

    @Override
    public List<PapReviewDTO> getMarkedPapReviews() {
        return new LinkedList<PapReviewDTO>();
    }

    @Override
    public AutoPayChangesReportMetadata getAutoPayFilterSettings() {
        AutoPayChangesReportMetadata settings = EntityFactory.create(AutoPayChangesReportMetadata.class);
        settings.minimum().setValue(new LogicalDate());
        return settings;
    }

    @Override
    public void setLoading(boolean isLoading) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEverythingSelected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void resetVisibleRange() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showMessage(String message) {
        // TODO Auto-generated method stub

    }

}
