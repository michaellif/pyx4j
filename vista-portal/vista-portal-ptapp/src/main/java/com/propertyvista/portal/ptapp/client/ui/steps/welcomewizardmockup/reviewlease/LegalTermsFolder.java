/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 8, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.reviewlease;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.AgreeFolder;

public class LegalTermsFolder extends VistaBoxFolder<LegalTermsDescriptorDTO> {

    private static final I18n i18n = I18n.get(LegalTermsFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "LeaseTemsFolder";

    public static enum StyleSuffix implements IStyleName {
        Scroll, Content, Agrees
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final boolean editable;

    public LegalTermsFolder(boolean modifiable) {
        super(LegalTermsDescriptorDTO.class, false);
        this.editable = modifiable;
    }

    @Override
    public IFolderItemDecorator<LegalTermsDescriptorDTO> createItemDecorator() {
        BoxFolderItemDecorator<LegalTermsDescriptorDTO> decor = (BoxFolderItemDecorator<LegalTermsDescriptorDTO>) super.createItemDecorator();
        decor.setCollapsible(false);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LegalTermsDescriptorDTO) {
            return new LegalTermsEditor();
        }
        return super.create(member);
    }

    private class LegalTermsEditor extends CEntityDecoratableForm<LegalTermsDescriptorDTO> {

        private FormFlexPanel main;

        public LegalTermsEditor() {
            super(LegalTermsDescriptorDTO.class);
        }

        @Override
        public IsWidget createContent() {
            main = new FormFlexPanel();

            int row = -1;
            ScrollPanel scroll = new ScrollPanel(inject(proto().content().content(), new CLabel<String>()).asWidget());
            main.setH2(++row, 0, 1, "");
            main.setWidget(++row, 0, scroll);
            main.setBR(++row, 0, 1);
            main.setWidget(++row, 0, inject(proto().agrees(), new AgreeFolder(editable)));

            // styling:
            get(proto().content().content()).asWidget().setWidth("auto");
            get(proto().content().content()).asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content.name());
            get(proto().agrees()).asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Agrees.name());
            scroll.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Scroll.name());

            main.setStyleName(DEFAULT_STYLE_PREFIX);
            main.setWidth("100%");

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            main.setH2(0, 0, 1, getValue().content().localizedCaption().getValue());
        }
    }
}
