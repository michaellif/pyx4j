/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.page;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.essentials.client.upload.UploadPanel;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.richtext.ImageGallery;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.SiteImageResource;

public class PageContentImageProvider extends Dialog implements CancelOption, RichTextImageProvider {

    private static final I18n i18n = I18n.get(PageContentImageProvider.class);

    private final ImageGallery gallery;

    private AsyncCallback<String> selectionHandler;

    private final UploadPanel<IEntity, SiteImageResource> uploadPanel;

    private final Button submitButton;

    private final SiteImageResourceCrudService service;

    private final Map<String, Key> imageResourceMap;

    @SuppressWarnings("unchecked")
    public PageContentImageProvider() {
        super("Image Picker");
        this.setDialogOptions(this);

        service = GWT.create(SiteImageResourceCrudService.class);

        imageResourceMap = new HashMap<String, Key>();

        gallery = new ImageGallery() {
            @Override
            public void onSelectImage(Image image) {
                selectionHandler.onSuccess(image.getUrl());
                PageContentImageProvider.this.hide();
            }

            @Override
            public void onRemoveImage(final Image image) {
                MessageDialog.confirm(i18n.tr("Remove Image"), i18n.tr("Would you like to remove this image?"), new Command() {
                    @Override
                    public void execute() {
                        service.delete(new AsyncCallback<Boolean>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                MessageDialog.error(i18n.tr("Server Error"), i18n.tr("Could not remove image.") + " " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Boolean result) {
                                gallery.removeImage(image);
                            }

                        }, imageResourceMap.get(image.getUrl()));
                    }
                });
            }
        };

        uploadPanel = new UploadPanel<IEntity, SiteImageResource>((UploadService<IEntity, SiteImageResource>) GWT.create(SiteImageResourceUploadService.class)) {
            @Override
            protected void onUploadSubmit() {
                PageContentImageProvider.this.getCancelButton().setEnabled(false);
                submitButton.setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                PageContentImageProvider.this.getCancelButton().setEnabled(true);
                submitButton.setEnabled(true);
                uploadPanel.reset();
            }

            @Override
            protected void onUploadComplete(UploadResponse<SiteImageResource> serverUploadResponse) {
                String url = MediaUtils.createSiteImageResourceUrl(serverUploadResponse.data);
                gallery.addImage(url);

                PageContentImageProvider.this.getCancelButton().setEnabled(true);
                submitButton.setEnabled(true);
            }
        };
        uploadPanel.setSupportedExtensions(SiteImageResourceUploadService.supportedFormats);

        submitButton = new Button(i18n.tr("Submit"));
        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                uploadPanel.uploadSubmit();
            }
        });
        submitButton.getElement().getStyle().setProperty("margin", "3px");

        FlexTable content = new FlexTable();
        int row = 0;
        content.setWidget(row, 0, new Label("Add Image"));
        content.setWidget(row, 1, uploadPanel);
        row++;
        content.setWidget(row, 0, submitButton);
        row++;
        content.getFlexCellFormatter().setColSpan(row, 0, 3);
        content.setWidget(row, 0, new HTML("<hr/>"));
        row++;
        content.getFlexCellFormatter().setColSpan(row, 0, 3);
        content.setWidget(row, 0, gallery);

        setBody(content);

        service.list(new AsyncCallback<EntitySearchResult<SiteImageResource>>() {
            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.error(i18n.tr("Server Error"), i18n.tr("Could not get images.") + " " + caught.getMessage());
            }

            @Override
            public void onSuccess(EntitySearchResult<SiteImageResource> result) {
                for (SiteImageResource rc : result.getData()) {
                    String url = MediaUtils.createSiteImageResourceUrl(rc);
                    imageResourceMap.put(url, rc.getPrimaryKey());
                    gallery.addImage(url);
                }
            }

        }, EntityListCriteria.create(SiteImageResource.class));
    }

    @Override
    public void selectImage(AsyncCallback<String> callback) {
        selectionHandler = callback;
        center();
    }

    @Override
    public boolean onClickCancel() {
        return true;
    }
}
