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
package com.propertyvista.crm.rpc.services.maintenance;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.annotations.AccessControl;

import com.propertyvista.crm.rpc.services.maintenance.ac.Resolve;
import com.propertyvista.crm.rpc.services.maintenance.ac.Schedule;
import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestScheduleDTO;

public interface MaintenanceCrudService extends AbstractCrudService<MaintenanceRequestDTO> {

    @Transient
    interface MaintenanceInitializationData extends InitializationData {

        Building building();

        AptUnit unit();

        Tenant tenant();
    }

    @AccessControl(Schedule.class)
    void sheduleAction(AsyncCallback<VoidSerializable> callback, MaintenanceRequestScheduleDTO schedule, Key entityId);

    void updateProgressAction(AsyncCallback<VoidSerializable> callback, String progressNote, Key scheduleId);

    @AccessControl(Resolve.class)
    void resolveAction(AsyncCallback<VoidSerializable> callback, LogicalDate resolvedOn, String resolution, Key entityId);

    void rateAction(AsyncCallback<VoidSerializable> callback, SurveyResponse rate, Key entityId);

    @AccessControl(Resolve.class)
    void cancelAction(AsyncCallback<VoidSerializable> callback, Key entityId);

    void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean labelsOnly, Key buildingId);

    void getPreferredWindowOptions(AsyncCallback<Vector<TimeWindow>> callback, Key buildingId);
}
