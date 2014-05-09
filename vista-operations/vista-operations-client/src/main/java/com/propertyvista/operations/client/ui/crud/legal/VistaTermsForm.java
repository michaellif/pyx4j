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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;

public class VistaTermsForm extends OperationsEntityForm<VistaTerms> {

    private final static I18n i18n = I18n.get(VistaTermsForm.class);

    public VistaTermsForm(IForm<VistaTerms> view) {
        super(VistaTerms.class, view);

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().version().caption(), new FieldDecoratorBuilder(10, true).build()));
        main.setWidget(++row, 0, 2, inject(proto().version().document(), new VistaTermsDocumentFolder()));

        selectTab(addTab(main, i18n.tr("General")));
        setTabBarVisible(false);
    }

    class VistaTermsDocumentFolder extends VistaBoxFolder<LegalDocument> {

        public VistaTermsDocumentFolder() {
            super(LegalDocument.class);
        }

        @Override
        protected CForm<LegalDocument> createItemForm(IObject<?> member) {
            return new LegalDocumentForm();
        }
    }

    class LegalDocumentForm extends CForm<LegalDocument> {

        public LegalDocumentForm() {
            super(LegalDocument.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);
            // locale
            formPanel.append(Location.Left, proto().locale()).decorate().componentWidth(160);
            // content
            formPanel.append(Location.Dual, proto().content(), new CRichTextArea()).decorate();

            return formPanel;
        }
    }
}
