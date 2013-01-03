/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractWizardService;

import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.OnlinePaymentSetupDTO;

public interface OnlinePaymentWizardService extends AbstractWizardService<OnlinePaymentSetupDTO> {

    void obtainPaymentFees(AsyncCallback<AbstractPaymentFees> paymentFees);
}
