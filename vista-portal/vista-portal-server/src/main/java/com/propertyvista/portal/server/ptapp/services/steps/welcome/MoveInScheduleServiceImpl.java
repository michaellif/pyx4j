/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services.steps.welcome;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.MoveInScheduleDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.welcome.MoveInScheduleService;

public class MoveInScheduleServiceImpl implements MoveInScheduleService {

    @Override
    public void retrieve(AsyncCallback<MoveInScheduleDTO> callback, Key tenantId) {
        callback.onSuccess(EntityFactory.create(MoveInScheduleDTO.class));
    }

    @Override
    public void save(AsyncCallback<MoveInScheduleDTO> callback, MoveInScheduleDTO editableEntity) {
        callback.onSuccess(editableEntity);
    }

}
