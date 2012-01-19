/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.domain.media.ApplicationDocument;

public class ApplicationDocumentsFolderUploader extends VistaTableFolder<ApplicationDocument> {

    private static I18n i18n = I18n.get(ApplicationDocumentsFolderUploader.class);

    private Key tenantId;

    public ApplicationDocumentsFolderUploader() {
        super(ApplicationDocument.class);
    }

    private static final List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        ApplicationDocument proto = EntityFactory.getEntityPrototype(ApplicationDocument.class);
        COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileName(), "25em"));
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileSize(), "5em"));
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

//    @Override
//    protected IFolderDecorator<ApplicationDocument> createDecorator() {
//        return new UploaderFolderDecorator();
//    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ApplicationDocument) {
            return new ApplicationDocumentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new ApplicationDocumentUploaderDialog(i18n.tr("Upload Document File"), tenantId) {

            @Override
            protected void onUploadComplete(UploadResponse<ApplicationDocument> serverUploadResponse) {
                // add new document to the folder-list:
                addItem(serverUploadResponse.data);
            }
        }.show();
    }

    private class ApplicationDocumentEditor extends CEntityFolderRowEditor<ApplicationDocument> {
        public ApplicationDocumentEditor() {
            super(ApplicationDocument.class, ApplicationDocumentsFolderUploader.COLUMNS);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().fileName()) {
                CHyperlink link = new CHyperlink(new Command() {
                    @Override
                    public void execute() {
                        Window.open(MediaUtils.createApplicationDocumentUrl(getValue()), "_blank", null);
                    }
                });
                return inject(column.getObject(), link);
            } else if (column.getObject() == proto().fileSize()) {
                CComponent<?, ?> comp = inject(column.getObject());
                comp.inheritContainerAccessRules(false);
                comp.setViewable(true);
                return comp;
            } else {
                return super.createCell(column);
            }
        }
    }

    public void setTenantID(Key id) {
        tenantId = id;
    }

    private class UploaderFolderDecorator extends HorizontalPanel implements IFolderDecorator<ApplicationDocument> {

        private final VistaTableFolderDecorator<ApplicationDocument> vistaTableFolder;

        public UploaderFolderDecorator() {

            HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
            add(side);

            Element td = DOM.getParent(side.getElement());
            if (td != null) {
                td.getStyle().setBackgroundColor("#50585F");
            }

            add(new HTML("&nbsp;&nbsp;&nbsp;"));
            add(new Image(VistaImages.INSTANCE.clip()));

            vistaTableFolder = new VistaTableFolderDecorator<ApplicationDocument>(ApplicationDocumentsFolderUploader.this, true);
            vistaTableFolder.getElement().getStyle().setMarginLeft(1, Unit.EM);
            add(vistaTableFolder);
        }

        @Override
        public void onValueChange(ValueChangeEvent<IList<ApplicationDocument>> event) {
            vistaTableFolder.onValueChange(event);
        }

        @Override
        public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
            return vistaTableFolder.addItemAddClickHandler(handler);
        }

        @Override
        public void setAddButtonVisible(boolean show) {
            vistaTableFolder.setAddButtonVisible(show);
        }

        @Override
        public void setComponent(CEntityFolder<ApplicationDocument> w) {
            vistaTableFolder.setComponent(w);
        }
    }

}
