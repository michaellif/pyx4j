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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.HtmlUtils;
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
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.media.ApplicationDocument.DocumentType;
import com.propertyvista.misc.ApplicationDocumentServletParameters;
import com.propertyvista.misc.ServletMapping;

public class ApplicationDocumentsFolderUploader extends VistaTableFolder<ApplicationDocument> {

    private static I18n i18n = I18n.get(ApplicationDocumentsFolderUploader.class);

    private final DocumentType documentType;

    private Key tenantId;

    public ApplicationDocumentsFolderUploader(DocumentType documentType) {
        super(ApplicationDocument.class);
        this.documentType = documentType;
    }

    private static final List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        ApplicationDocument proto = EntityFactory.getEntityPrototype(ApplicationDocument.class);
        COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.filename(), "25em"));
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileSize(), "5em"));
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

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
            protected void onUploadComplete(UploadResponse serverUploadResponse) {
                ApplicationDocument newDocument = EntityFactory.create(ApplicationDocument.class);
                newDocument.type().setValue(documentType);

                newDocument.blobKey().setValue(serverUploadResponse.uploadKey);
                newDocument.filename().setValue(serverUploadResponse.fileName);
                newDocument.fileSize().setValue(serverUploadResponse.fileSize);
                newDocument.timestamp().setValue(serverUploadResponse.timestamp);
                newDocument.contentMimeType().setValue(serverUploadResponse.fileContentType);

                // add new document to the folder-list:
                addItem(newDocument);
            }
        }.show();
    }

    private class ApplicationDocumentEditor extends CEntityFolderRowEditor<ApplicationDocument> {
        public ApplicationDocumentEditor() {
            super(ApplicationDocument.class, ApplicationDocumentsFolderUploader.COLUMNS);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().filename()) {
                CHyperlink link = new CHyperlink(new Command() {
                    @Override
                    public void execute() {
                        String url = GWT.getModuleBaseURL() + ServletMapping.APPLICATIONDOCUMENT + "?" + ApplicationDocumentServletParameters.DATA_ID + "="
                                + getValue().blobKey().getValue();
                        Window.open(url, "_blank", null);
                    }
                });
                return inject(column.getObject(), link);
            } else {
                return super.createCell(column);
            }
        }
    }

    public void setTenantID(Key id) {
        tenantId = id;
    }

    private class UploaderFolderDecorator extends HorizontalPanel implements IFolderDecorator<ApplicationDocument> {

        private SimplePanel appDocsListHolder;

        public UploaderFolderDecorator() {
            super();

            HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
            add(side);

            Element td = DOM.getParent(side.getElement());
            if (td != null) {
                td.getStyle().setBackgroundColor("#50585F");
            }

            add(new HTML("&nbsp;&nbsp;&nbsp;"));
            add(new Image(VistaImages.INSTANCE.clip()));

            FlowPanel fp = new FlowPanel();
            fp.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            fp.add(new HTML(HtmlUtils.h4(i18n.tr("Attached Files") + ":")));
            fp.add(appDocsListHolder = new SimplePanel());
            appDocsListHolder.getElement().getStyle().setMarginTop(0.5, Unit.EM);

            add(fp);
            setCellVerticalAlignment(fp, HorizontalPanel.ALIGN_TOP);
            setCellWidth(fp, "100%");
        }

        @Override
        public void onValueChange(ValueChangeEvent<IList<ApplicationDocument>> event) {
        }

        @Override
        public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
            return null;
        }

        @Override
        public void setAddButtonVisible(boolean show) {
            // TODO Auto-generated method stub
        }

        @Override
        public void setComponent(CEntityFolder<ApplicationDocument> w) {
            // TODO Auto-generated method stub
        }
    }

}
