/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 */
package com.propertyvista.crm.rpc.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public interface EmailToTenantsService extends IService {

    public void sendEmail(AsyncCallback<String> callback, EmailTemplateType emailType,
            @SuppressWarnings("rawtypes") EntityQueryCriteria<LeaseTermParticipant> criteria);

}
