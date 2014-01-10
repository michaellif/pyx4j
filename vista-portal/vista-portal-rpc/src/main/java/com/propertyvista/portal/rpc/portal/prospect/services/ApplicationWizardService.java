/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BathroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BedroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;

public interface ApplicationWizardService extends IService {

    public void init(AsyncCallback<OnlineApplicationDTO> callback);

    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity);

    public void submit(AsyncCallback<Key> callback, OnlineApplicationDTO editableEntity);

    void getAvailableUnits(AsyncCallback<Vector<UnitTO>> callback, BedroomNumber beds, BathroomNumber baths, LogicalDate moveIn);

    void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, UnitTO unitId);
}
