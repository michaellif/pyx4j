/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2012-12-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.view.form;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CImageList;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.images.Images;
import com.pyx4j.tester.client.ui.TesterWidgetDecorator;
import com.pyx4j.tester.domain.TFile;
import com.pyx4j.tester.shared.file.TFileUploadService;

public class EntityIImageForm extends CEntityForm<EntityI> {

    private static final I18n i18n = I18n.get(EntityIImageForm.class);

    public EntityIImageForm() {
        super(EntityI.class);
    }

    @Override
    public IsWidget createContent() {

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH2(++row, 0, 1, i18n.tr("CImage is here"));

//        CImage<TFile> cImage = new CImage<TFile>(CImage.Type.multiple);
//        cImage.setImageFileUrlBuilder(new ImageFileURLBuilder(false));
//        cImage.setThumbnailFileUrlBuilder(new ImageFileURLBuilder(true));
//        cImage.setUploadService(GWT.<TFileUploadService> create(TFileUploadService.class));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().files(), cGallery)));

        CImageList<TFile> cGallery = new CImageList<TFile>(TFile.class) {
            @Override
            public EntityFolderImages getFolderIcons() {
                return Images.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<TFile> entryForm) {
                TwoColumnFlexFormPanel infoPanel = new TwoColumnFlexFormPanel();
                int row = -1;
                infoPanel.setWidget(++row, 0, new TesterWidgetDecorator(entryForm.inject(entryForm.proto().fileName(), new CLabel<String>())));
                infoPanel.setWidget(++row, 0, new TesterWidgetDecorator(entryForm.inject(entryForm.proto().caption())));
                infoPanel.setWidget(++row, 0, new TesterWidgetDecorator(entryForm.inject(entryForm.proto().description())));
                return infoPanel;
            }
        };

        cGallery.setImageFileUrlBuilder(new ImageFileURLBuilder(false));
        cGallery.setThumbnailFileUrlBuilder(new ImageFileURLBuilder(true));
        cGallery.setUploadService(GWT.<TFileUploadService> create(TFileUploadService.class));

        main.setWidget(++row, 0, inject(proto().files(), cGallery));

        return main;
    }
}
