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
package com.propertyvista.crm.rpc.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;

import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;
import com.propertyvista.dto.TenantDTO;

public interface TenantCrudService extends LeaseParticipantCrudServiceBase<TenantDTO> {

    void getAssosiatedTenant(AsyncCallback<Tenant> callback, Key entityId);

    void createPreauthorizedPayment(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId);
}
