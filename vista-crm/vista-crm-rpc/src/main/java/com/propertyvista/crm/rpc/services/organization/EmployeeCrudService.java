/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.organization;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

/**
 * Service used by managers to create new users/employees.
 */
public interface EmployeeCrudService extends AbstractCrudService<EmployeeDTO> {

    void clearSecurityQuestion(AsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId);

    void sendPasswordResetEmail(AsyncCallback<VoidSerializable> asyncCallback, EmployeeDTO employeeId);
}
