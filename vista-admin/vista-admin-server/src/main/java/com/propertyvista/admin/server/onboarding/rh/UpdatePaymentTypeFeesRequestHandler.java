/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.UpdatePaymentTypeFeesRequestIO;

public class UpdatePaymentTypeFeesRequestHandler extends AbstractRequestHandler<UpdatePaymentTypeFeesRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(UpdatePaymentTypeFeesRequestHandler.class);

    public UpdatePaymentTypeFeesRequestHandler() {
        super(UpdatePaymentTypeFeesRequestIO.class);
    }

    @Override
    public ResponseIO execute(UpdatePaymentTypeFeesRequestIO request) {

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        return response;
    }
}
