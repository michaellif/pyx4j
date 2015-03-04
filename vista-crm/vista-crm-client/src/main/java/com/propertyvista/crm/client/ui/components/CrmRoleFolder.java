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
 */
package com.propertyvista.crm.client.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.organization.SelectCrmRoleListService;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleFolder extends VistaTableFolder<CrmRole> {

    private static final I18n i18n = I18n.get(CrmRoleFolder.class);

    private final CrmEntityForm<?> parent;

    public CrmRoleFolder(CrmEntityForm<?> parent) {
        super(CrmRole.class, parent.isEditable());
        setAddable(parent.isEditable());
        this.parent = parent;
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
        columns.add(new FolderColumnDescriptor(proto().description(), "20em"));
        columns.add(new FolderColumnDescriptor(proto().behaviors(), "40em"));
        return columns;
    }

    @Override
    protected CForm<CrmRole> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<CrmRole>(CrmRole.class, columns()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                return inject(column.getObject(), new CLabel<String>());
            }
        };
    }

    @Override
    protected IFolderDecorator<CrmRole> createFolderDecorator() {
        return new VistaTableFolderDecorator<CrmRole>(this, this.isEditable());
    };

    @Override
    protected void addItem() {
        new CrmRoleSelectorDialog().show();
    }

    private class CrmRoleSelectorDialog extends EntitySelectorTableDialog<CrmRole> {

        public CrmRoleSelectorDialog() {
            super(CrmRole.class, true, new HashSet<>(getValue()), i18n.tr("Select roles"));
        }

        @Override
        public boolean onClickOk() {
            for (CrmRole selected : getSelectedItems()) {
                addItem(selected);
            }
            return true;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(),
                    new ColumnDescriptor.Builder(proto().behaviors()).filterAlwaysShown(true).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListCrudService<CrmRole> getSelectService() {
            return GWT.<AbstractListCrudService<CrmRole>> create(SelectCrmRoleListService.class);
        }
    }
}
