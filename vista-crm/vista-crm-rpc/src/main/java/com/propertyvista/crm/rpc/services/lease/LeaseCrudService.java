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

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseCrudService extends LeaseCrudServiceBase<LeaseDTO> {

    // TODO Move to new Service  LeaseOperationService

    void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId);

    void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut);

    void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId);

    void activate(AsyncCallback<VoidSerializable> callback, Key entityId);

    void sendMail(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<ApplicationUserDTO> users, EmailTemplateType emailType);
}
