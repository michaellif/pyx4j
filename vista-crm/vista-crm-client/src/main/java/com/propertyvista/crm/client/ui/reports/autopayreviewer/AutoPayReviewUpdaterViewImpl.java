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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.view.client.Range;

import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapDTO;

public class AutoPayReviewUpdaterViewImpl extends AbstractPrimePane implements AutoPayReviewUpdaterView {

    private final AutoPayReviewUpdaterDataGrid dataGrid;

    private com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterView.Presenter presenter;

    public AutoPayReviewUpdaterViewImpl() {
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
    public void setRowData(int start, List<LeasePapsReviewDTO> values) {
        List<PapChargeDTO> charges = new ArrayList<PapChargeDTO>();
        for (LeasePapsReviewDTO v : values) {
            for (PapDTO p : v.paps()) {
                charges.addAll(p.charges());
            }
        }
        dataGrid.populate(charges);
    }

    @Override
    public Range getVisibleRange() {
        return new Range(0, Integer.MAX_VALUE);
    }

}
