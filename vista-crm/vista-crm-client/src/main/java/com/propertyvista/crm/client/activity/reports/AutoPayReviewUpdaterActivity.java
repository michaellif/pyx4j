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
package com.propertyvista.crm.client.activity.reports;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterView;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterViewImpl;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewUpdaterActivity extends AbstractActivity implements AutoPayReviewUpdaterView.Presenter {

    private final AutoPayReviewUpdaterView view;

    private List<PapReviewDTO> leasePapsReview;

    private final AutoPayReviewService autoPayReviewService;

    public AutoPayReviewUpdaterActivity(Place place) {
        view = new AutoPayReviewUpdaterViewImpl();
        autoPayReviewService = GWT.create(AutoPayReviewService.class);
        leasePapsReview = new LinkedList<PapReviewDTO>();
    }

    @Override
    public void populate() {
        autoPayReviewService.getAutoPayReviews(new DefaultAsyncCallback<Vector<PapReviewDTO>>() {
            @Override
            public void onSuccess(Vector<PapReviewDTO> result) {
                leasePapsReview = result;
                populateView();
            }
        }, view.getAutoPayFilterSettings().duplicate(AutoPayChangesReportMetadata.class));
    }

    @Override
    public void refresh() {
        // no need to implement
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
        populate();
    }

    @Override
    public AppPlace getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRangeChanged() {
        populateView();
    }

    private void populateView() {
        int start = view.getVisibleRange().getStart();
        int end = Math.min(leasePapsReview.size(), start + view.getVisibleRange().getLength());

        view.setRowData(view.getVisibleRange().getStart(), leasePapsReview.size(), leasePapsReview.subList(start, end));
    }

}
