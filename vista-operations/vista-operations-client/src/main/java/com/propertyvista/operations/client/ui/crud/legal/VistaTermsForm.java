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
 */
package com.propertyvista.operations.client.ui.crud.legal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;

public class VistaTermsForm extends OperationsEntityForm<VistaTerms> {

    private final static I18n i18n = I18n.get(VistaTermsForm.class);

    public VistaTermsForm(IPrimeFormView<VistaTerms, ?> view) {
        super(VistaTerms.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().version().caption()).decorate();
        formPanel.append(Location.Dual, proto().version().document(), new VistaTermsDocumentFolder());

        selectTab(addTab(formPanel, i18n.tr("General")));
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
            FormPanel formPanel = new FormPanel(this);
            // locale
            formPanel.append(Location.Left, proto().locale()).decorate().componentWidth(160);
            // content
            formPanel.append(Location.Dual, proto().content(), new CRichTextArea()).decorate();

            return formPanel;
        }
    }
}
