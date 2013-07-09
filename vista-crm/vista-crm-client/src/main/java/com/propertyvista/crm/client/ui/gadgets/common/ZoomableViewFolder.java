/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm.ZoominRequestHandler;

public abstract class ZoomableViewFolder<E extends IEntity> extends VistaTableFolder<E> {

    public interface IZoomableRowEditorFactory<E extends IEntity> {

        ZoomableViewEntityRowEditor<E> createEditor(ZoomableViewFolder<E> parent, List<EntityFolderColumnDescriptor> columns);

    }

    public static abstract class ZoomableViewEntityRowEditor<E extends IEntity> extends CEntityFolderRowEditor<E> {

        private ZoominRequestHandler zoomInHandler;

        private IObject<?>[] zoomableMembers;

        public ZoomableViewEntityRowEditor(Class<E> clazz, List<EntityFolderColumnDescriptor> columns) {
            super(clazz, columns);
            inheritViewable(false);
            setViewable(true);
        }

        public void initZoomin(ZoominRequestHandler zoomInHandler, IObject<?>... zoomableMembers) {
            this.zoomableMembers = zoomableMembers;
            this.zoomInHandler = zoomInHandler;
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected CComponent<?> createCell(final EntityFolderColumnDescriptor column) {
            if (isZoomable(column.getObject())) {
                CComponent<?> comp = inject(column.getObject(), this.create(column.getObject()));
                ((CField) comp).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        zoomInHandler.onZoomIn(getValue().getMember(column.getObject().getPath()));
                    }
                });
                comp.asWidget().getElement().getStyle().setProperty("textAlign", "right");
                return comp;
            } else {
                return super.createCell(column);
            }
        }

        private boolean isZoomable(IObject<?> member) {
            for (IObject<?> zoomableMember : zoomableMembers) {
                if (member instanceof ICollection) {
                    if (zoomableMember.getPath().toString().startsWith(member.getPath().toString())) {
                        return true;
                    }
                } else if (zoomableMember == member) {
                    return true;
                }
            }
            return false;
        }

    }

    private final IZoomableRowEditorFactory<E> editorfactory;

    private IObject<?>[] zoomableMembers;

    private ZoominRequestHandler zoomInHandler;

    public ZoomableViewFolder(Class<E> clazz, IZoomableRowEditorFactory<E> editorFactory) {
        super(clazz);
        this.editorfactory = editorFactory;
    }

    public void initZoomIn(ZoominRequestHandler zoomInHandler, IObject<?>... zoomableMembers) {
        this.zoomableMembers = zoomableMembers;
        this.zoomInHandler = zoomInHandler;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member.getValueClass().equals(proto().getValueClass())) {
            ZoomableViewEntityRowEditor<E> e = editorfactory.createEditor(this, columns());
            e.initZoomin(zoomInHandler, zoomableMembers);
            return e;
        } else {
            return super.create(member);
        }
    }
}
