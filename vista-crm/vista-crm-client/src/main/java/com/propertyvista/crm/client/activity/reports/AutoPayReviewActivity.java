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
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewView;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewViewImpl;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapChargeDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapsHolderDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewActivity extends AbstractActivity implements AutoPayReviewView.Presenter {

    private static final I18n i18n = I18n.get(AutoPayReviewActivity.class);

    private final AutoPayReviewView view;

    private List<PapReviewDTO> papReviews;

    private final AutoPayReviewService autoPayReviewService;

    private final AppPlace place;

    public AutoPayReviewActivity(AppPlace place) {
        this.place = place;
        this.view = new AutoPayReviewViewImpl();
        this.autoPayReviewService = GWT.create(AutoPayReviewService.class);
        this.papReviews = new LinkedList<PapReviewDTO>();
    }

    @Override
    public void populate() {
        view.setLoading(true);
        autoPayReviewService.getAutoPayReviews(new DefaultAsyncCallback<Vector<PapReviewDTO>>() {
            @Override
            public void onSuccess(Vector<PapReviewDTO> papReviews) {
                AutoPayReviewActivity.this.papReviews = papReviews;
                AutoPayReviewActivity.this.view.resetVisibleRange();
                AutoPayReviewActivity.this.populateView();
            }
        }, view.getAutoPayFilterSettings().duplicate(AutoPayChangesReportMetadata.class));
    }

    @Override
    public void acceptMarked() {
        if (!papReviews.isEmpty() && (view.isEverythingSelected() || !view.getMarkedPapReviews().isEmpty())) {
            autoPayReviewService.accept(new DefaultAsyncCallback<String>() {
                @Override
                public void onSuccess(String deferredCorrelationId) {
                    startAccetanceProgress(deferredCorrelationId);
                }
            }, makeReviewedPaps(view.isEverythingSelected() ? papReviews : view.getMarkedPapReviews()));
        } else {
            view.showMessage(i18n.tr("Please select some AutoPays first"));
        }
    }

    private void startAccetanceProgress(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog(i18n.tr("Accept Selected"), i18n.tr("Accepting Auto Pay changes ..."), false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
                populate();
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }

    @Override
    public void refresh() {
        // no need to implement
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void onRangeChanged() {
        view.setLoading(true);
        populateView();
    }

    private void populateView() {
        int start = view.getVisibleRange().getStart();
        int end = Math.min(papReviews.size(), start + view.getVisibleRange().getLength());

        view.setRowData(view.getVisibleRange().getStart(), papReviews.size(), papReviews.subList(start, end));
        view.setLoading(false);
    }

    private ReviewedPapsHolderDTO makeReviewedPaps(List<PapReviewDTO> papReviews) {
        ReviewedPapsHolderDTO reviewedPapsHolder = EntityFactory.create(ReviewedPapsHolderDTO.class);
        for (PapReviewDTO papReview : papReviews) {
            ReviewedAutopayAgreementDTO reviewedPap = EntityFactory.create(ReviewedAutopayAgreementDTO.class);
            reviewedPap.papId().set(papReview.papId());

            for (PapChargeReviewDTO papChargeReview : papReview.charges()) {
                if (papChargeReview.changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed) {
                    ReviewedPapChargeDTO reviewedPapCharge = EntityFactory.create(ReviewedPapChargeDTO.class);
                    reviewedPapCharge.billableItem().set(papChargeReview.billableItem());
                    reviewedPapCharge.paymentAmountUpdate().setValue(papChargeReview.newPapAmount().getValue());
                    reviewedPap.reviewedCharges().add(reviewedPapCharge);
                }
            }
            reviewedPapsHolder.acceptedReviewedPaps().add(reviewedPap);
        }

        return reviewedPapsHolder;
    }

}
