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

import java.util.List;

import com.google.gwt.dom.client.Style;
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
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdValidator;
import com.propertyvista.crm.client.ui.components.cms.FileUploadHyperlink;
import com.propertyvista.domain.media.Media;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class CrmMediaListFolderEditor extends VistaTableFolder<Media> {

    protected static I18n i18n = I18n.get(CrmMediaListFolderEditor.class);

    private final ImageTarget imageTarget;

    private final VistaTableFolder<Media> parent = this;

    public CrmMediaListFolderEditor(boolean editable, ImageTarget imageTarget) {
        super(Media.class, "", editable);
        this.imageTarget = imageTarget;
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        return null;
    }

    @Override
    protected IFolderDecorator<Media> createDecorator() {
        return new VistaBoxFolderDecorator<Media>(this);
    }

    @Override
    protected CEntityFolderItem<Media> createItem(boolean first) {
        CEntityFolderItem<Media> item = new CEntityFolderItem<Media>(Media.class) {

            Image thumbnail;

            @Override
            public IFolderItemDecorator<Media> createDecorator() {
                return new VistaBoxFolderItemDecorator<Media>(parent);
            }

            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!parent.isEditable());
                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!parent.isEditable(), 10, 35);
                main.add(split);

                split.getLeftPanel().add(inject(proto().type()), 15);

                if (parent.isEditable()) {
                    split.getLeftPanel().add(inject(proto().youTubeVideoID()), 25);
                    split.getLeftPanel().add(inject(proto().url()), 25);
                    split.getLeftPanel().add(inject(proto().file(), new FileUploadHyperlink(parent.isEditable(), imageTarget)), 25);
                } else {
                    Command showMediaCommand = new Command() {
                        @Override
                        public void execute() {
                            showMedia();
                        }
                    };
                    split.getLeftPanel().add(inject(proto().youTubeVideoID(), new CHyperlink(showMediaCommand)), 25);
                    split.getLeftPanel().add(inject(proto().url(), new CHyperlink(showMediaCommand)), 25);
                    split.getLeftPanel().add(inject(proto().file(), new CEntityHyperlink(showMediaCommand)), 25);
                }

                split.getRightPanel().add(inject(proto().caption()), 15);
                split.getRightPanel().add(inject(proto().visibility()), 6);

                HorizontalPanel wrap = new HorizontalPanel();
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
                thumbnail.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        showMedia();
                    }
                });

                wrap.add(main);
                wrap.add(thumbnailWrap);
                return wrap;
            }

            @Override
            public void addValidations() {
                if (parent.isEditable()) {
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
                                    + getValue().getPrimaryKey().toString() + "/" + ThumbnailSize.small.name() + ".jpg");
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
                        MessageDialog.error(i18n.tr("Upload error"), i18n.tr("Please save this first"));
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
        };

        item.setRemovable(parent.isEditable());
        return item;
    }
}
