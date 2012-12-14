/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 14, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.CreditCheckWizardService;
import com.propertyvista.dto.CreditCheckWizardDTO;

public class CreditCheckWizardServiceImpl implements CreditCheckWizardService {

    @Override
    public void create(AsyncCallback<CreditCheckWizardDTO> callback) {
        CreditCheckWizardDTO creditCheck = EntityFactory.create(CreditCheckWizardDTO.class);
        callback.onSuccess(creditCheck);

    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void finish(AsyncCallback<VoidSerializable> callback, CreditCheckWizardDTO editableEntity) {
        System.out.println("++++++++save");
    }

}
