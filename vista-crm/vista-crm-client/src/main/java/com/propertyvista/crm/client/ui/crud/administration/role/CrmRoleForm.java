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
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.activity.crud.administration.role.CrmRoleBehaviorDTOListServiceImpl;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class CrmRoleForm extends CrmEntityForm<CrmRole> {

    private static final I18n i18n = I18n.get(CrmRoleForm.class);

    private boolean prevTwoStepVerificationValue;

    public CrmRoleForm(IForm<CrmRole> view) {
        super(CrmRole.class, view);

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();
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
    }

    private void enforceRequireTwoStepVerificationForEquifaxBehaviour() {
        boolean hasEquifax = false;

        for (VistaCrmBehaviorDTO behaviour : getValue().permissions()) {
            if (behaviour.behavior().getValue() == VistaCrmBehavior.Equifax) {
                hasEquifax = true;
                break;
            }
        }
        if (hasEquifax) {
            prevTwoStepVerificationValue = getValue().requireTwoStepVerificationOnLogin().getValue(false);
            get(proto().requireTwoStepVerificationOnLogin()).setValue(true);
            get(proto().requireTwoStepVerificationOnLogin()).setEditable(false);
        } else {
            get(proto().requireTwoStepVerificationOnLogin()).setValue(prevTwoStepVerificationValue);
            get(proto().requireTwoStepVerificationOnLogin()).setEditable(true);
        }
    }

    private static class CrmRolePermissionsFolder extends VistaTableFolder<VistaCrmBehaviorDTO> {

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
            new CrmRoleBehaviorDTOListServiceImpl().list(new DefaultAsyncCallback<EntitySearchResult<VistaCrmBehaviorDTO>>() {
                @Override
                public void onSuccess(EntitySearchResult<VistaCrmBehaviorDTO> result) {
                    result.getData().removeAll(getValue());
                    new EntitySelectorListDialog<VistaCrmBehaviorDTO>(i18n.tr("Select Permissions"), true, result.getData()) {
                        @Override
                        public boolean onClickOk() {
                            for (VistaCrmBehaviorDTO item : getSelectedItems()) {
                                addItem(item);
                            }
                            return true;
                        }
                    }.show();
                }
            }, EntityListCriteria.create(VistaCrmBehaviorDTO.class));
        }
    }
}
