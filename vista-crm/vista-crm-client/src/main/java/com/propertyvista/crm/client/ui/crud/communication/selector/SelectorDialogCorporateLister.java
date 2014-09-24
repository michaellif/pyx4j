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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.ListerDataSource;

import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;

public class SelectorDialogCorporateLister extends EntityLister<Employee> {

    public AbstractListCrudService<Employee> selectService;

    private final ListerDataSource<Employee> dataSource;

    private final Collection<Employee> alreadySelected;

    public SelectorDialogCorporateLister(boolean isVersioned) {
        this(isVersioned, null);
    }

    public SelectorDialogCorporateLister(boolean isVersioned, Collection<Employee> alreadySelected) {
        super(Employee.class, isVersioned);
        this.selectService = createSelectService();
        setDataTableModel();
        this.dataSource = new ListerDataSource<Employee>(Employee.class, this.selectService);
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<Employee> emptyList());
        setFilters(createRestrictionFilterForAlreadySelected());
        setDataSource(new ListerDataSource<Employee>(Employee.class, this.selectService));

    }

    protected AbstractListCrudService<Employee> createSelectService() {
        return GWT.<SelectEmployeeListService> create(SelectEmployeeListService.class);
    }

    public AbstractListCrudService<Employee> getSelectService() {
        return this.selectService;
    }

    public void setDataTableModel() {
        DataTableModel<Employee> dataTableModel = new DataTableModel<Employee>(defineColumnDescriptors());
        dataTableModel.setPageSize(PAGESIZE_SMALL);
        dataTableModel.setMultipleSelection(true);
        setDataTableModel(dataTableModel);
    }

    protected ColumnDescriptor[] defineColumnDescriptors() {
        return new ColumnDescriptor[] {//@formatter:off
                new MemberColumnDescriptor.Builder(proto().employeeId()).build(),
                new MemberColumnDescriptor.Builder(proto().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().title()).build(),
                new MemberColumnDescriptor.Builder(proto().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto().email(), false).build(),
                new MemberColumnDescriptor.Builder(proto().updated(), false).build()
        }; //@formatter:on
    }

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        Employee proto = EntityFactory.getEntityPrototype(Employee.class);

        for (Employee entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    @Override
    protected void onObtainSuccess() {
        super.onObtainSuccess();
        setRowsSelected();
    }

    public void setRowsSelected() {

        if (alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<Employee> model = getLister().getDataTablePanel().getDataTable().getDataTableModel();

        for (DataItem<Employee> dataItem : model.getData()) {
            if (alreadySelected.contains(dataItem.getEntity())) {
                model.selectRow(true, model.indexOf(dataItem));
            }
        }
    }
}
