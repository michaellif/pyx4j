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
package com.propertyvista.crm.client.ui.crud.administration.website.content.pages;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.site.PageDescriptor;

class PageDescriptorFolder extends VistaTableFolder<PageDescriptor> {

    private final PageForm parent;

    private final PageViewer viewer;

    public PageDescriptorFolder(PageForm parent) {
        super(PageDescriptor.class, parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (PageViewer) parent.getParentView() : null);
        this.setAddable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        return columns;
    }

    @Override
    protected CEntityForm<PageDescriptor> createItemForm(IObject<?> member) {
        return new PageDescriptorEditor();
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

        @Override
        protected CField<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().name())) {
                CField<?, ?> comp = new CLabel<String>();
                comp.setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        viewer.viewChild(getValue().getPrimaryKey());
                    }
                });
                inject(column.getObject(), comp);
                return comp;
            }
            return super.createCell(column);

        }
    }
}