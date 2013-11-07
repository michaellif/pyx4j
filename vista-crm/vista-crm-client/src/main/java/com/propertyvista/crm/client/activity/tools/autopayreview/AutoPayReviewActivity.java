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
package com.propertyvista.crm.client.activity.tools.autopayreview;

import java.util.List;

import com.google.gwt.core.shared.GWT;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.tools.common.AbstractBulkOperationToolActivity;
import com.propertyvista.crm.client.ui.tools.autopayreview.AutoPayReviewView;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapChargeDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapsHolderDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewActivity extends AbstractBulkOperationToolActivity<AutoPayChangesReportMetadata, PapReviewDTO, ReviewedPapsHolderDTO> implements
        AutoPayReviewView.Presenter {

    private static final I18n i18n = I18n.get(AutoPayReviewActivity.class);

    public AutoPayReviewActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().instantiate(AutoPayReviewView.class), GWT.<AutoPayReviewService> create(AutoPayReviewService.class),
                AutoPayChangesReportMetadata.class);
    }

    @Override
    protected ReviewedPapsHolderDTO makeProducedItems(List<PapReviewDTO> papReviews) {
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

    @Override
    protected void onSelectedProccessSuccess(DeferredProcessProgressResponse result) {

    }

}
