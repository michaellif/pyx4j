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
package com.propertyvista.portal.rpc.portal.web.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationDTO;

public interface ResidentSelfRegistrationService extends IService {

    public void obtainBuildings(AsyncCallback<EntitySearchResult<SelfRegistrationBuildingDTO>> callback);

    public void selfRegistration(AsyncCallback<VoidSerializable> callback, SelfRegistrationDTO selfRegistration);
}
