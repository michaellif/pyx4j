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
package com.propertyvista.crm.client.ui.components.cms;

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

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.gwt.client.upload.UploadPanel;
import com.pyx4j.gwt.client.upload.UploadReceiver;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.CloseOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.richtext.ImageGallery;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.blob.MediaFileBlob;
import com.propertyvista.domain.site.SiteImageResource;

public class SiteImageResourceProvider extends Dialog implements CloseOption, RichTextImageProvider {

    private static final I18n i18n = I18n.get(SiteImageResourceProvider.class);

    private final ImageGallery gallery;

    private AsyncCallback<String> imageSelectionHandler;

    private AsyncCallback<SiteImageResource> resourceSelectionHandler;

    private final UploadPanel<IEntity, MediaFileBlob> uploadPanel;

    private final Button submitButton;

    private final SiteImageResourceCrudService service;

    private final Map<String, SiteImageResource> imageResourceMap;

    @SuppressWarnings("unchecked")
    public SiteImageResourceProvider() {
        super("Image Picker");
        this.setDialogOptions(this);

        service = GWT.create(SiteImageResourceCrudService.class);

        imageResourceMap = new HashMap<String, SiteImageResource>();

        gallery = new ImageGallery() {
            @Override
            public void onSelectImage(Image image) {
                if (imageSelectionHandler != null) {
                    imageSelectionHandler.onSuccess(image.getUrl());
                }
                if (resourceSelectionHandler != null) {
                    resourceSelectionHandler.onSuccess(imageResourceMap.get(image.getUrl()));
                }
                SiteImageResourceProvider.this.hide(false);
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

                        }, imageResourceMap.get(image.getUrl()).getPrimaryKey());
                    }
                });
            }
        };

        uploadPanel = new UploadPanel<IEntity, MediaFileBlob>((UploadService<IEntity, MediaFileBlob>) GWT.create(SiteImageResourceUploadService.class),
                new UploadReceiver() {

                    @Override
                    public void onUploadComplete(IFile<?> result) {
                        SiteImageResource x = EntityFactory.create(SiteImageResource.class);
                        x.file().set(result);
                        String url = MediaUtils.createSiteImageResourceUrl(x);
                        imageResourceMap.put(url, x);
                        gallery.addImage(url, result.fileName().getStringView());

                        SiteImageResourceProvider.this.getCloseButton().setEnabled(true);
                        submitButton.setEnabled(true);
                    }
                }) {
            @Override
            protected void onUploadSubmit() {
                SiteImageResourceProvider.this.getCloseButton().setEnabled(false);
                submitButton.setEnabled(false);
            }

            @Override
            protected void onUploadError(UploadError error, String args) {
                super.onUploadError(error, args);
                SiteImageResourceProvider.this.getCloseButton().setEnabled(true);
                submitButton.setEnabled(true);
                uploadPanel.reset();
            }

        };

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
                    imageResourceMap.put(url, rc);
                    gallery.addImage(url, rc.file().fileName().getStringView());
                }
            }

        }, EntityListCriteria.create(SiteImageResource.class));
    }

    public void selectResource(AsyncCallback<SiteImageResource> callback) {
        resourceSelectionHandler = callback;
        layout();
    }

    @Override
    public void selectImage(AsyncCallback<String> callback) {
        imageSelectionHandler = callback;
        layout();
    }

    @Override
    public boolean onClickClose() {
        if (imageSelectionHandler != null) {
            imageSelectionHandler.onSuccess(null);
        }
        if (resourceSelectionHandler != null) {
            resourceSelectionHandler.onSuccess(null);
        }
        return true;
    }
}
