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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.flex.editor.BoxFolderItemEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdFormat;
import com.propertyvista.common.client.ui.validators.YouTubeVideoIdValidator;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.domain.media.Media;

class CrmMediaFolderItemEditor extends CEntityFolderItemEditor<Media> {

    protected static I18n i18n = I18nFactory.getI18n(CrmMediaFolderItemEditor.class);

    private final boolean editable;

    private CHyperlink viewLink;

    public CrmMediaFolderItemEditor(boolean editable) {
        super(Media.class);
        this.editable = editable;
    }

    @Override
    public IFolderItemEditorDecorator<Media> createFolderItemDecorator() {
        return new BoxFolderItemEditorDecorator<Media>(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), i18n.tr("Remove Media"), !isFirst() && editable);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!editable);
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().type()), 15);
        split.getLeftPanel().add(inject(proto().youTubeVideoID()), 15);
        split.getLeftPanel().add(inject(proto().url()), 15);
        if (editable) {
            split.getLeftPanel().add(inject(proto().file(), new FileUploadHyperlink(editable)), 15);
        } else {
            split.getLeftPanel().add(inject(proto().file(), new CEntityHyperlink(new Command() {
                @Override
                public void execute() {
                    showMedia();
                }
            })), 15);
        }

        split.getRightPanel().add(inject(proto().caption()), 15);
        //Link to view
        viewLink = new CHyperlink(new Command() {
            @Override
            public void execute() {
                showMedia();
            }
        });
        viewLink.setValue("view this media");
        split.getRightPanel().add(viewLink, 15);

        return main;
    }

    @Override
    public void addValidations() {
        if (editable) {
            @SuppressWarnings("unchecked")
            CComboBox<Media.Type> type = (CComboBox<Media.Type>) get(proto().type());
            type.addValueChangeHandler(new ValueChangeHandler<Media.Type>() {
                @Override
                public void onValueChange(ValueChangeEvent<Media.Type> event) {
                    setVisibility(event.getValue());
                }
            });

            ((CTextFieldBase<String, ?>) get(proto().youTubeVideoID())).setFormat(new YouTubeVideoIdFormat());
            get(proto().youTubeVideoID()).addValueValidator(new YouTubeVideoIdValidator());
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
        viewLink.setVisible(false);
        if (type != null) {
            viewLink.setVisible(!getValue().id().isNull());
            switch (type) {
            case file:
                get(proto().file()).setVisible(true);
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
                MessageDialog.error(i18n.tr("Upload error"), i18n.tr("Plase save this first"));
            } else {
                MediaFileViewDialog dialog = new MediaFileViewDialog();
                dialog.title = getValue().caption().getValue();
                dialog.mediaId = getValue().id().getValue();
                dialog.show();
            }
            break;
        case externalUrl:
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
