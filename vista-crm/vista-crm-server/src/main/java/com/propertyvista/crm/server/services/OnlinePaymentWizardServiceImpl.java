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
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.OnlinePaymentWizardService;
import com.propertyvista.dto.OnlinePaymentSetupDTO;

public class OnlinePaymentWizardServiceImpl implements OnlinePaymentWizardService {

    @Override
    public void create(AsyncCallback<OnlinePaymentSetupDTO> callback) {
        callback.onSuccess(EntityFactory.create(OnlinePaymentSetupDTO.class));
    }

    @Override
    @ServiceExecution(waitCaption = "Submitting...")
    public void finish(AsyncCallback<VoidSerializable> callback, OnlinePaymentSetupDTO editableEntity) {
        callback.onSuccess(null);
    }

}
