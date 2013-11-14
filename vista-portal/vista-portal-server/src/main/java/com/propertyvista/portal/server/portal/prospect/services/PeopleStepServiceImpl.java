/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.prospect.dto.steps.PeopleStepDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.PeopleStepService;

public class PeopleStepServiceImpl implements PeopleStepService {

    @Override
    public void retrieve(AsyncCallback<PeopleStepDTO> callback) {
        PeopleStepDTO entity = EntityFactory.create(PeopleStepDTO.class);
        callback.onSuccess(entity);
    }

    @Override
    public void submit(AsyncCallback<PeopleStepDTO> callback, PeopleStepDTO entity) {
        callback.onSuccess(entity);
    }

}