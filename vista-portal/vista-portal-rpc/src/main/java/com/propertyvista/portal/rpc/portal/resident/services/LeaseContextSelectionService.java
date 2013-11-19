/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.portal.resident.dto.LeaseContextChoiceDTO;

public interface LeaseContextSelectionService extends IService {

    void getLeaseContextChoices(AsyncCallback<Vector<LeaseContextChoiceDTO>> callback);

    void setLeaseContext(AsyncCallback<AuthenticationResponse> callback, Lease leaseStub);

}
