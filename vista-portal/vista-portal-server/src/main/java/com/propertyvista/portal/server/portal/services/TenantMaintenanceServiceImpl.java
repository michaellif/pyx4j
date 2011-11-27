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
package com.propertyvista.portal.server.portal.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class TenantMaintenanceServiceImpl implements TenantMaintenanceService {

    @Override
    public void listOpenIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        dto.addAll(TenantMaintenanceDAO.getOpenIssues());
        callback.onSuccess(dto);
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        dto.addAll(TenantMaintenanceDAO.getHistoryIssues());
        callback.onSuccess(dto);
    }

}
