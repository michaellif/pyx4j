/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 */
package com.propertyvista.crm.rpc.services.building;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;

public interface FloorplanCrudService extends AbstractCrudService<FloorplanDTO> {
    void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback, Key buildingId);
}
