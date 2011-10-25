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

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.Uploader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.media.ApplicationDocument.DocumentType;
import com.propertyvista.misc.ApplicationDocumentServletParameters;
import com.propertyvista.misc.ServletMapping;

public class ApplicationDocumentsFolderUploader extends CEntityFolder<ApplicationDocument> {

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
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ApplicationDocument) {
            return new ApplicationDocumentEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected IFolderDecorator<ApplicationDocument> createDecorator() {
        return new UploaderFolderDecorator();
    }

    static class ApplicationDocumentEditor extends CEntityFolderRowEditor<ApplicationDocument> {
        public ApplicationDocumentEditor() {
            super(ApplicationDocument.class, ApplicationDocumentsFolderUploader.COLUMNS);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().filename()) {
                CHyperlink link = new CHyperlink(new Command() {
                    @Override
                    public void execute() {
                        String url = GWT.getModuleBaseURL() + ServletMapping.APPLICATIONDOCUMENT + "?" + ApplicationDocumentServletParameters.DATA_ID + "="
                                + getValue().dataId().getValue();
                        Window.open(url, "_blank", null);
                    }
                });
                return inject(column.getObject(), link);
            } else {
                return super.createCell(column);
            }
        }

        @Override
        public IFolderItemDecorator createDecorator() {
            return new TableFolderItemDecorator(VistaImages.INSTANCE, i18n.tr("Remove file"));
        }
    }

    @Override
    protected IFolderItemDecorator<ApplicationDocument> createItemDecorator() {
        return new TableFolderItemDecorator<ApplicationDocument>(VistaImages.INSTANCE);
    }

    protected void callSuperRemoveItem(final CEntityFolderItem<ApplicationDocument> comp) {
        super.removeItem(comp);
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

            List<String> validExtensions = new ArrayList<String>();
            for (DownloadFormat f : ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS) {
                validExtensions.addAll(Arrays.asList(f.getExtensions()));
            }

            DocumentsUploader uploader = new DocumentsUploader();
            uploader.setValidExtensions(validExtensions.toArray(new String[validExtensions.size()]));
            uploader.addOnStartUploadHandler(onStartUploaderHandler);
            uploader.addOnFinishUploadHandler(onFinishUploaderHandler);

            uploader.getFileInput().setText(i18n.tr("Browse..."));
            uploader.getFileInput().getWidget().setStyleName("customButton");
            uploader.getFileInput().getWidget().setSize("120px", "27px");
            uploader.getStatusWidget().getWidget().getElement().getStyle().setMarginLeft(1, Unit.EM);
            fp.add(uploader);

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

        private final IUploader.OnStartUploaderHandler onStartUploaderHandler = new IUploader.OnStartUploaderHandler() {

            private Hidden tenantIdParam;

            @Override
            public void onStart(IUploader uploader) {

                if (tenantIdParam != null) {
                    tenantIdParam.removeFromParent();
                }

                if (tenantId != null) {
                    uploader.add(tenantIdParam = new Hidden(ApplicationDocumentServletParameters.TENANT_ID, tenantId.toString()));
                }
            }
        };

        private final IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

            @Override
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus() == Status.SUCCESS) {
                    ApplicationDocument newDocument = EntityFactory.create(ApplicationDocument.class);
                    newDocument.type().setValue(documentType);
                    //TODO deserialize key
                    newDocument.dataId().setValue(new Key(uploader.getServerInfo().message));
                    newDocument.filename().setValue(uploader.getServerInfo().name);
                    newDocument.fileSize().setValue((long) uploader.getServerInfo().size);
                    // add new document to the folder-list:
                    getValue().add(newDocument);
                    setValue(getValue());
                }
            }
        };

        // overridden gwtupload.client.Uploader:
        protected class DocumentsUploader extends Uploader {

            public DocumentsUploader() {
                super(FileInputType.BUTTON, true);
                super.setStatusWidget(new BaseUploadStatus());
                getStatusWidget().setCancelConfiguration(IUploadStatus.GMAIL_CANCEL_CFG);
                getStatusWidget().getWidget().getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
                getStatusWidget().getWidget().getElement().getStyle().setBorderWidth(1, Unit.PX);
                getStatusWidget().getWidget().getElement().getStyle().setBorderColor("#bbb");
                if (getStatusWidget().getWidget().getClass().equals(HorizontalPanel.class)) {
                    ((HorizontalPanel) getStatusWidget().getWidget()).setSpacing(10);
                }
            }

            @Override
            protected void onFinishUpload() {
                super.onFinishUpload();
                if (getStatus() == Status.REPEATED) {
                    getStatusWidget().setError(getI18NConstants().uploaderAlreadyDone());
                }
                getStatusWidget().setStatus(Status.UNINITIALIZED);
                reuse();
                assignNewNameToFileInput();
            }
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
