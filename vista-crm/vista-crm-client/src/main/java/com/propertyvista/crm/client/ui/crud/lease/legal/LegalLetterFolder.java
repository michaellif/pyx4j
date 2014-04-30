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
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

public class LegalLetterFolder extends VistaBoxFolder<LegalLetter> {

    public static class N4LegalLetterForm extends CForm<N4LegalLetter> {

        public N4LegalLetterForm() {
            super(N4LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);

            formPanel.append(Location.Dual, proto().file(), new CFile(null, new VistaFileURLBuilder(N4LegalLetter.class))).decorate();
            formPanel.append(Location.Dual, proto().generatedOn()).decorate();
            formPanel.append(Location.Dual, proto().amountOwed()).decorate();
            formPanel.append(Location.Dual, proto().terminationDate()).decorate();
            return formPanel;
        }
    }

    private static class LegalLetterForm extends CForm<LegalLetter> {

        public LegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);
            formPanel.append(Location.Dual, proto().file(), new CFile(null, new VistaFileURLBuilder(LegalLetter.class))).decorate();
            formPanel.append(Location.Dual, proto().generatedOn()).decorate();
            formPanel.append(Location.Dual, proto().notes()).decorate();

            return formPanel;
        }
    }

    public LegalLetterFolder() {
        super(LegalLetter.class);
        setAddable(false);
        setViewable(true);
    }

    @Override
    protected CForm<LegalLetter> createItemForm(IObject<?> member) {
        return new PolyLegalLetterForm();
    }

    private static class PolyLegalLetterForm extends CForm<LegalLetter> {

        private SimplePanel polymorphicFormContainer;

        public PolyLegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);
            formPanel.append(Location.Dual, polymorphicFormContainer = new SimplePanel());
            return formPanel;
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
