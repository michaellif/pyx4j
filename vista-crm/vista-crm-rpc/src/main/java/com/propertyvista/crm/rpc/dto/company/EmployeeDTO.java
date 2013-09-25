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
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;
import com.propertyvista.domain.security.UserCredentialEditDTO;

@Transient
@ExtendsBO(Employee.class)
public interface EmployeeDTO extends Employee, UserCredentialEditDTO {

    @Override
    @Caption(name = "Active Employee")
    IPrimitive<Boolean> enabled();

    IPrimitive<Boolean> restrictAccessToSelectedBuildingsAndPortfolios();

    IList<CrmRole> roles();

    // TODO put auditing configuration here
    UserAuditingConfigurationDTO userAuditingConfiguration();

    @Caption(name = "Has security question")
    IPrimitive<Boolean> isSecurityQuestionSet();

}
