/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.components;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentsList;
import com.propertyvista.portal.rpc.pt.services.ApplicationDocumentsService;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.rpc.shared.VoidSerializable;

public class ApplicationDocumentsUpload extends HorizontalPanel {

    private static I18n i18n = I18nFactory.getI18n(ApplicationDocumentsUpload.class);

    VerticalPanel docList;

    Long tenantId;

    DocumentType documentType;

    final ApplicationDocumentsService applicationDocumentsService = (ApplicationDocumentsService) GWT.create(ApplicationDocumentsService.class);

    public ApplicationDocumentsUpload(DocumentType documentType) {
        this.documentType = documentType;

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
        fp.add(docList = new VerticalPanel());
        docList.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        docList.getElement().getStyle().setPaddingTop(1, Unit.EM);
        docList.getElement().getStyle().setPaddingBottom(1, Unit.EM);

        SingleUploader uploader = new SingleUploader(FileInputType.BUTTON, new BaseUploadStatus());
        uploader.setAutoSubmit(true);
        uploader.avoidRepeatFiles(true);
        uploader.setValidExtensions(new String[] { "jpg", "jpeg", "gif", "png", "tiff", "bmp", "pdf" });
        uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
        uploader.getFileInput().setText(i18n.tr("Browse for File"));
        uploader.getFileInput().getWidget().setStyleName("customButton");
        uploader.getFileInput().getWidget().setSize("152px", "27px");
        fp.add(uploader);

        add(fp);
        setCellVerticalAlignment(fp, HorizontalPanel.ALIGN_TOP);
        setCellWidth(fp, "100%");

    }

    private final IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {

        @Override
        public void onFinish(IUploader uploader) {
            if (uploader.getStatus() == Status.SUCCESS) {
                updateFileList(tenantId);
            }
        }
    };

    public void updateFileList(final Long tenantId) {

        this.tenantId = tenantId;

        docList.clear();

        if (applicationDocumentsService != null) {
            applicationDocumentsService.retrieveAttachments(new AsyncCallback<ApplicationDocumentsList>() {

                @Override
                public void onSuccess(ApplicationDocumentsList result) {
                    for (final ApplicationDocument doc : result.documents()) {

                        CHyperlink link = new CHyperlink(null, new Command() {
                            @Override
                            public void execute() {
                                //TODO: show file here... 
                            }
                        });

                        final Image remove = new Image(SiteImages.INSTANCE.delRow());
                        remove.getElement().getStyle().setCursor(Cursor.POINTER);
                        remove.addClickHandler(new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                                applicationDocumentsService.removeAttachment(new AsyncCallback<VoidSerializable>() {

                                    @Override
                                    public void onSuccess(VoidSerializable result) {
                                        updateFileList(tenantId);
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        // TODO Auto-generated method stub
                                    }
                                }, doc.id().getValue());
                            }
                        });

                        link.setValue(doc.filename().getStringView());

                        HorizontalPanel item = new HorizontalPanel();
                        item.add(link);
                        item.setCellWidth(link.asWidget(), "152px");

                        item.add(remove);
                        item.setCellVerticalAlignment(remove, HasVerticalAlignment.ALIGN_MIDDLE);

                        docList.add(item);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub
                }
            }, tenantId, documentType);
        }

    }
}