/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2014
 * @author smolka
 */
package com.propertyvista.crm.client.ui.components.boxes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;

public abstract class EmployeeSelectionDialog extends EntitySelectorTableDialog<Employee> {
    private static final I18n i18n = I18n.get(EmployeeSelectionDialog.class);

    public EmployeeSelectionDialog() {
        this(Collections.<Employee> emptySet());
    }

    public EmployeeSelectionDialog(Set<Employee> alreadySelected) {
        this(false, alreadySelected);
    }

    public EmployeeSelectionDialog(boolean isMultiselect, Set<Employee> alreadySelected) {
        this(isMultiselect, alreadySelected, i18n.tr("Select Employee"));
    }

    public EmployeeSelectionDialog(boolean isMultiselect, Set<Employee> alreadySelected, String caption) {
        super(Employee.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new ColumnDescriptor.Builder(proto().employeeId()).build(),
                new ColumnDescriptor.Builder(proto().name()).searchable(false).build(),
                new ColumnDescriptor.Builder(proto().title()).build(),
                new ColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build(),
                new ColumnDescriptor.Builder(proto().email(), false).build(),
                new ColumnDescriptor.Builder(proto().updated(), false).build()
        ); //@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().employeeId(), false));
    }

    @Override
    protected AbstractListCrudService<Employee> getSelectService() {
        return GWT.<AbstractListCrudService<Employee>> create(SelectEmployeeListService.class);
    }
}
