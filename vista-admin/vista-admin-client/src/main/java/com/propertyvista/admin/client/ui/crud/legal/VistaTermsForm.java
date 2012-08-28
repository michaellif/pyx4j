/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.legal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.legal.LegalDocument;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class VistaTermsForm extends AdminEntityForm<VistaTerms> {
    private final static I18n i18n = I18n.get(VistaTermsForm.class);

    public VistaTermsForm(boolean viewMode) {
        super(VistaTerms.class, viewMode);
    }

    @Override
    protected void createTabs() {
        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));

        main.setWidget(0, 0, inject(proto().version().document(), new VistaTermsDocumentFolder()));

        selectTab(addTab(main));
    }

    class VistaTermsDocumentFolder extends VistaBoxFolder<LegalDocument> {

        public VistaTermsDocumentFolder() {
            super(LegalDocument.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof LegalDocument) {
                return new LegalDocumentForm();
            } else {
                return super.create(member);
            }
        }
    }

    class LegalDocumentForm extends CEntityDecoratableForm<LegalDocument> {

        public LegalDocumentForm() {
            super(LegalDocument.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));

            int row = -1;
            // locale
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale()), 10).build());
            // content
            CComponent<?, ?> editor = null;
            if (isEditable()) {
                editor = new CRichTextArea();
            } else {
                editor = new CLabel();
                ((CLabel) editor).setAllowHtml(true);
            }
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().content(), editor), 60).build());

            return main;
        }

    }
}
