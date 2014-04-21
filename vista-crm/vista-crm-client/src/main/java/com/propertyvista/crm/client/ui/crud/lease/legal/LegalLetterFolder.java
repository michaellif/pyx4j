/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

public class LegalLetterFolder extends VistaBoxFolder<LegalLetter> {

    private static final I18n i18n = I18n.get(LegalLetterFolder.class);

    public static class N4LegalLetterForm extends CEntityForm<N4LegalLetter> {

        public N4LegalLetterForm() {
            super(N4LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setWidget(++row, 0, 2,
                    inject(proto().file(), new CFile(null, new VistaFileURLBuilder(N4LegalLetter.class)), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().generatedOn(), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().amountOwed(), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().terminationDate(), new FieldDecoratorBuilder().build()));
            return panel;
        }
    }

    private static class LegalLetterForm extends CEntityForm<LegalLetter> {

        public LegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;

            panel.setWidget(++row, 0, 2,
                    inject(proto().file(), new CFile(null, new VistaFileURLBuilder(LegalLetter.class)), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().generatedOn(), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().notes(), new FieldDecoratorBuilder().build()));

            return panel;
        }
    }

    public LegalLetterFolder() {
        super(LegalLetter.class);
        setAddable(false);
        setViewable(true);
    }

    @Override
    protected CEntityForm<LegalLetter> createItemForm(IObject<?> member) {
        return new PolyLegalLetterForm();
    }

    private static class PolyLegalLetterForm extends CEntityForm<LegalLetter> {

        private SimplePanel polymorphicFormContainer;

        public PolyLegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            panel.setWidth("100%");
            int row = -1;
            panel.setWidget(++row, 0, 2, polymorphicFormContainer = new SimplePanel());
            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            if (getValue().getInstanceValueClass().equals(N4LegalLetter.class)) {
                N4LegalLetterForm form = new N4LegalLetterForm();
                form.init();
                form.setEditable(isEditable());
                form.setViewable(isViewable());
                form.populate(getValue().duplicate(N4LegalLetter.class));
                polymorphicFormContainer.setWidget(form);

            } else {
                LegalLetterForm form = new LegalLetterForm();
                form.init();
                form.setEditable(isEditable());
                form.setViewable(isViewable());
                form.populate(getValue().duplicate(LegalLetter.class));
                polymorphicFormContainer.setWidget(form);
            }
        }
    }

}
