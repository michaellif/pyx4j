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

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.onboarding.GetReCaptchaPublicKeyRequestIO;
import com.propertyvista.onboarding.GetReCaptchaPublicKeyResponseIO;
import com.propertyvista.onboarding.ResponseIO;

public class GetReCaptchaPublicKeyRequestHandler extends AbstractRequestHandler<GetReCaptchaPublicKeyRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(GetReCaptchaPublicKeyRequestHandler.class);

    public GetReCaptchaPublicKeyRequestHandler() {
        super(GetReCaptchaPublicKeyRequestIO.class);
    }

    @Override
    public ResponseIO execute(GetReCaptchaPublicKeyRequestIO request) {

        GetReCaptchaPublicKeyResponseIO response = EntityFactory.create(GetReCaptchaPublicKeyResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.reCaptchaPublicKey().setValue(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());

        return response;
    }
}
