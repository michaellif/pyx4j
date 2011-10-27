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
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.site.PageDescriptor;

class PageDescriptorFolder extends VistaTableFolder<PageDescriptor> {

    private final PageEditorForm parent;

    private final PageViewer viewer;

    public PageDescriptorFolder(PageEditorForm parent) {
        super(PageDescriptor.class, !parent.isEditable());
        this.parent = parent;
        this.viewer = (!parent.isEditable() ? (PageViewer) parent.getParentView() : null);
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "25em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PageDescriptor) {
            return new PageDescriptorEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        if (parent.getValue().getPrimaryKey() != null) { // parent shouldn't be new unsaved value!..
            viewer.newChild(parent.getValue().getPrimaryKey());
        }
    }

    class PageDescriptorEditor extends CEntityFolderRowEditor<PageDescriptor> {

        public PageDescriptorEditor() {
            super(PageDescriptor.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject().equals(proto().name())) {
                CComponent<?> comp = null;
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