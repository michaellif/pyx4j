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
package com.propertyvista.portal.server.portal.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.portal.services.UnitStepService;
import com.propertyvista.portal.rpc.portal.web.dto.application.UnitStepDTO;

public class UnitStepServiceImpl implements UnitStepService {

    @Override
    public void retrieve(AsyncCallback<UnitStepDTO> callback) {
        UnitStepDTO entity = EntityFactory.create(UnitStepDTO.class);
        callback.onSuccess(entity);
    }

    @Override
    public void submit(AsyncCallback<UnitStepDTO> callback, UnitStepDTO entity) {
        callback.onSuccess(entity);
    }

}