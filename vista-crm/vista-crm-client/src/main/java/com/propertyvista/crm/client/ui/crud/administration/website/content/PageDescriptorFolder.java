/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.PageDescriptor;

class PageDescriptorFolder extends VistaTableFolder<PageDescriptor> {

    private final ContentForm parent;

    private final ContentViewer viewer;

    public PageDescriptorFolder(ContentForm parent) {
        super(PageDescriptor.class, parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (ContentViewer) parent.getParentView() : null);
        setAddable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PageDescriptor) {
            return new PageDescriptorEditor();
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<PageDescriptor> createFolderDecorator() {
        TableFolderDecorator<PageDescriptor> decor = (TableFolderDecorator<PageDescriptor>) super.createFolderDecorator();
        decor.setShowHeader(false);
        decor.setAddButtonVisible(false);
        return decor;
    }

    private class PageDescriptorEditor extends CEntityFolderRowEditor<PageDescriptor> {

        public PageDescriptorEditor() {
            super(PageDescriptor.class, columns());
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().name())) {
                CComponent<?> comp = null;
                comp = inject(column.getObject(), new CLabel<String>());
                ((CField) comp).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        viewer.viewChild(getValue().getPrimaryKey());
                    }
                });
                return comp;
            }
            return super.createCell(column);
        }
    }

    @Override
    public void adopt(final CComponent<?> component) {
        // first two items are not editable
        if (this.getItemCount() < 2) {
            @SuppressWarnings("unchecked")
            CEntityFolderItem<PageDescriptor> item = (CEntityFolderItem<PageDescriptor>) component;
            item.setMovable(false);
            item.setRemovable(false);
        }
        super.adopt(component);
    }
}