/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.domain.tenant.ProspectSignUp;
import com.propertyvista.portal.rpc.portal.prospect.dto.ProspectSignUpDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ProspectSignUpService;

public class ProspectSignUpServiceImpl implements ProspectSignUpService {

    @Override
    public void signUp(AsyncCallback<VoidSerializable> callback, ProspectSignUpDTO dto) {
        ProspectSignUp request = EntityFactory.create(ProspectSignUp.class);

        request.firstName().setValue(dto.firstName().getValue());
        request.middleName().setValue(dto.middleName().getValue());
        request.lastName().setValue(dto.lastName().getValue());
        request.email().setValue(dto.email().getValue());
        request.password().setValue(dto.password().getValue());

        ServerSideFactory.create(CustomerFacade.class).prospectSignUp(request);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
