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
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;

import com.propertyvista.portal.rpc.ptapp.dto.UnitInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;

public class ApartmentServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<UnitInfoDTO> callback, Key tenantId) {
        callback.onSuccess(null);
    }

    @Override
    public void save(AsyncCallback<UnitInfoDTO> callback, UnitInfoDTO editableEntity) {
        callback.onSuccess(null);
    }

}
