/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectCrmUserListService;
import com.propertyvista.crm.rpc.services.selections.SelectCustomerUserListService;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.dto.CommunicationMessageDTO.ContactType;

public class CommunicationEndpointFolder extends VistaTableFolder<CommunicationEndpoint> {
    private final CrmEntityForm<?> parent;

    private static final I18n i18n = I18n.get(CommunicationEndpointFolder.class);

    public CommunicationEndpointFolder(CrmEntityForm<?> parent) {
        super(CommunicationEndpoint.class, i18n.tr("To"), true);
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().email(), "20em"));
        return columns;
    }

    @Override
    protected IFolderDecorator<CommunicationEndpoint> createFolderDecorator() {
        return new VistaTableFolderDecorator<CommunicationEndpoint>(this, true) {
            {
                setShowHeader(true);
            }
        };
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof CrmUser) {
            return new CommunicationEndpointEditor<CrmUser>(CrmUser.class);
        } else if (member instanceof CustomerUser) {
            return new CommunicationEndpointEditor<CustomerUser>(CustomerUser.class);
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<ContactType>(i18n.tr("Select contact type"), EnumSet.allOf(ContactType.class)) {
            @Override
            public boolean onClickOk() {
                final ContactType type = getSelectedType();
                if (type != null) {
                    if (type.equals(ContactType.crmUser)) {
                        new CommunicationEndpointSelectorDialog<CrmUser>(parent.getParentView(), CrmUser.class) {

                            @Override
                            protected AbstractListService<CrmUser> getSelectService() {
                                return GWT.<AbstractListService<CrmUser>> create(SelectCrmUserListService.class);
                            }
                        }.show();
                    } else if (type.equals(ContactType.customerUser)) {
                        new CommunicationEndpointSelectorDialog<CustomerUser>(parent.getParentView(), CustomerUser.class) {

                            @Override
                            protected AbstractListService<CustomerUser> getSelectService() {
                                return GWT.<AbstractListService<CustomerUser>> create(SelectCustomerUserListService.class);
                            }
                        }.show();
                    }
                }
                return true;
            }

            @Override
            public String getEmptySelectionMessage() {
                return i18n.tr("No recipient type to choose from.");
            }
        }.show();
    }

    private class CommunicationEndpointEditor<E extends CommunicationEndpoint> extends CEntityFolderRowEditor<E> {

        public CommunicationEndpointEditor(Class<E> clazz) {
            super(clazz, columns());
        }
    }

    private abstract class CommunicationEndpointSelectorDialog<E extends CommunicationEndpoint> extends EntitySelectorTableVisorController<E> {

        public CommunicationEndpointSelectorDialog(IPane parentView, Class<E> entityClass) {
            super(parentView, entityClass, true, i18n.tr("Select User"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).searchable(true).build(),
                    new MemberColumnDescriptor.Builder(proto().email(), true).build()
            ); //@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        public void onClickOk() {
            if (!getSelectedItems().isEmpty()) {
                for (CommunicationEndpoint selected : getSelectedItems()) {
                    addItem(selected);
                }
            }
        }
    }
}