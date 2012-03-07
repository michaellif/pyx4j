/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.onboarding.RequestIO;
import com.propertyvista.onboarding.RequestMessageIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.onboarding.ResponseMessageIO;

class OnboardingProcessor {

    private final static Logger log = LoggerFactory.getLogger(OnboardingProcessor.class);

    OnboardingProcessor() {

    }

    public boolean isValid(RequestMessageIO message) {
        for (RequestIO request : message.requests()) {
            if (request.pmcId().isNull()) {
                return false;
            }
        }
        return true;
    }

    private ResponseIO execute(RequestIO request) {
        ResponseIO response = EntityFactory.create(ResponseIO.class);
        if (!request.requestId().isNull()) {
            response.requestId().set(request.requestId());
        }
        response.success().setValue(Boolean.FALSE);
        response.errorMessage().setValue("Not implemented");
        return response;
    }

    public ResponseMessageIO execute(RequestMessageIO message) {
        ResponseMessageIO rm = EntityFactory.create(ResponseMessageIO.class);
        if (!message.messageId().isNull()) {
            rm.messageId().set(message.messageId());
        }
        for (RequestIO request : message.requests()) {
            try {
                rm.responses().add(execute(request));
            } catch (Throwable e) {
                log.error("Error", e);
                ResponseIO response = EntityFactory.create(ResponseIO.class);
                response.success().setValue(Boolean.FALSE);
                if (ApplicationMode.isDevelopment()) {
                    response.errorMessage().setValue(e.getMessage());
                }
                rm.responses().add(response);
            }
        }
        rm.status().setValue(ResponseMessageIO.StatusCode.OK);
        return rm;
    }

}
