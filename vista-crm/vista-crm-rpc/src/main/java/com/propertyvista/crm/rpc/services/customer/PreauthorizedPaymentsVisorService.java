/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-06
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.customer;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public interface PreauthorizedPaymentsVisorService extends IService {

    void retrieve(AsyncCallback<PreauthorizedPaymentsDTO> callback, Tenant tenantId);

    void save(AsyncCallback<VoidSerializable> callback, PreauthorizedPaymentsDTO pads);

    void create(AsyncCallback<PreauthorizedPaymentDTO> callback, Tenant tenantId);

    void delete(AsyncCallback<VoidSerializable> callback, AutopayAgreement pad);

    void recollect(AsyncCallback<Vector<AutopayAgreement>> callback, Tenant tenantId);
}
