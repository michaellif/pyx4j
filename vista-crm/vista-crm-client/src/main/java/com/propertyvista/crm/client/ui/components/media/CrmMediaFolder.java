/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.media;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdFormat;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdValidator;
import com.propertyvista.crm.client.ui.components.cms.FileUploadHyperlink;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class CrmMediaFolder extends VistaBoxFolder<Media> {

    private static final I18n i18n = I18n.get(CrmMediaFolder.class);

    private final ImageTarget imageTarget;

    public CrmMediaFolder(boolean modifyable, ImageTarget imageTarget) {
        super(Media.class, "", modifyable);
        this.imageTarget = imageTarget;
    }

    @Override
    protected void createNewEntity(AsyncCallback<Media> callback) {
        Media newEntity = EntityFactory.create(Media.class);

        newEntity.visibility().setValue(PublicVisibilityType.global);

        callback.onSuccess(newEntity);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Media) {
            return new CrmMediaEditor();
        } else {
            return super.create(member);
        }
    }

    class CrmMediaEditor extends CEntityDecoratableForm<Media> {

        private Image thumbnail;

        public CrmMediaEditor() {
            super(Media.class);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 15).build());

            CTextField utubeEditor = null;
            main.setWidget(++row, 0, new FormDecoratorBuilder(utubeEditor = (CTextField) inject(proto().youTubeVideoID()), 15).build());
            utubeEditor.addValueValidator(new YouTubeVideoIdValidator());
            utubeEditor.setFormat(new YouTubeVideoIdFormat());

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().url()), 15).build());
            Command showMediaCommand = new Command() {
                @Override
                public void execute() {
                    showMedia();
                }
            };
            ((CField) get(proto().youTubeVideoID())).setNavigationCommand(showMediaCommand);
            ((CField) get(proto().url())).setNavigationCommand(showMediaCommand);

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().file(), new FileUploadHyperlink(imageTarget, new Command() {
                @Override
                public void execute() {
                    showMedia();
                }
            })), 15).build());

            row = -1;
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().caption()), 15).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().visibility()), 7).build());
            main.getFlexCellFormatter().setRowSpan(row, 1, 3);

            SimplePanel thumbnailWrap = new SimplePanel();
            thumbnailWrap.setWidget(thumbnail = new Image());

            int width = 0;
            switch (imageTarget) {
            case Building:
                width = ImageConsts.BUILDING_SMALL.width;
            case Floorplan:
                width = ImageConsts.FLOORPLAN_SMALL.width;
            }

            thumbnailWrap.getElement().getStyle().setWidth(width, Style.Unit.PX);
            thumbnailWrap.getElement().getStyle().setMarginLeft(5, Unit.EM);

            thumbnail.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showMedia();
                }
            });

            HorizontalPanel wrap = new HorizontalPanel();
            wrap.add(main);
            wrap.add(thumbnailWrap);
            return wrap;
        }

        @Override
        public void addValidations() {
            if (CrmMediaFolder.this.isEditable()) {
                @SuppressWarnings("unchecked")
                CComboBox<Media.Type> type = (CComboBox<Media.Type>) get(proto().type());
                type.addValueChangeHandler(new ValueChangeHandler<Media.Type>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Media.Type> event) {
                        setVisibility(event.getValue());
                    }
                });

                // Validate media completeness:
                addValueValidator(new MediaItemValidator());
                get(proto().youTubeVideoID()).addValueValidator(new YouTubeVideoIdValidator());
            }
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            setVisibility(getValue().type().getValue());
            get(proto().type()).setViewable(getValue().getPrimaryKey() != null);
        }

        private void setVisibility(Media.Type type) {
            get(proto().youTubeVideoID()).setVisible(false);
            get(proto().url()).setVisible(false);
            get(proto().file()).setVisible(false);
            thumbnail.setVisible(false);

            if (type != null) {
                switch (type) {
                case file:
                    get(proto().file()).setVisible(true);
                    if (!getValue().file().blobKey().isNull() & getValue().getPrimaryKey() != null) {
                        thumbnail.setVisible(true);
                        thumbnail.setUrl(GWT.getModuleBaseURL() + DeploymentConsts.mediaImagesServletMapping + getValue().getPrimaryKey().toString() + "/"
                                + ThumbnailSize.small.name() + "." + ImageConsts.THUMBNAIL_TYPE);
                    }
                    break;
                case externalUrl:
                    get(proto().url()).setVisible(true);
                    break;
                case youTube:
                    get(proto().youTubeVideoID()).setVisible(true);
                    break;
                }
            }
        }

        private void showMedia() {
            Media.Type type = getValue().type().getValue();
            switch (type) {
            case file:
                if (getValue().id().isNull()) {
                    MessageDialog.error(i18n.tr("Upload Error"), i18n.tr("Please save this first"));
                } else {
                    MediaFileViewDialog dialog = new MediaFileViewDialog();
                    dialog.title = getValue().caption().getValue();
                    dialog.mediaId = getValue().id().getValue();
                    dialog.show();
                }
                break;

            case externalUrl:
                String url = getValue().url().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                Window.open(url, Media.Type.externalUrl.name(), null);
                break;

            case youTube:
                YouTubePlayVideoDialog dialog = new YouTubePlayVideoDialog();
                dialog.title = getValue().caption().getValue();
                dialog.videoId = getValue().youTubeVideoID().getValue();
                dialog.show();
                break;
            }
        }
    }
}
