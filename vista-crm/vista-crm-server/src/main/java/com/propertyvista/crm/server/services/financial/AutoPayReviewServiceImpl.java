/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentsReportCriteria;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO.ChangeType;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewCaptionDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedPapsHolderDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayReviewService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayReviewServiceImpl implements AutoPayReviewService {

    @Override
    public void getAutoPayReviews(AsyncCallback<Vector<PapReviewDTO>> callback, AutoPayChangesReportMetadata filterSettings) {
        Vector<AutoPayReviewLeaseDTO> suspendedPreauthorizedPayments = new Vector<AutoPayReviewLeaseDTO>(ServerSideFactory.create(PaymentReportFacade.class)
                .reportPreauthorizedPaymentsRequiredReview(makeCriteria(filterSettings)));
        Vector<PapReviewDTO> papsForReview = convert2PapReviews(suspendedPreauthorizedPayments);
        callback.onSuccess(papsForReview);
    }

    @Override
    public void accept(AsyncCallback<String> callback, final ReviewedPapsHolderDTO acceptedReviews) {
        callback.onSuccess(DeferredProcessRegistry.fork(new AutoPayReviewDeferredProcess(acceptedReviews), ThreadPoolNames.IMPORTS));
    }

    private PreauthorizedPaymentsReportCriteria makeCriteria(AutoPayChangesReportMetadata filterSettings) {
        // query buildings to enforce portfolio:
        List<Building> selectedBuildings = null;

        if (!filterSettings.buildings().isEmpty()) {
            Vector<Key> buildingKeys = new Vector<Key>(filterSettings.buildings().size());
            for (Building b : filterSettings.buildings()) {
                buildingKeys.add(b.getPrimaryKey());
            }
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.in(criteria.proto().id(), buildingKeys);
            selectedBuildings = Persistence.secureQuery(criteria, AttachLevel.IdOnly);
        } else {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().suspended(), false);
            selectedBuildings = Persistence.secureQuery(criteria);
        }

        PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(null, selectedBuildings);
        if (filterSettings.filterByExpectedMoveOut().isBooleanTrue()) {
            reportCriteria.setExpectedMoveOutCriteris(filterSettings.minimum().getValue(), filterSettings.maximum().getValue());
        }
        reportCriteria.setLeasesOnNoticeOnly(filterSettings.leasesOnNoticeOnly().isBooleanTrue());
        return reportCriteria;
    }

    private Vector<PapReviewDTO> convert2PapReviews(Vector<AutoPayReviewLeaseDTO> suspendedPreauthorizedPayments) {
        Vector<PapReviewDTO> papReviews = new Vector<PapReviewDTO>();
        for (AutoPayReviewLeaseDTO leaseAutoPays : suspendedPreauthorizedPayments) {
            PapReviewCaptionDTO papReviewCaption = makeCaption(leaseAutoPays);
            for (AutoPayReviewPreauthorizedPaymentDTO autoPay : leaseAutoPays.pap()) {
                papReviews.add(makePapReview(papReviewCaption, autoPay));
            }
        }
        return papReviews;
    }

    private PapReviewDTO makePapReview(PapReviewCaptionDTO papReviewCaption, AutoPayReviewPreauthorizedPaymentDTO autoPay) {
        PapReviewDTO papReview = EntityFactory.create(PapReviewDTO.class);
        papReview.papId().set(autoPay.pap());
        papReview.caption().set(papReviewCaption.duplicate(PapReviewCaptionDTO.class));
        papReview.caption().paymentMethod().setValue(autoPay.paymentMethodView().getStringView());
        papReview.caption().tenant().setValue(autoPay.tenantName().getStringView());
        papReview.caption().tenant_().set(autoPay.tenant_());

        for (AutoPayReviewChargeDTO autoPayCharge : autoPay.items()) {
            PapChargeReviewDTO papCharge = EntityFactory.create(PapChargeReviewDTO.class);
            papCharge.chargeName().setValue(autoPayCharge.leaseCharge().getValue());
            papCharge.changeType().setValue(guessChangeType(autoPayCharge));

            papCharge.suspendedPrice().setValue(autoPayCharge.previous().totalPrice().getValue());
            papCharge.suspendedPapAmount().setValue(autoPayCharge.previous().payment().getValue());
            papCharge.suspendedPapPercent().setValue(autoPayCharge.previous().percent().getValue());

            papCharge.billableItem().set(autoPayCharge.current().billableItem());
            papCharge.newPrice().setValue(autoPayCharge.current().totalPrice().getValue());
            papCharge.newPapAmount().setValue(autoPayCharge.current().payment().getValue());
            papCharge.newPapPercent().setValue(autoPayCharge.current().percent().getValue());

            papCharge.changePercent().setValue(autoPayCharge.current().percentChange().getValue());

            papReview.charges().add(papCharge);
        }
        return papReview;
    }

    private PapChargeReviewDTO.ChangeType guessChangeType(AutoPayReviewChargeDTO charge) {
        if (charge.previous().isEmpty()) {
            return ChangeType.New;
        }
        if (charge.current().isEmpty()) {
            return ChangeType.Removed;
        }
        if (charge.current().totalPrice().getValue().compareTo(charge.previous().totalPrice().getValue()) == 0) {
            return ChangeType.Unchanged;
        }
        return ChangeType.Changed;
    }

    private PapReviewCaptionDTO makeCaption(AutoPayReviewLeaseDTO leaseAutoPays) {
        PapReviewCaptionDTO caption = EntityFactory.create(PapReviewCaptionDTO.class);
        caption.building().setValue(leaseAutoPays.building().getValue());
        caption.unit().setValue(leaseAutoPays.unit().getValue());
        caption.lease().setValue(leaseAutoPays.leaseId().getValue());
        caption.lease_().set(leaseAutoPays.lease());
        caption.hasLeaseWithOtherPaps().setValue(leaseAutoPays.pap().size() > 1);
        caption.expectedMoveOut().setValue(leaseAutoPays.lease().expectedMoveOut().getValue());
        caption.paymentDue().setValue(leaseAutoPays.paymentDue().getValue());
        return caption;
    }

    private Vector<PapReviewDTO> makeMockData() {
        int maxLeaseNum = 300;
        Vector<PapReviewDTO> papReviews = new Vector<PapReviewDTO>(maxLeaseNum);
        int tenantNum = 0;
        int chargeKeyCounter = 0;

        for (int leaseNum = 0; leaseNum < maxLeaseNum; leaseNum++) {
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
                if (chargeNum == 0) {
                    papCharge._isPivot().setValue(true);
                    papCharge._parentPap().set(papReviewCaption);
                }
                papCharge.setPrimaryKey(new Key(++chargeKeyCounter));
                papCharge.chargeName().setValue("Charge#" + chargeNum);
                papCharge.changeType().setValue(
                        chargeNum % 3 == 0 ? PapChargeReviewDTO.ChangeType.Changed : chargeNum % 2 == 0 ? PapChargeReviewDTO.ChangeType.Removed
                                : PapChargeReviewDTO.ChangeType.New);

                switch (papCharge.changeType().getValue()) {
                case Changed:
                    papCharge.suspendedPrice().setValue(new BigDecimal(1200 * (chargeNum + 1)));
                    papCharge.suspendedPapPercent().setValue(tenantNum % 3 == 0 ? new BigDecimal("0.6") : new BigDecimal("1"));
                    papCharge.suspendedPapAmount().setValue(papCharge.suspendedPrice().getValue().multiply((papCharge.suspendedPapPercent().getValue())));
                    papCharge.newPrice().setValue(papCharge.suspendedPrice().getValue().add(new BigDecimal(50)));
                    papCharge.newPapAmount().setValue(new BigDecimal("0.00"));
                    papCharge.newPapPercent().setValue(new BigDecimal("0.00"));
                    break;
                case New:
                    papCharge.newPrice().setValue(new BigDecimal(500 * (chargeNum + 1)));
                    papCharge.newPapAmount().setValue(new BigDecimal("0.00"));
                    papCharge.newPapPercent().setValue(new BigDecimal("0.00"));
                    break;
                case Removed:
                    papCharge.suspendedPrice().setValue(new BigDecimal(199 * (chargeNum + 1)));
                    papCharge.suspendedPapPercent().setValue(tenantNum % 3 == 0 ? new BigDecimal("0.6") : new BigDecimal("1"));
                    papCharge.suspendedPapAmount().setValue(papCharge.suspendedPrice().getValue().multiply(papCharge.suspendedPapPercent().getValue()));
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

                pap.caption().set(thisPapCaption);
                ++tenantNum;

                for (PapChargeReviewDTO charge : charges) {
                    PapChargeReviewDTO papCharge = charge.duplicate();

                    if (charge.changeType().equals(PapChargeReviewDTO.ChangeType.Changed)) {
                        papCharge.suspendedPapPercent().setValue(charge.suspendedPapPercent().getValue().divide(new BigDecimal(papsPerLeaseCount)));
                        papCharge.suspendedPapAmount().setValue(papCharge.suspendedPrice().getValue().multiply(papCharge.suspendedPapPercent().getValue()));

                    }
                    pap.charges().add(papCharge);
                }
                papReviews.add(pap);
            }
        }
        return papReviews;
    }

}
