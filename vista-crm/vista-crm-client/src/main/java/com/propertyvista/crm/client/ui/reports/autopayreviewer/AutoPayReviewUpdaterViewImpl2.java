/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import java.util.List;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewsHolderDTO;

public class AutoPayReviewUpdaterViewImpl2 extends AbstractPrimePane implements AutoPayReviewUpdaterView, IsView {

    private final static I18n i18n = I18n.get(AutoPayReviewUpdaterViewImpl2.class);

    private AutoPayReviewUpdaterView.Presenter presenter;

    private final LeasePapsReviewsHolderForm leasePapsReviewsHolderForm;

    public AutoPayReviewUpdaterViewImpl2() {
        leasePapsReviewsHolderForm = new LeasePapsReviewsHolderForm();
        leasePapsReviewsHolderForm.initContent();
        setContentPane(new ScrollPanel(leasePapsReviewsHolderForm.asWidget()));
        setSize("100%", "100%");
    }

    @Override
    public void setRowData(int start, List<LeasePapsReviewDTO> values) {
        LeasePapsReviewsHolderDTO holder = EntityFactory.create(LeasePapsReviewsHolderDTO.class);
        holder.leasePapsReviews().addAll(values);
        leasePapsReviewsHolderForm.populate(holder);
    }

    @Override
    public Range getVisibleRange() {
        // TODO
        return new Range(0, 3);
    }

    @Override
    public void setPresenter(AutoPayReviewUpdaterView.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.onRangeChanged();
    }

}
