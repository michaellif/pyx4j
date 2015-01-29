/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.crm.rpc.services.lease;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.annotations.AccessControl;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseAgreementSigning;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseRenew;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseReserveUnit;
import com.propertyvista.crm.rpc.services.lease.ac.LeaseStateManagement;
import com.propertyvista.crm.rpc.services.lease.ac.SendMail;
import com.propertyvista.crm.rpc.services.lease.ac.UpdateFromYardi;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseViewerCrudService extends LeaseViewerCrudServiceBase<LeaseDTO> {

    // TODO Move to new Service  LeaseOperationService

//TODO need accumulative (OR) permissions
//  @AccessControl(LeaseNotice.class)
//  @AccessControl(LeaseCompletion.class)
    void createCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, Lease.CompletionType completionType, LogicalDate eventDate,
            LogicalDate moveOutDate, LogicalDate leseEndDate);

    void isCancelCompletionEventAvailable(AsyncCallback<CancelMoveOutConstraintsDTO> callback, Key entityId);

//TODO need accumulative (OR) permissions
//    @AccessControl(LeaseNotice.class)
//    @AccessControl(LeaseCompletion.class)
    void cancelCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    @AccessControl(LeaseStateManagement.class)
    void moveOut(AsyncCallback<VoidSerializable> callback, Key entityId);

    /**
     * <code>callback</code> returns a message that should be display to the users (i.e. e-mails were send successfully);
     */
    @AccessControl(SendMail.class)
    void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users, EmailTemplateType emailType);

    @AccessControl(LeaseStateManagement.class)
    void activate(AsyncCallback<VoidSerializable> callback, Key entityId);

    @AccessControl(LeaseStateManagement.class)
    void closeLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    @AccessControl(LeaseStateManagement.class)
    void cancelLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    /**
     * Yardi Integration stuff
     */
    @AccessControl(UpdateFromYardi.class)
    void updateFromYardiDeferred(AsyncCallback<String> callback, Key entityId);

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    @AccessControl(LeaseRenew.class)
    void simpleLeaseRenew(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate leaseEndDate);

    @Override
    @AccessControl(LeaseReserveUnit.class)
    void reserveUnit(AsyncCallback<VoidSerializable> callback, Key entityId, int durationHours);

    @Override
    @AccessControl(LeaseReserveUnit.class)
    void releaseUnit(AsyncCallback<VoidSerializable> callback, Key entityId);

    void signLease(AsyncCallback<String> callback, Lease leaseId);

    // Agreement Documents
    @AccessControl(LeaseAgreementSigning.class)
    void getLeaseAgreementDocuments(AsyncCallback<LeaseAgreementDocumentsDTO> callback, Lease leaseId);

    @AccessControl(LeaseAgreementSigning.class)
    void updateLeaseAgreementDocuments(AsyncCallback<VoidSerializable> callback, Lease leaseId, LeaseAgreementDocumentsDTO documents);

}
