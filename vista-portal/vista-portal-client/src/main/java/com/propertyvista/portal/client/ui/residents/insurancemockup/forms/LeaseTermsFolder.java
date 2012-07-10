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
package com.propertyvista.portal.client.ui.residents.insurancemockup.forms;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;

public class LeaseTermsFolder extends VistaBoxFolder<LegalTermsDescriptorDTO> {

    private static final I18n i18n = I18n.get(LeaseTermsFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "LeaseTemsFolder";

    public static enum StyleSuffix implements IStyleName {
        Scroll, Content, Agrees
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final boolean editable;

    public LeaseTermsFolder(boolean modifiable) {
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
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LegalTermsDescriptorDTO) {
            return new LeaseTermsEditor();
        }
        return super.create(member);
    }

    private class LeaseTermsEditor extends CEntityDecoratableForm<LegalTermsDescriptorDTO> {

        private FormFlexPanel main;

        public LeaseTermsEditor() {
            super(LegalTermsDescriptorDTO.class);
        }

        @Override
        public IsWidget createContent() {
            main = new FormFlexPanel();

            int row = -1;
            CLabel content = new CLabel();
            content.setAllowHtml(true);
            ScrollPanel scroll = new ScrollPanel(inject(proto().content().content(), content).asWidget());
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
        protected void onSetValue(boolean populate) {
            super.onSetValue(populate);
            if (isValueEmpty()) {
                return;
            }

            main.setH2(0, 0, 1, getValue().content().localizedCaption().getValue());
        }
    }
}
