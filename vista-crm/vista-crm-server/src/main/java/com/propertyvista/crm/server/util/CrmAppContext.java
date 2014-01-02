/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;

public class CrmAppContext extends VistaContext {

    public static CrmUser getCurrentUser() {
        return Context.getUserVisit(CrmUserVisit.class).getCurrentUser();
    }

    public static Employee getCurrentUserEmployee() {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), CrmAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }

}
