/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.services.resident;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceService extends AbstractCrudService<MaintenanceRequestDTO> {

    // Can't use List, this should be serializable collection
    void listOpenIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback);

    void listClosedIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback);

    void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId);

    void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId, Integer rate);

    void createNewRequest(AsyncCallback<MaintenanceRequestDTO> callback);

    void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly);

}
