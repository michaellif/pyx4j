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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdValidator;
import com.propertyvista.crm.client.ui.components.cms.FileUploadHyperlink;
import com.propertyvista.domain.media.Media;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class CrmMediaFolder extends VistaBoxFolder<Media> {

    protected static I18n i18n = I18n.get(CrmMediaFolder.class);

    private final ImageTarget imageTarget;

    public CrmMediaFolder(boolean modifyable, ImageTarget imageTarget) {
        super(Media.class, "", modifyable);
        this.imageTarget = imageTarget;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Media) {
            return new CrmMediaEditor();
        } else {
            return super.create(member);
        }
    }

    class CrmMediaEditor extends CEntityDecoratableEditor<Media> {

        private Image thumbnail;

        public CrmMediaEditor() {
            super(Media.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 15, 10).build());

            if (CrmMediaFolder.this.isEditable()) {
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().youTubeVideoID()), 25, 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().url()), 25, 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().file(), new FileUploadHyperlink(CrmMediaFolder.this.isEditable(), imageTarget)),
                        25, 10).build());
            } else {
                Command showMediaCommand = new Command() {
                    @Override
                    public void execute() {
                        showMedia();
                    }
                };
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().youTubeVideoID(), new CHyperlink(showMediaCommand)), 25, 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().url(), new CHyperlink(showMediaCommand)), 25, 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().file(), new CEntityHyperlink(showMediaCommand)), 25, 10).build());
            }

            row = -1;
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().caption()), 15, 10).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().visibility()), 7, 10).build());
            main.getFlexCellFormatter().setRowSpan(row, 1, 3);

            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");

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

                get(proto().youTubeVideoID()).addValueValidator(new YouTubeVideoIdValidator());

                // Validate media completeness:
                addValueValidator(new MediaItemValidator());
            }
        }

        @Override
        public void populate(Media value) {
            super.populate(value);
            setVisibility(value.type().getValue());
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
                    if (!getValue().file().blobKey().isNull()) {
                        thumbnail.setVisible(true);
                        thumbnail.setUrl(ClentNavigUtils.getDeploymentBaseURL() + DeploymentConsts.mediaImagesServletMapping
                                + getValue().getPrimaryKey().toString() + "/" + ThumbnailSize.small.name() + "." + ImageConsts.THUMBNAIL_TYPE);
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
