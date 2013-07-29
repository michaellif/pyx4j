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
package com.propertyvista.operations.client.ui.crud.legal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;

public class VistaTermsForm extends OperationsEntityForm<VistaTerms> {
    private final static I18n i18n = I18n.get(VistaTermsForm.class);

    public VistaTermsForm(IForm<VistaTerms> view) {
        super(VistaTerms.class, view);

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("General"));

        main.setWidget(0, 0, inject(proto().version().document(), new VistaTermsDocumentFolder()));

        selectTab(addTab(main));
    }

    class VistaTermsDocumentFolder extends VistaBoxFolder<LegalDocument> {

        public VistaTermsDocumentFolder() {
            super(LegalDocument.class);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
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
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("General"));

            int row = -1;
            // locale
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale()), 10).build());
            // content
            CComponent<?> editor = null;
            editor = new CRichTextArea();
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().content(), editor), 60).build());

            return main;
        }

    }
}
