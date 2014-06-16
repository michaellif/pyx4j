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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
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
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointFolder extends VistaTableFolder<CommunicationEndpointDTO> {
    private final CrmEntityForm<?> parent;

    private static final I18n i18n = I18n.get(CommunicationEndpointFolder.class);

    public CommunicationEndpointFolder(CrmEntityForm<?> parent) {
        super(CommunicationEndpointDTO.class, i18n.tr("Message Recipients"), false);
        setAddable(true);
        setRemovable(true);
        setOrderable(true);
        setNoDataLabel(null);
        this.parent = parent;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().type(), "20em", true));
        columns.add(new FolderColumnDescriptor(proto().name(), "20em", true));
        return columns;
    }

    @Override
    protected IFolderDecorator<CommunicationEndpointDTO> createFolderDecorator() {
        return new VistaTableFolderDecorator<CommunicationEndpointDTO>(this, true) {
            {
                setShowHeader(true);
            }
        };
    }

    @Override
    protected CForm<? extends CommunicationEndpointDTO> createItemForm(IObject<?> member) {
        return new CommunicationEndpointEditor();
    }

    @Override
    protected void addItem() {
        new SelectEnumDialog<ContactType>(i18n.tr("Select contact type"), EnumSet.of(ContactType.Employee, ContactType.Tenants)) {
            @Override
            public boolean onClickOk() {
                final ContactType type = getSelectedType();
                if (type != null) {
                    if (type.equals(ContactType.Employee)) {
                        new CommunicationEndpointSelectorDialog<CrmUser>(parent.getParentView(), CrmUser.class) {

                            @Override
                            protected AbstractListService<CrmUser> getSelectService() {
                                return GWT.<AbstractListService<CrmUser>> create(SelectCrmUserListService.class);
                            }
                        }.show();
                    } else if (type.equals(ContactType.Tenants)) {
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

    private class CommunicationEndpointEditor extends CFolderRowEditor<CommunicationEndpointDTO> {

        public CommunicationEndpointEditor() {
            super(CommunicationEndpointDTO.class, columns());
        }
    }

    private abstract class CommunicationEndpointSelectorDialog<E extends AbstractPmcUser> extends EntitySelectorTableVisorController<E> {

        public CommunicationEndpointSelectorDialog(IPane parentView, Class<E> entityClass) {
            super(parentView, entityClass, true, i18n.tr("Select User"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(new MemberColumnDescriptor.Builder(proto().name()).searchable(true).build());
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        public void onClickOk() {
            if (!getSelectedItems().isEmpty()) {
                for (AbstractPmcUser selected : getSelectedItems()) {
                    CommunicationEndpointDTO proto = EntityFactory.create(CommunicationEndpointDTO.class);
                    proto.name().set(selected.name());
                    proto.type().setValue(selected.getInstanceValueClass().equals(CustomerUser.class) ? ContactType.Tenants : ContactType.Employee);
                    proto.endpoint().set(selected);
                    addItem(proto);
                }
            }
        }
    }
}