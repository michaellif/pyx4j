/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.web.services.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;

public interface MaintenanceRequestCrudService extends AbstractCrudService<MaintenanceRequestDTO> {

    void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId);

    void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId, Integer rate);

    void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly);

    void retreiveMaintenanceSummary(AsyncCallback<MaintenanceSummaryDTO> callback);

}
