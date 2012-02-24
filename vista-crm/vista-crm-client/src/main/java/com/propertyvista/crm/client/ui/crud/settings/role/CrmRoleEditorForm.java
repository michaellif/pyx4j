/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.role;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.activity.crud.settings.role.CrmRoleBehaviorDTOListServiceImpl;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class CrmRoleEditorForm extends CrmEntityForm<CrmRole> {

    private static final I18n i18n = I18n.get(CrmRoleEditorForm.class);

    public CrmRoleEditorForm() {
        this(false);
    }

    public CrmRoleEditorForm(boolean viewMode) {
        super(CrmRole.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).labelWidth(10).componentWidth(20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description())).labelWidth(10).componentWidth(20).build());
        content.setH1(++row, 0, 1, proto().permissions().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().permissions(), new CrmRolePermissionsFolder()));

        return content;
    }

    private static class CrmRolePermissionsFolder extends VistaTableFolder<VistaCrmBehaviorDTO> {

        public CrmRolePermissionsFolder() {
            super(VistaCrmBehaviorDTO.class);
            setOrderable(false);
            setViewable(true);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(new EntityFolderColumnDescriptor(proto().behavior(), "20em"));
        }

        @Override
        protected IFolderDecorator<VistaCrmBehaviorDTO> createDecorator() {
            return new VistaTableFolderDecorator<VistaCrmBehaviorDTO>(this, this.isEditable()) {
                {
                    setShowHeader(false);
                }
            };
        }

        @Override
        protected void addItem() {
            new CrmBehaviorDTOSelectorDialog(getValue()) {

                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().isEmpty()) {
                        return false;
                    } else {
                        for (VistaCrmBehaviorDTO selected : getSelectedItems()) {
                            addItem(selected);
                        }
                        return true;
                    }

                }
            }.show();
        }
    }

    private abstract static class CrmBehaviorDTOSelectorDialog extends EntitySelectorDialog<VistaCrmBehaviorDTO> {

        public CrmBehaviorDTOSelectorDialog(List<VistaCrmBehaviorDTO> alreadySelected) {
            super(VistaCrmBehaviorDTO.class, true, alreadySelected, i18n.tr("Select Permissions"));
            setWidth("500px");
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(new MemberColumnDescriptor.Builder(proto().permission()).build());
        }

        @Override
        protected AbstractListService<VistaCrmBehaviorDTO> getSelectService() {
            return new CrmRoleBehaviorDTOListServiceImpl();
        }
    }

}
