/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.building;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.annotations.AccessControl;

import com.propertyvista.crm.rpc.services.lease.ac.UpdateFromYardi;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.BuildingDTO;

public interface BuildingCrudService extends AbstractCrudService<BuildingDTO> {

    void retrieveMerchantAccountStatus(AsyncCallback<MerchantAccount> callback, MerchantAccount merchantAccountId);

    void setMerchantAccount(AsyncCallback<VoidSerializable> callback, Building buildingStub, MerchantAccount merchantAccountId);

    void retrieveEmployee(AsyncCallback<Employee> callback, Employee employeeId);

    @AccessControl(UpdateFromYardi.class)
    void updateFromYardi(AsyncCallback<String> callback, Building buildingId);

    void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback);

}
