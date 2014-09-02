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
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.EmployeeSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeEnabledCriteria;
import com.propertyvista.domain.person.Name;

public class EmployeeFolder extends VistaTableFolder<Employee> {

    private static final I18n i18n = I18n.get(EmployeeFolder.class);

    private final CrmEntityForm<?> parent;

    private final ParentEmployeeGetter parentEmployeeGetter;

    public final boolean askForActiveOnlyEmployees;

    public EmployeeFolder(CrmEntityForm<?> parent, ParentEmployeeGetter parentEmployeeGetter, boolean askForActiveOnlyEmployees) {
        super(Employee.class, parent.isEditable());
        this.parent = parent;
        this.parentEmployeeGetter = parentEmployeeGetter;
        this.askForActiveOnlyEmployees = askForActiveOnlyEmployees;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().employeeId(), "5em"));
        columns.add(new FolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new FolderColumnDescriptor(proto().title(), "20em"));
        return columns;
    }

    @Override
    protected CForm<Employee> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Employee>(Employee.class, columns()) {

            @SuppressWarnings("rawtypes")
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                CField<?, ?> comp = null;
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
    protected void addItem() {
        new EmployeeSelectorDialogExcludeCurrent(parent.getParentView()).show();
    }

    private class EmployeeSelectorDialogExcludeCurrent extends EmployeeSelectorDialog {

        public EmployeeSelectorDialogExcludeCurrent(IPane parentView) {
            super(parentView, true);

            // add restriction for papa/mama employee, so that he/she won't be able manage himself :)
            // FIXME: somehow we need to forbid circular references. maybe only server side (if someone wants to be a smart ass)
            addFilter(PropertyCriterion.ne(proto().id(), parentEmployeeGetter.getParentId()));

            if (askForActiveOnlyEmployees) {
                addFilter(new EmployeeEnabledCriteria(true));
            }
        }

        @Override
        public void onClickOk() {
            for (Employee selected : getSelectedItems()) {
                addItem(selected);
            }
        }
    }

    public static interface ParentEmployeeGetter {
        Key getParentId();
    }
};
