/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPaySummaryDTO;

public interface AutoPayService extends IService {

    void createAutoPay(AsyncCallback<AutoPayDTO> callback);

    void saveAutoPay(AsyncCallback<Boolean> callback, AutoPayDTO autoPay);

    void deleteAutoPay(AsyncCallback<Boolean> callback, PreauthorizedPayment entityId);

    void retreiveAutoPay(AsyncCallback<AutoPayDTO> callback, PreauthorizedPayment entityId);

    void getAutoPaySummary(AsyncCallback<AutoPaySummaryDTO> callback);
}
