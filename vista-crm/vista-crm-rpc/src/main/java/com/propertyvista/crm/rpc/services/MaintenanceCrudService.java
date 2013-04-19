/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryMeta;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceCrudService extends AbstractCrudService<MaintenanceRequestDTO> {

    void sheduleAction(AsyncCallback<VoidSerializable> callback, ScheduleDataDTO data, Key entityId);

    void resolveAction(AsyncCallback<VoidSerializable> callback, Key entityId);

    void rateAction(AsyncCallback<VoidSerializable> callback, SurveyResponse rate, Key entityId);

    void cancelAction(AsyncCallback<VoidSerializable> callback, Key entityId);

    void createNewRequest(AsyncCallback<MaintenanceRequestDTO> callback, Key tenantId);

    void getCategoryMeta(AsyncCallback<MaintenanceRequestCategoryMeta> callback, boolean labelsOnly);
}
