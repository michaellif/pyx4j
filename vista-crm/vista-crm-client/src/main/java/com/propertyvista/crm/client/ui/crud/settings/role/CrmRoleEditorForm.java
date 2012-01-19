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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.CrmRoleBehaviorDTOListService;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class CrmRoleEditorForm extends CrmEntityForm<CrmRole> {

    public CrmRoleEditorForm(IEditableComponentFactory factory) {
        super(CrmRole.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name())).componentWidth(20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description())).componentWidth(20).build());
        content.setH1(++row, 0, 1, proto().permissions().getMeta().getCaption());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().permissions(), new CrmRoleFolder())).componentWidth(30).build());

        return content;
    }

    private static class CrmRoleFolder extends VistaTableFolder<VistaCrmBehaviorDTO> {

        public CrmRoleFolder() {
            super(VistaCrmBehaviorDTO.class);
            setViewable(true);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(new EntityFolderColumnDescriptor(proto().behavior(), "20em"));
        }

        @Override
        protected void addItem() {

            new EntitySelectorDialog<VistaCrmBehaviorDTO>(VistaCrmBehaviorDTO.class, true, getValue(), i18n.tr("Select Permissions")) {

                @Override
                public boolean onClickOk() {
                    if (!getSelectedItems().isEmpty()) {
                        for (VistaCrmBehaviorDTO item : getSelectedItems()) {
                            addItem(item);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                protected String width() {
                    return "500px";
                }

                @Override
                protected String height() {
                    return "400px";
                }

                @Override
                protected List<ColumnDescriptor> defineColumnDescriptors() {
                    return Arrays.asList(new MemberColumnDescriptor.Builder(proto().permissions()).build());
                }

                @Override
                protected AbstractListService<VistaCrmBehaviorDTO> getSelectService() {
                    return GWT.<AbstractListService<VistaCrmBehaviorDTO>> create(CrmRoleBehaviorDTOListService.class);
                }

            };
        }
    }

}
