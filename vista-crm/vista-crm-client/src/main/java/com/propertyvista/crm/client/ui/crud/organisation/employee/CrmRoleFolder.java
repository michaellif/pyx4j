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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;

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
        this.parent = parent;
        setOrderable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().name(), "20em"),
                new EntityFolderColumnDescriptor(proto().behaviors(), "40em")
        );//@formatter:on
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof CrmRole) {
            return (T) new CEntityFolderRowEditor<CrmRole>(CrmRole.class, columns()) {
                @SuppressWarnings("rawtypes")
                @Override
                protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                    return inject(column.getObject(), new CLabel<String>());
                }
            };
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<CrmRole> createFolderDecorator() {
        return new VistaTableFolderDecorator<CrmRole>(this, this.isEditable());
    };

    @Override
    protected void addItem() {
        new CrmRoleSelectorDialog().show();
    }

    private class CrmRoleSelectorDialog extends EntitySelectorTableVisorController<CrmRole> {

        public CrmRoleSelectorDialog() {
            super(parent.getParentView(), CrmRole.class, true, getValue(), i18n.tr("Select roles"));
        }

        @Override
        public void onClickOk() {
            for (CrmRole selected : getSelectedItems()) {
                addItem(selected);
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().behaviors()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListService<CrmRole> getSelectService() {
            return GWT.<AbstractListService<CrmRole>> create(SelectCrmRoleListService.class);
        }
    }
}
