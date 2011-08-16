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
package com.propertyvista.crm.client.ui.components;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.Uploader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;

import com.propertyvista.common.client.resources.FormImageBundle;
import com.propertyvista.domain.media.File;
import com.propertyvista.domain.media.Media;
import com.propertyvista.misc.ApplicationDocumentServletParameters;
import com.propertyvista.misc.ServletMapping;

public class CrmMediaListViewer extends CrmEntityFolder<Media> {

    private final CrmEntityFolder<Media> parent = this;

    private List<EntityFolderColumnDescriptor> columns;
    {
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().file(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().url(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().youTubeVideoID(), "10em"));
    }

    public CrmMediaListViewer(boolean editable) {
        super(Media.class, i18n.tr("Media"), editable);
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        return columns;
    }

    @Override
    protected IFolderEditorDecorator<Media> createFolderDecorator() {
        CrmTableFolderDecorator<Media> decor = new CrmTableFolderDecorator<Media>(columns(), parent);
        setExternalAddItemProcessing(true);
        decor.addItemAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowPopUpBox<NewMediaBox>(new NewMediaBox()) {
                    @Override
                    protected void onClose(NewMediaBox box) {
                        if (box.getNewItem() != null) {
                            addItem(box.getNewItem());
                        }
                    }
                };
            }
        });
        return decor;
    }

    @Override
    protected CEntityFolderItemEditor<Media> createItem() {
        return new CEntityFolderRowEditor<Media>(Media.class, columns) {
            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                if (column.getObject() == proto().type()) {
                    return inject(column.getObject(), new CLabel());
                } else if (column.getObject() == proto().file()) {
                    return inject(column.getObject(), new CEntityHyperlink(new Command() {
                        @Override
                        public void execute() {
                            String url = GWT.getModuleBaseURL() + ServletMapping.APPLICATIONDOCUMENT + "?" + ApplicationDocumentServletParameters.DATA_ID + "="
                                    + getValue().file().blobKey().getValue();
                            Window.open(url, Media.Type.file.name(), null);
                        }
                    }));
                } else if (column.getObject() == proto().url()) {
                    return inject(column.getObject(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            Window.open(getValue().url().getStringView(), Media.Type.externalUrl.name(), null);
                        }
                    }) {
                        {
                            setWordWrap(true);
                        }
                    });
                } else if (column.getObject() == proto().youTubeVideoID()) {
                    return inject(column.getObject(), new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            Window.open(Media.YOU_TUBE_URL + getValue().youTubeVideoID().getStringView(), Media.Type.youTube.name(), null);
                        }
                    }) {
                        {
                            setWordWrap(true);
                        }
                    });
                } else {
                    return inject(column.getObject(), new CLabel());
                }
            }

            @Override
            public IFolderItemEditorDecorator<Media> createFolderItemDecorator() {
                return new CrmFolderItemDecorator<Media>(parent);
            }
        };
    }

    private class FileUploaderWidget extends HorizontalPanel {

        private File file;

        private final IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

            @Override
            public void onFinish(IUploader uploader) {
                if (uploader.getStatus() == Status.SUCCESS) {
                    file = EntityFactory.create(File.class);
                    //TODO deserialize key
                    file.blobKey().setValue(new Key(uploader.getServerInfo().message));
                    file.filename().setValue(uploader.getServerInfo().name);
                    file.fileSize().setValue(uploader.getServerInfo().size);
                }
            }
        };

        private SimplePanel appDocsListHolder;

        public FileUploaderWidget() {
            super();

            HTML side = new HTML("&nbsp;&nbsp;&nbsp;");
            add(side);

            Element td = DOM.getParent(side.getElement());
            if (td != null) {
                td.getStyle().setBackgroundColor("#50585F");
            }

            add(new HTML("&nbsp;&nbsp;&nbsp;"));
            add(new Image(FormImageBundle.INSTANCE.clip()));

            FlowPanel fp = new FlowPanel();
            fp.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            fp.add(new HTML(HtmlUtils.h4(i18n.tr("Attached Files:"))));
            fp.add(appDocsListHolder = new SimplePanel());
            appDocsListHolder.getElement().getStyle().setMarginTop(0.5, Unit.EM);

            List<String> validExtensions = new ArrayList<String>();
            for (DownloadFormat f : ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS) {
                validExtensions.addAll(Arrays.asList(f.getExtensions()));
            }

            FileUploader uploader = new FileUploader();
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

        private final IUploader.OnStartUploaderHandler onStartUploaderHandler = new IUploader.OnStartUploaderHandler() {

            private Hidden tenantIdParam;

            @Override
            public void onStart(IUploader uploader) {

                if (tenantIdParam != null) {
                    tenantIdParam.removeFromParent();
                }

//                if (tenantId != null) {
//                    uploader.add(tenantIdParam = new Hidden(ApplicationDocumentServletParameters.TENANT_ID, tenantId.toString()));
//                }
            }
        };

        public File getFile() {
            return file;
        }

        // overridden gwtupload.client.Uploader:
        protected class FileUploader extends Uploader {

            public FileUploader() {
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
    }

    private class NewMediaBox extends OkCancelBox {

        private CComboBox<Media.Type> type;

        private FileUploaderWidget file;

        private CTextField text;

        private Media.Type selectedType;;

        private Media newItem;

        public NewMediaBox() {
            super("Select Media Type");
        }

        @Override
        protected Widget createContent() {
            okButton.setEnabled(false);

            final SimplePanel main = new SimplePanel();
            main.setWidget(type = new CComboBox<Media.Type>());
            type.setOptions(EnumSet.allOf(Media.Type.class));
            type.addValueChangeHandler(new ValueChangeHandler<Media.Type>() {
                @Override
                public void onValueChange(ValueChangeEvent<Media.Type> event) {
                    okButton.setEnabled(true);

                    switch (selectedType = event.getValue()) {
                    case file:
                        setCaption(i18n.tr("Select file to upload:"));
                        main.setWidget(file = new FileUploaderWidget());
                        break;
                    case externalUrl:
                        setCaption(i18n.tr("Enter external Url:"));
                        main.setWidget(text = new CTextField());
                        break;
                    case youTube:
                        setCaption(i18n.tr("You Tube Video ID"));
                        main.setWidget(text = new CTextField());
                        break;
                    }
                }
            });

            return main;
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            newItem = EntityFactory.create(Media.class);
            newItem.type().setValue(selectedType);
            switch (selectedType) {
            case file:
                newItem.file().set(file.getFile());
                break;
            case externalUrl:
                newItem.url().setValue(text.getValue());
                break;
            case youTube:
                newItem.youTubeVideoID().setValue(text.getValue());
                break;
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            newItem = null;
        }

        protected Media getNewItem() {
            return newItem;
        }
    }
}
