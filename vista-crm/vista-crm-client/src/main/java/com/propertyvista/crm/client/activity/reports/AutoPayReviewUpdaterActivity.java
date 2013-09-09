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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterView;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.AutoPayReviewUpdaterViewImpl;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewCaptionDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewDTO;

public class AutoPayReviewUpdaterActivity extends AbstractActivity implements AutoPayReviewUpdaterView.Presenter {

    private final AutoPayReviewUpdaterView view;

    List<PapReviewDTO> leasePapsReview;

    public AutoPayReviewUpdaterActivity(Place place) {
        view = new AutoPayReviewUpdaterViewImpl();
        leasePapsReview = makeMockData();
    }

    @Override
    public void acceptSelected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
    }

    @Override
    public AppPlace getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onRangeChanged() {
        int start = view.getVisibleRange().getStart();
        int end = Math.min(leasePapsReview.size(), start + view.getVisibleRange().getLength());

        view.setRowData(view.getVisibleRange().getStart(), leasePapsReview.size(), leasePapsReview.subList(start, end));
    }

    private List<PapReviewDTO> makeMockData() {
        List<PapReviewDTO> papReviews = new LinkedList<PapReviewDTO>();
        int tenantNum = 0;
        int chargeKeyCounter = 0;

        for (int leaseNum = 0; leaseNum < 30; leaseNum++) {
            PapReviewCaptionDTO papReviewCaption = EntityFactory.create(PapReviewCaptionDTO.class);
            papReviewCaption.building().setValue("bath9999");
            papReviewCaption.building_().setPrimaryKey(new Key(1));

            papReviewCaption.unit().setValue("#" + leaseNum);
            papReviewCaption.unit_().setPrimaryKey(new Key(1));

            papReviewCaption.lease().setValue("t000" + leaseNum);
            papReviewCaption.lease_().setPrimaryKey(new Key(leaseNum + 1));

            papReviewCaption.expectedMoveOut().setValue(new LogicalDate());

            // create charges for lease:
            List<PapChargeReviewDTO> charges = new ArrayList<PapChargeReviewDTO>();
            for (int chargeNum = 0; chargeNum < 3; ++chargeNum) {
                PapChargeReviewDTO papCharge = EntityFactory.create(PapChargeReviewDTO.class);
                papCharge.setPrimaryKey(new Key(++chargeKeyCounter));
                papCharge.chargeName().setValue("Charge#" + chargeNum);
                papCharge.changeType().setValue(
                        chargeNum % 3 == 0 ? PapChargeReviewDTO.ChangeType.Changed : chargeNum % 2 == 0 ? PapChargeReviewDTO.ChangeType.Removed
                                : PapChargeReviewDTO.ChangeType.New);

                switch (papCharge.changeType().getValue()) {
                case Changed:
                    papCharge.suspendedPrice().setValue(new BigDecimal(1200 * (chargeNum + 1)));
                    papCharge.suspendedPreAuthorizedPaymentPercent().setValue(tenantNum % 3 == 0 ? new BigDecimal("0.6") : new BigDecimal("1"));
                    papCharge.suspendedPreAuthorizedPaymentAmount().setValue(
                            papCharge.suspendedPrice().getValue().multiply((papCharge.suspendedPreAuthorizedPaymentPercent().getValue())));
                    papCharge.newPrice().setValue(papCharge.suspendedPrice().getValue().add(new BigDecimal(50)));
                    papCharge.newPreAuthorizedPaymentAmount().setValue(new BigDecimal("0.00"));
                    papCharge.newPreAuthorizedPaymentPercent().setValue(new BigDecimal("0.00"));
                    papCharge.suggestedNewPreAuthorizedPaymentAmount().setValue(
                            papCharge.newPrice().getValue().multiply(papCharge.suspendedPreAuthorizedPaymentPercent().getValue()));
                    break;
                case New:
                    papCharge.newPrice().setValue(new BigDecimal(500 * (chargeNum + 1)));
                    papCharge.newPreAuthorizedPaymentAmount().setValue(new BigDecimal("0.00"));
                    papCharge.newPreAuthorizedPaymentPercent().setValue(new BigDecimal("0.00"));
                    papCharge.suggestedNewPreAuthorizedPaymentAmount().setValue(papCharge.newPrice().getValue());
                    break;
                case Removed:
                    papCharge.suspendedPrice().setValue(new BigDecimal(199 * (chargeNum + 1)));
                    papCharge.suspendedPreAuthorizedPaymentPercent().setValue(tenantNum % 3 == 0 ? new BigDecimal("0.6") : new BigDecimal("1"));
                    papCharge.suspendedPreAuthorizedPaymentAmount().setValue(
                            papCharge.suspendedPrice().getValue().multiply(papCharge.suspendedPreAuthorizedPaymentPercent().getValue()));
                    break;
                default:
                    break;
                }
                charges.add(papCharge);
            }

            int papsPerLeaseCount = 2;
            for (int papNum = 0; papNum < papsPerLeaseCount; ++papNum) {
                PapReviewDTO pap = EntityFactory.create(PapReviewDTO.class);
                PapReviewCaptionDTO thisPapCaption = papReviewCaption.duplicate();
                thisPapCaption.tenant().setValue("Tenant Tenantovic" + tenantNum);
                thisPapCaption.tenant_().setPrimaryKey(new Key(tenantNum + 1));
                thisPapCaption.paymentMethod().setValue("Payment Method" + tenantNum);
                thisPapCaption.paymentMethod_().setPrimaryKey(new Key(tenantNum + 1));

                pap.caption().set(thisPapCaption);
                ++tenantNum;

                for (PapChargeReviewDTO charge : charges) {
                    PapChargeReviewDTO papCharge = charge.duplicate();

                    if (charge.changeType().equals(PapChargeReviewDTO.ChangeType.Changed)) {
                        papCharge.suspendedPreAuthorizedPaymentPercent().setValue(
                                charge.suspendedPreAuthorizedPaymentPercent().getValue().divide(new BigDecimal(papsPerLeaseCount)));
                        papCharge.suspendedPreAuthorizedPaymentAmount().setValue(
                                papCharge.suspendedPrice().getValue().multiply(papCharge.suspendedPreAuthorizedPaymentPercent().getValue()));

                        papCharge.suggestedNewPreAuthorizedPaymentAmount().setValue(
                                papCharge.newPrice().getValue().multiply(papCharge.suspendedPreAuthorizedPaymentPercent().getValue()));
                    }
                    pap.charges().add(papCharge);
                }
                papReviews.add(pap);
            }
        }
        return papReviews;
    }

}
