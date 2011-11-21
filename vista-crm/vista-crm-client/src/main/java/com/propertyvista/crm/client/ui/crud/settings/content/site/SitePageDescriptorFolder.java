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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.site.PageDescriptor;
import com.propertyvista.domain.site.PageDescriptor.Type;

class SitePageDescriptorFolder extends VistaTableFolder<PageDescriptor> {

    private final SiteEditorForm parent;

    private final SiteViewer viewer;

    public SitePageDescriptorFolder(SiteEditorForm parent) {
        super(PageDescriptor.class, parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (SiteViewer) parent.getParentView() : null);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PageDescriptor) {
            return new PageDescriptorEditor();
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<PageDescriptor> createDecorator() {
        TableFolderDecorator<PageDescriptor> decor = (TableFolderDecorator<PageDescriptor>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    @Override
    protected void addItem() {
        if (parent.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
            viewer.newChild(parent.getValue().getPrimaryKey());
        }
    }

    @Override
    protected boolean isFolderItemAllowed(PageDescriptor item) {
        return !(Type.findApartment.equals(item.type().getValue()) || Type.residents.equals(item.type().getValue()));
    }

    private class PageDescriptorEditor extends CEntityFolderRowEditor<PageDescriptor> {

        public PageDescriptorEditor() {
            super(PageDescriptor.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().name())) {
                CComponent<?, ?> comp = null;
                if (parent.isEditable()) {
                    comp = inject(column.getObject(), new CLabel());
                } else {
                    comp = inject(column.getObject(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            viewer.viewChild(getValue().getPrimaryKey());
                        }
                    }));
                }
                return comp;
            }
            return super.createCell(column);
        }
    }
}