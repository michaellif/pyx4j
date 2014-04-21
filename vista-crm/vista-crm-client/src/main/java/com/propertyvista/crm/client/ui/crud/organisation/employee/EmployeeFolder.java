/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.SelectEmployeeListService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.person.Name;

public class EmployeeFolder extends VistaTableFolder<Employee> {

    private static final I18n i18n = I18n.get(EmployeeFolder.class);

    private final CrmEntityForm<?> parent;

    private final ParentEmployeeGetter parentEmployeeGetter;

    public EmployeeFolder(CrmEntityForm<?> parent, ParentEmployeeGetter parentEmployeeGetter) {
        super(Employee.class, parent.isEditable());
        this.parent = parent;
        this.parentEmployeeGetter = parentEmployeeGetter;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().employeeId(), "5em"));
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().title(), "20em"));
        return columns;
    }

    @Override
    protected CEntityForm<Employee> createItemForm(IObject<?> member) {
        return new CEntityFolderRowEditor<Employee>(Employee.class, columns()) {

            @SuppressWarnings("rawtypes")
            @Override
            protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                CComponent<?, ?> comp = null;
                if (proto().title() == column.getObject()) {
                    comp = inject(column.getObject(), new CLabel<String>());
                } else if (proto().name() == column.getObject()) {
                    comp = inject(column.getObject(), new CEntityLabel<Name>());
                    ((CField) comp).setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(new CrmSiteMap.Organization.Employee().formViewerPlace(getValue().id().getValue()));
                        }
                    });
                } else {
                    comp = inject(column.getObject(), new CLabel());
                }

                return comp;
            }
        };
    }

    @Override
    protected IFolderDecorator<Employee> createFolderDecorator() {
        return new VistaTableFolderDecorator<Employee>(this, this.isEditable()) {
            {
                setShowHeader(false);
            }
        };
    }

    @Override
    protected void addItem() {
        new EmployeeSelectorDialog(parent.getParentView()).show();
    }

    private class EmployeeSelectorDialog extends EntitySelectorTableVisorController<Employee> {

        public EmployeeSelectorDialog(IPane parentView) {
            super(parentView, Employee.class, true, getValue(), i18n.tr("Select Employee"));

            // add restriction for papa/mama employee, so that he/she won't be able manage himself :)
            // FIXME: somehow we need to forbid circular references. maybe only server side (if someone wants to be a smart ass)
            addFilter(PropertyCriterion.ne(proto().id(), parentEmployeeGetter.getParentId()));
        }

        @Override
        public void onClickOk() {
            for (Employee selected : getSelectedItems()) {
                addItem(selected);
            }
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
        protected AbstractListService<Employee> getSelectService() {
            return GWT.<AbstractListService<Employee>> create(SelectEmployeeListService.class);
        }
    }

    public static interface ParentEmployeeGetter {
        Key getParentId();
    }
};
