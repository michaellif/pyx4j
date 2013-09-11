/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.rpc.portal.web.dto.FinancialDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.MainDashboardDTO;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentServicesDashboardDTO;

public interface DashboardService extends IService {

    public void retrieveMainDashboard(AsyncCallback<MainDashboardDTO> callback);

    public void retrieveFinancialDashboard(AsyncCallback<FinancialDashboardDTO> callback);

    public void retrieveServicesDashboard(AsyncCallback<ResidentServicesDashboardDTO> callback);

    // Gadget utils:

    public void deletePreauthorizedPayment(AsyncCallback<VoidSerializable> callback, PreauthorizedPayment itemId);

    public void deletePaymentMethod(AsyncCallback<VoidSerializable> callback, LeasePaymentMethod itemId);
}
