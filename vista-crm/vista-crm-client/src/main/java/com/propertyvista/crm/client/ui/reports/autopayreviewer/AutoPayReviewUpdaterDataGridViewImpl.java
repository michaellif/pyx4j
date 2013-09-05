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

import com.google.gwt.view.client.Range;

import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewDTO;

public class AutoPayReviewUpdaterDataGridViewImpl extends AbstractPrimePane implements AutoPayReviewUpdaterView {

    private final AutoPayReviewUpdaterDataGrid dataGrid;

    private com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterView.Presenter presenter;

    public AutoPayReviewUpdaterDataGridViewImpl() {
        setSize("100%", "100%");
        dataGrid = new AutoPayReviewUpdaterDataGrid();
        setContentPane(dataGrid);
    }

    @Override
    public void setPresenter(com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterView.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.onRangeChanged();
    }

    @Override
    public void setRowData(int start, int total, List<PapReviewDTO> values) {

    }

    @Override
    public Range getVisibleRange() {
        return new Range(0, Integer.MAX_VALUE);
    }

    @Override
    public List<PapReviewDTO> selectedRows() {
        // TODO Auto-generated method stub
        return new LinkedList<PapReviewDTO>();
    }

}
