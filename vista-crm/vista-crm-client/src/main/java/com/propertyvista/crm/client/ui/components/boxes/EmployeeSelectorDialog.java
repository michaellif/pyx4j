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
 * @version $Id$
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
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;

public abstract class EmployeeSelectorDialog extends EntitySelectorTableVisorController<Employee> {
    private static final I18n i18n = I18n.get(EmployeeSelectorDialog.class);

    public EmployeeSelectorDialog(IPane parentView) {
        this(parentView, null);
    }

    public EmployeeSelectorDialog(IPane parentView, boolean isMultiselect) {
        this(parentView, isMultiselect, Collections.<Employee> emptySet());
    }

    public EmployeeSelectorDialog(IPane parentView, Set<Employee> alreadySelected) {
        this(parentView, alreadySelected != null, alreadySelected);
    }

    public EmployeeSelectorDialog(IPane parentView, boolean isMultiselect, Set<Employee> alreadySelected) {
        this(parentView, isMultiselect, alreadySelected, i18n.tr("Select Employee"));
    }

    public EmployeeSelectorDialog(IPane parentView, boolean isMultiselect, Set<Employee> alreadySelected, String caption) {
        super(parentView, Employee.class, isMultiselect, alreadySelected, caption);
    }

    @Override
    protected List<ColumnDescriptor> defineColumnDescriptors() {
        return Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().employeeId()).build(),
                new MemberColumnDescriptor.Builder(proto().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().title()).build(),
                new MemberColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().email(), false).build(),
                new MemberColumnDescriptor.Builder(proto().updated(), false).build()
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
