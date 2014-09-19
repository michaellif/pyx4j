/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CHtml;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.portal.shared.ui.OriginalSignatureMock;
import com.propertyvista.portal.shared.ui.OriginalSignatureValidator;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class LegalTermsFolder extends PortalBoxFolder<SignedOnlineApplicationLegalTerm> {

    public LegalTermsFolder() {
        this(true);

        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    public LegalTermsFolder(boolean modifiable) {
        super(SignedOnlineApplicationLegalTerm.class, modifiable);
    }

    @Override
    protected CForm<SignedOnlineApplicationLegalTerm> createItemForm(IObject<?> member) {
        return new LegalTermForm();
    }

    class LegalTermForm extends CForm<SignedOnlineApplicationLegalTerm> {

        public LegalTermForm() {
            super(SignedOnlineApplicationLegalTerm.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            CLabel<String> caption = new CLabel<String>();
            caption.asWidget().addStyleName(FlexFormPanelTheme.StyleName.FormFlexPanelH1Label.name());
            formPanel.append(Location.Left, proto().term().title(), caption);
            formPanel.append(Location.Left, proto().term().body(), new CHtml<String>());

            formPanel.append(Location.Left, proto().signature()).decorate().customLabel("").labelPosition(LabelPosition.hidden).componentWidth(250);

            return formPanel;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().signature()).addComponentValidator(new OriginalSignatureValidator());
        }

        @Override
        public void generateMockData() {
            CustomerSignature signature = get(proto().signature()).getValue().duplicate();
            OriginalSignatureMock.generateMockData(signature);
            get(proto().signature()).setMockValue(signature);
        }
    }
}
