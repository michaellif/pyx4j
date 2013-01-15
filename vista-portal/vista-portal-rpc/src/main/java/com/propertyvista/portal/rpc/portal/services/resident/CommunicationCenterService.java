/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.services.resident;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.dto.MaintenanceRequestDTO;

//TODO: chnage from MaintenanceRequestDTO
public interface CommunicationCenterService extends AbstractCrudService<MaintenanceRequestDTO> {

    // Can't use List, this should be serializable collection
    void listOpenIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback);

    void listHistoryIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback);

    void cancelTicket(AsyncCallback<Vector<MaintenanceRequestDTO>> callback, MaintenanceRequestDTO request);

    void rateTicket(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO request, Integer rate);

}
