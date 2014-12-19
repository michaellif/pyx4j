/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeEnabledCriteria;

public class SelectEmployeeListServiceImpl extends AbstractListServiceImpl<Employee> implements SelectEmployeeListService {

    public SelectEmployeeListServiceImpl() {
        super(Employee.class);
    }

    @Override
    public Criterion convertCriterion(EntityListCriteria<Employee> criteria, Criterion cr) {
        if (cr instanceof EmployeeEnabledCriteria) {
            EmployeeEnabledCriteria enable = (EmployeeEnabledCriteria) cr;
            return PropertyCriterion.eq(criteria.proto().user().credential().enabled(), enable.getEnabled());
        } else {
            return super.convertCriterion(criteria, cr);
        }
    }

    @Override
    protected void enhanceListRetrieved(Employee bo, Employee dto) {
        super.enhanceListRetrieved(bo, dto);
        Persistence.service().retrieve(dto.signature(), AttachLevel.IdOnly, false);
    }

}
