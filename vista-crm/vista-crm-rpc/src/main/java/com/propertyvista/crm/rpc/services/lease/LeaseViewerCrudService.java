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
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.lease;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseViewerCrudService extends LeaseViewerCrudServiceBase<LeaseDTO> {

    // TODO Move to new Service  LeaseOperationService

    void createCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, Lease.CompletionType completionType, LogicalDate eventDate,
            LogicalDate moveOutDate, LogicalDate leseEndDate);

    void isCancelCompletionEventAvailable(AsyncCallback<CancelMoveOutConstraintsDTO> callback, Key entityId);

    void cancelCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    void moveOut(AsyncCallback<VoidSerializable> callback, Key entityId);

    /**
     * <code>callback</code> returns a message that should be display to the users (i.e. e-mails were send successfully);
     */
    void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users, EmailTemplateType emailType);

    void activate(AsyncCallback<VoidSerializable> callback, Key entityId);

    void closeLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    void cancelLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    /**
     * Yardi Integration stuff
     */
    void updateFromYardi(AsyncCallback<VoidSerializable> callback, Key entityId);

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    void simpleLeaseRenew(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate leaseEndDate);

    void issueN4(AsyncCallback<VoidSerializable> callback, N4BatchRequestDTO n4GenerationQuery);

    void setLegalStatus(AsyncCallback<VoidSerializable> callback, Lease leaseId, LegalStatus status);

    void signLease(AsyncCallback<VoidSerializable> callback, Lease leaseId);

    void getLeaseAgreementDocuments(AsyncCallback<LeaseAgreementDocumentsDTO> callback, Lease leaseId);

    void updateLeaseAgreementDocuments(AsyncCallback<VoidSerializable> callback, Lease leaseId, LeaseAgreementDocumentsDTO documents);

}
