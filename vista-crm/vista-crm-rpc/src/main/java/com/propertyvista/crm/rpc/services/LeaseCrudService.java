/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.LeaseDTO;

public interface LeaseCrudService extends AbstractCrudService<LeaseDTO> {

    void syncBuildingServiceCatalog(AsyncCallback<Building> callback, Building building);

    void removeTeant(AsyncCallback<Boolean> callback, TenantInLease tenant);

    void createMasterApplication(AsyncCallback<VoidSerializable> callback, Key entityId);
}
