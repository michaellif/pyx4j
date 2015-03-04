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
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.Collection;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;

public class SelectorDialogCorporateLister extends EntityLister<Employee> {

    public AbstractListCrudService<Employee> selectService;

    public SelectorDialogCorporateLister(SelectRecipientsDialogForm parent, Collection<Employee> alreadySelected) {
        super(Employee.class, GWT.<SelectEmployeeListService> create(SelectEmployeeListService.class), parent, alreadySelected);

        setColumnDescriptors( //
                new ColumnDescriptor.Builder(proto().employeeId()).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().name()).searchable(false).build(),//
                new ColumnDescriptor.Builder(proto().title()).filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().filterAlwaysShown(true).build(),//
                new ColumnDescriptor.Builder(proto().email()).visible(false).build(),//
                new ColumnDescriptor.Builder(proto().updated()).visible(false).build() //
        );

        DataTableModel<Employee> dataTableModel = new DataTableModel<Employee>();
        dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    @Override
    protected EntityListCriteria<Employee> updateCriteria(EntityListCriteria<Employee> criteria) {
        EntityListCriteria<Employee> result = super.updateCriteria(criteria);
        criteria.ne(criteria.proto().user().email(), CrmUser.VISTA_SUPPORT_ACCOUNT_EMAIL);
        return result;
    }
}
