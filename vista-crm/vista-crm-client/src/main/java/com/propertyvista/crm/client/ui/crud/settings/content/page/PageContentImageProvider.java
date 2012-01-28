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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

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
import com.pyx4j.widgets.client.richtext.ImageGallery;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceCrudService;
import com.propertyvista.crm.rpc.services.admin.SiteImageResourceUploadService;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PageContentImageProvider extends Dialog implements CancelOption, RichTextImageProvider {

    private static final I18n i18n = I18n.get(PageContentImageProvider.class);

    private final ImageGallery gallery;

    private AsyncCallback<String> selectionHandler;

    private final UploadPanel<IEntity, SiteImageResource> uploadPanel;

    private final Button submitButton;

    private final TextBox imgTitle;

    private final SiteImageResourceCrudService service;

    private final Map<Image, Key> imageResourceMap;

    @SuppressWarnings("unchecked")
    public PageContentImageProvider() {
        super("Image Picker");
        this.setDialogOptions(this);

        service = GWT.create(SiteImageResourceCrudService.class);

        imageResourceMap = new HashMap<Image, Key>();

        gallery = new ImageGallery() {
            @Override
            public void onImageSelected(Image image) {
                selectionHandler.onSuccess(image.getUrl());
                PageContentImageProvider.this.hide();
            }

            @Override
            public void onImageRemoved(Image image) {
                // TODO use service to remove on server
                service.delete(new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        // TODO Auto-generated method stub

                    }

                }, imageResourceMap.get(image));
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
                Image image = new Image(url);
                String title;
                if ((title = imgTitle.getText()) == null) {
                    title = serverUploadResponse.data.fileInfo().fileName().getValue();
                } else {
                    imgTitle.setText(null);
                }
                image.setTitle(title);
                gallery.addImage(image);
                PageContentImageProvider.this.getCancelButton().setEnabled(true);
                submitButton.setEnabled(true);
            }
        };
        uploadPanel.setSupportedExtensions(SiteImageResourceUploadService.supportedFormats);
        uploadPanel.setServletPath(GWT.getModuleBaseURL() + DeploymentConsts.siteImageResourceServletMapping);

        imgTitle = new TextBox();

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
        content.setWidget(row, 0, new Label("Image Title"));
        content.setWidget(row, 1, imgTitle);
        row++;
        content.setWidget(row, 0, submitButton);
        row++;
        content.getFlexCellFormatter().setColSpan(row, 0, 3);
        content.setWidget(row, 0, new HTML("<hr/>"));
        row++;
        content.getFlexCellFormatter().setColSpan(row, 0, 3);
        content.setWidget(row, 0, gallery);

        setBody(content);

        // TODO use service to get list from server
        service.list(new AsyncCallback<EntitySearchResult<SiteImageResource>>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                return;
            }

            @Override
            public void onSuccess(EntitySearchResult<SiteImageResource> result) {
                for (SiteImageResource rc : result.getData()) {
                    String url = MediaUtils.createSiteImageResourceUrl(rc);
                    Image image = new Image(url);
                    imageResourceMap.put(image, rc.getPrimaryKey());
                    gallery.addImage(image);
                }
            }

        }, EntityListCriteria.create(SiteImageResource.class));

        // add sample images
        List<Image> images = new ArrayList<Image>();
        Image image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/1997.149.9.jpg");
        image.setTitle("Reclining Nude");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT1308.jpg");
        image.setTitle("The Mountain");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT2533.jpg");
        image.setTitle("Juan Gris");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT7873.jpg");
        image.setTitle("The Marketplace, Vitebsk");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT6414.jpg");
        image.setTitle("Self-Portrait");
        images.add(image);
        gallery.setImages(images);
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
