/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.SiteDataTablePanel;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.rpc.services.BroadcastTemplateCrudService;
import com.propertyvista.domain.communication.BroadcastTemplate;
import com.propertyvista.domain.communication.BroadcastTemplate.AudienceType;
import com.propertyvista.domain.communication.DeliveryHandle.MessageType;

public class BroadcastTemplateLister extends SiteDataTablePanel<BroadcastTemplate> {
    private static final I18n i18n = I18n.get(BroadcastTemplateLister.class);

    private Button newButton;

    public BroadcastTemplateLister() {
        super(BroadcastTemplate.class, GWT.<AbstractCrudService<BroadcastTemplate>> create(BroadcastTemplateCrudService.class));
        addUpperActionItem(newButton = new Button(FolderImages.INSTANCE.addIcon(), i18n.tr("New Broadcast Template"), new Command() {
            @Override
            public void execute() {
            }
        }));

        Button.ButtonMenuBar subMenu = new Button.ButtonMenuBar();
        subMenu.addItem(new MenuItem(i18n.tr("Tenant"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Tenant);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Prospect"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Prospect);
            }
        }));
        subMenu.addItem(new MenuItem(i18n.tr("Employee"), new Command() {
            @Override
            public void execute() {
                editNewEntity(AudienceType.Employee);
            }
        }));

        newButton.setMenu(subMenu);
        newButton.setPermission(DataModelPermission.permissionCreate(BroadcastTemplate.class));

        setColumnDescriptors(new ColumnDescriptor[] { //
        new ColumnDescriptor.Builder(proto().name()).build(), //
                new ColumnDescriptor.Builder(proto().subject()).build(), //
                new ColumnDescriptor.Builder(proto().category()).build(), //
                new ColumnDescriptor.Builder(proto().highImportance()).build() //
        });

        setDataTableModel(new DataTableModel<BroadcastTemplate>());
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().name(), false));
    }

    private void editNewEntity(Object placeCriteria) {
        BroadcastTemplateCrudService.BroadcastTemplateInitializationData initData = EntityFactory
                .create(BroadcastTemplateCrudService.BroadcastTemplateInitializationData.class);
        if (placeCriteria != null) {
            initData.audienceType().setValue((AudienceType) placeCriteria);
            if (((AudienceType) placeCriteria).equals(AudienceType.Employee)) {
                initData.messageType().setValue(MessageType.Organizational);
            } else {
                initData.messageType().setValue(MessageType.Informational);
            }
        } else {
            initData.audienceType().setValue(null); // should not appear
            initData.messageType().setValue(null);
        }

        editNew(com.propertyvista.crm.rpc.CrmSiteMap.Communication.BroadcastTemplate.class, initData);
    }
}
