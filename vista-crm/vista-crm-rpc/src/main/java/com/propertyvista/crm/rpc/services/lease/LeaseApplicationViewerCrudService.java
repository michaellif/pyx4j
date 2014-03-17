/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.lease;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.common.LeaseViewerCrudServiceBase;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;

public interface LeaseApplicationViewerCrudService extends LeaseViewerCrudServiceBase<LeaseApplicationDTO> {

    void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId);

    /**
     * callback returns a message that should be passed to the users.
     */
    void inviteUsers(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users);

    void creditCheck(AsyncCallback<String> callback, Key entityId, BigDecimal creditCheckAmount, Vector<LeaseTermParticipant<?>> users);

    void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO);

    void getCreditCheckServiceStatus(AsyncCallback<PmcEquifaxStatus> callback);

    void isCreditCheckViewAllowed(AsyncCallback<VoidSerializable> callback);

    void saveApplicationDocument(AsyncCallback<VoidSerializable> callback, LeaseApplicationDocument value);

}
