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

import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseTermDTO;

public interface LeaseViewerCrudService extends LeaseViewerCrudServiceBase<LeaseDTO> {

    // TODO Move to new Service  LeaseOperationService

    void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    /**
     * <code>callback</code> returns a message that should be display to the users (i.e. e-mails were send successfully);
     */
    void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseParticipant> users, EmailTemplateType emailType);

    void activate(AsyncCallback<VoidSerializable> callback, Key entityId);

    void cancelLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason);

    void createOffer(AsyncCallback<LeaseTermDTO> callback, Key entityId, LeaseTerm.Type type);
}
