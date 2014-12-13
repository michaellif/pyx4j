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
package com.propertyvista.crm.client.ui.crud.administration.role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.admin.CrmRoleBehaviorDTOListService;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class CrmRoleForm extends CrmEntityForm<CrmRole> {

    private static final I18n i18n = I18n.get(CrmRoleForm.class);

    public CrmRoleForm(IPrimeFormView<CrmRole, ?> view) {
        super(CrmRole.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();
        formPanel.append(Location.Left, proto().requireSecurityQuestionForPasswordReset()).decorate().componentWidth(50);
        formPanel.append(Location.Left, proto().requireTwoStepVerificationOnLogin()).decorate().componentWidth(50);

        formPanel.h1(proto().permissions().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().permissions(), new CrmRolePermissionsFolder());
        get(proto().permissions()).addValueChangeHandler(new ValueChangeHandler<List<VistaCrmBehaviorDTO>>() {
            @Override
            public void onValueChange(ValueChangeEvent<List<VistaCrmBehaviorDTO>> event) {
                enforceRequireTwoStepVerificationForEquifaxBehaviour();
            }
        });

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("Crm Role")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        enforceRequireTwoStepVerificationForEquifaxBehaviour();
        get(proto().permissions()).setEditable(!getValue().systemPredefined().getValue(false));
    }

    private void enforceRequireTwoStepVerificationForEquifaxBehaviour() {
        boolean hasEquifax = false;

        for (VistaCrmBehaviorDTO behaviour : getValue().permissions()) {
            if (behaviour.behavior().getValue() == VistaCrmBehavior.CreditCheckFull) {
                hasEquifax = true;
                break;
            }
        }

        if (hasEquifax) {
            get(proto().requireTwoStepVerificationOnLogin()).setValue(true);
            get(proto().requireTwoStepVerificationOnLogin()).setEditable(false);
        } else {
            get(proto().requireTwoStepVerificationOnLogin()).setEditable(true);
        }
    }

    private class CrmRolePermissionsFolder extends VistaTableFolder<VistaCrmBehaviorDTO> {

        public CrmRolePermissionsFolder() {
            super(VistaCrmBehaviorDTO.class);
            setOrderable(false);
        }

        @Override
        protected CFolderItem<VistaCrmBehaviorDTO> createItem(boolean first) {
            CFolderItem<VistaCrmBehaviorDTO> folderItem = super.createItem(first);
            folderItem.setViewable(true);
            return folderItem;
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(new FolderColumnDescriptor(proto().behavior(), "20em"));
        }

        @Override
        protected IFolderDecorator<VistaCrmBehaviorDTO> createFolderDecorator() {
            TableFolderDecorator<VistaCrmBehaviorDTO> folderDecorator = (TableFolderDecorator<VistaCrmBehaviorDTO>) super.createFolderDecorator();
            folderDecorator.setShowHeader(false);
            return folderDecorator;
        }

        @Override
        protected void addItem() {
            new CrmPermissionSelectorDialog().show();
        }

        public class CrmPermissionSelectorDialog extends EntitySelectorTableDialog<VistaCrmBehaviorDTO> {

            public CrmPermissionSelectorDialog() {
                super(VistaCrmBehaviorDTO.class, false, true, new HashSet<>(getValue()), i18n.tr("Select Permissions"));
            }

            @Override
            protected AbstractListCrudService<VistaCrmBehaviorDTO> getSelectService() {
                return GWT.<AbstractListCrudService<VistaCrmBehaviorDTO>> create(CrmRoleBehaviorDTOListService.class);
            }

            @Override
            protected List<ColumnDescriptor> defineColumnDescriptors() {
                return Arrays.asList(//
                        new ColumnDescriptor.Builder(proto().permission(), true).build(),//
                        new ColumnDescriptor.Builder(proto().description(), true).build());
            }

            @Override
            public boolean onClickOk() {
                for (VistaCrmBehaviorDTO item : getSelectedItems()) {
                    addItem(item);
                }
                return true;
            }

        }

    }
}
