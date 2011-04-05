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
package com.propertyvista.portal.client.ptapp.ui.components;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.propertyvista.portal.rpc.pt.ServletMapping;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CNumberLabel;

public class ApplicationDocumentsFolderUploader extends CEntityFolder<ApplicationDocument> {

    private static I18n i18n = I18nFactory.getI18n(ApplicationDocumentsFolderUploader.class);

    private final DocumentType documentType;

    private Long tenantId;

    public ApplicationDocumentsFolderUploader(DocumentType documentType) {
        super(ApplicationDocument.class);
        this.documentType = documentType;
    }

    private List<EntityFolderColumnDescriptor> columns;
    {
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().filename(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().fileSize(), "5em"));
    }

    @Override
    protected FolderDecorator<ApplicationDocument> createFolderDecorator() {
        return new UploaderFolderDecorator();
    }

    @Override
    protected CEntityFolderItem<ApplicationDocument> createItem() {
        return new CEntityFolderRow<ApplicationDocument>(ApplicationDocument.class, columns) {

            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                if (column.getObject() == proto().filename()) {
                    CHyperlink link = new CHyperlink(new Command() {
                        @Override
                        public void execute() {
                            String url = GWT.getModuleBaseURL() + ServletMapping.APPLICATIONDOCUMENT + "?" + ApplicationDocumentServletParameters.DOCUMENT_ID
                                    + "=" + getValue().id().getValue();
                            Window.open(url, "_blank", null);
                        }
                    });
                    return inject(column.getObject(), link);
                } else if (column.getObject() == proto().fileSize()) {
                    return inject(column.getObject(), new CNumberLabel());
                } else {
                    return super.createCell(column);
                }
            }

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new TableFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove file"));
            }
        };
    }

    protected void callSuperRemoveItem(final CEntityFolderItem<ApplicationDocument> comp, final FolderItemDecorator folderItemDecorator) {
        super.removeItem(comp, folderItemDecorator);
    }

    public void setTenantID(Long id) {
        tenantId = id;
    }

    private class UploaderFolderDecorator extends HorizontalPanel implements FolderDecorator<ApplicationDocument> {

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
            add(new Image(SiteImages.INSTANCE.clip()));

            final FlowPanel fp = new FlowPanel();
            fp.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            fp.add(new HTML(HtmlUtils.h4(i18n.tr("Attached Files:"))));
            fp.add(appDocsListHolder = new SimplePanel());

            SingleUploader uploader = new SingleUploader(FileInputType.BUTTON, new BaseUploadStatus());
            uploader.add(new Hidden(ApplicationDocumentServletParameters.DOCUMENT_TYPE, documentType.name()));
            uploader.setAutoSubmit(true);
            //uploader.avoidRepeatFiles(true);

            List<String> validExtensions = new ArrayList<String>();
            for (DownloadFormat f : ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS) {
                validExtensions.addAll(Arrays.asList(f.getExtensions()));
            }

            uploader.setValidExtensions(validExtensions.toArray(new String[validExtensions.size()]));
            uploader.addOnStartUploadHandler(onStartUploaderHandler);
            uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
            uploader.getFileInput().setText(i18n.tr("Browse for File"));
            uploader.getFileInput().getWidget().setStyleName("customButton");
            uploader.getFileInput().getWidget().setSize("152px", "27px");
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

        @Override
        public void setFolder(CEntityFolder<?> w) {
            appDocsListHolder.setWidget(w.getContent());
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
                    newDocument.dataId().setValue(Long.parseLong(uploader.getServerInfo().message));
                    newDocument.filename().setValue(uploader.getServerInfo().name);
                    newDocument.fileSize().setValue((long) uploader.getServerInfo().size);

                    IList<ApplicationDocument> docList = getValue();
                    docList.add(newDocument);
                    setValue(docList);
                }
            }
        };
    };
}
