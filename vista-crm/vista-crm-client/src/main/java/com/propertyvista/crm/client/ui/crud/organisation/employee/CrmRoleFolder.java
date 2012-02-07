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

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.rpc.services.organization.SelectCrmRoleListService;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleFolder extends VistaTableFolder<CrmRole> {

    private static final I18n i18n = I18n.get(CrmRoleFolder.class);

    public CrmRoleFolder(boolean isModifiable) {
        super(CrmRole.class, isModifiable);
        setViewable(true);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().name(), "30em"),
                new EntityFolderColumnDescriptor(proto().description(), "30em"),
                new EntityFolderColumnDescriptor(proto().behaviors(), "30em")
        );//@formatter:on
    }

    @Override
    protected IFolderDecorator<CrmRole> createDecorator() {
        return new VistaTableFolderDecorator<CrmRole>(this, this.isEditable()) {
            {
                setShowHeader(false);
            }
        };
    };

    @Override
    protected void addItem() {
        new CrmRoleSelectorDialog().show();
    }

    private class CrmRoleSelectorDialog extends EntitySelectorDialog<CrmRole> {

        public CrmRoleSelectorDialog() {
            super(CrmRole.class, true, getValue(), i18n.tr("Select roles"));
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (CrmRole role : getSelectedItems()) {
                    addItem(role);
                }
                return true;
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
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().behaviors()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<CrmRole> getSelectService() {
            return GWT.<AbstractListService<CrmRole>> create(SelectCrmRoleListService.class);
        }

    }
}
