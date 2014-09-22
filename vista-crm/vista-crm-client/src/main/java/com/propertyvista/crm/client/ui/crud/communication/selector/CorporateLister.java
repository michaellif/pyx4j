/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;

public class CorporateLister extends EntityLister<Employee> {

    public AbstractListCrudService<Employee> selectService;

    public CorporateLister(boolean isVersioned) {
        super(Employee.class, isVersioned);
        this.selectService = createSelectService();
        setDataSource(new ListerDataSource<Employee>(Employee.class, this.selectService));

    }

    protected AbstractListCrudService<Employee> createSelectService() {
        return GWT.<SelectEmployeeListService> create(SelectEmployeeListService.class);
    }

    public AbstractListCrudService<Employee> getSelectService() {
        return this.selectService;
    }
}
