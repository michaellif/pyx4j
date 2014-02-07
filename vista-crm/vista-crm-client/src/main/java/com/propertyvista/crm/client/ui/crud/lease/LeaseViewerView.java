/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.site.client.ui.prime.lister.ILister;

import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.PaymentRecordDTO;

public interface LeaseViewerView extends LeaseViewerViewBase<LeaseDTO> {

    interface Presenter extends LeaseViewerViewBase.Presenter {

        void sendMail(List<LeaseTermParticipant<?>> users, EmailTemplateType emailType);

        void startBilling();

        void createCompletionEvent(Lease.CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leseEndDate);

        void isCancelCompletionEventAvailable(AsyncCallback<CancelMoveOutConstraintsDTO> callback);

        void cancelCompletionEvent(String decisionReason);

        void moveOut();

        void activate();

        void closeLease(String decisionReason);

        void cancelLease(String decisionReason);

        void createOffer(LeaseTerm.Type type);

        /*
         * Yardi integration stuff:
         */
        void updateFromYardi();

        /*
         * TODO: This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
         */
        void simpleLeaseRenew(LogicalDate leaseEndDate);

        void onInsuredTenantClicked(Tenant tenantId);

        void issueN4(N4BatchRequestDTO n4GenerationQuery);

        void viewApplication();

        void viewDeletedPaps(Tenant tenantId);

        void createMaintenanceRequest();

        void downloadAgreementForSigning();

        void downloadSignedAgreement(LeaseTermAgreementDocument doc);

        void uploadSignedAgreement();

    }

    ILister<DepositLifecycleDTO> getDepositListerView();

    ILister<BillDataDTO> getBillListerView();

    ILister<PaymentRecordDTO> getPaymentListerView();

    ILister<LeaseAdjustment> getLeaseAdjustmentListerView();

    ILister<MaintenanceRequestDTO> getMaintenanceListerView();

    void reportSendMailActionResult(String message);

    void reportCancelNoticeFailed(UserRuntimeException caught);

    void requestNewLegalStatus(AsyncCallback<LegalStatus> legalStatusUpadate);
}
