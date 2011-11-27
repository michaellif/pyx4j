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
package com.propertyvista.portal.rpc.portal.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

public interface TenantMaintenanceService extends IService {

    // Can't use List, this should be serializable collection
    public void listOpenIssues(AsyncCallback<Vector<MaintananceDTO>> callback);

    public void listHistoryIssues(AsyncCallback<Vector<MaintananceDTO>> callback);

}
