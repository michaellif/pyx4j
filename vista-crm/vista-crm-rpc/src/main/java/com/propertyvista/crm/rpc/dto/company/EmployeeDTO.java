/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.company;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitiveSet;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification.NotificationType;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;

@Transient
@ExtendsBO(Employee.class)
@SecurityEnabled
public interface EmployeeDTO extends Employee {

    EmployeePrivilegesDTO privileges();

    // TODO put auditing configuration here
    UserAuditingConfigurationDTO userAuditingConfiguration();

    @Caption(name = "Notifications")
    IPrimitiveSet<NotificationType> notificationTypes();

}
