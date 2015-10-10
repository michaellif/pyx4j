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
 */
package com.pyx4j.tester.client.view.form;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormFieldDecoratorOptions;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.images.Images;
import com.pyx4j.tester.domain.TFile;
import com.pyx4j.tester.shared.file.TFileUploadService;

public class EntityIImageForm extends CForm<EntityI> {

    private static final I18n i18n = I18n.get(EntityIImageForm.class);

    public EntityIImageForm() {
        super(EntityI.class);
    }

    @Override
    protected IsWidget createContent() {

        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("CImage is here"));

        CImageSlider<TFile> cGallery = new CImageSlider<TFile>(TFile.class, GWT.<TFileUploadService> create(TFileUploadService.class),
                new ImageFileURLBuilder(false)) {
            @Override
            public FolderImages getFolderIcons() {
                return Images.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CForm<TFile> entryForm) {
                VerticalPanel infoPanel = new VerticalPanel();
                infoPanel.add(entryForm.inject(entryForm.proto().file().fileName(), new CLabel<String>(), new FormFieldDecoratorOptions().build()));
                infoPanel.add(entryForm.inject(entryForm.proto().caption(), new FormFieldDecoratorOptions().build()));
                infoPanel.add(entryForm.inject(entryForm.proto().description(), new FormFieldDecoratorOptions().build()));
                return infoPanel;
            }
        };

        formPanel.append(Location.Dual, proto().files(), cGallery);

        return formPanel;
    }
}
