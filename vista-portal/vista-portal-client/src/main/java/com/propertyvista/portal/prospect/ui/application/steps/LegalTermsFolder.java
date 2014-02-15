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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CHtml;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;

import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class LegalTermsFolder extends PortalBoxFolder<SignedOnlineApplicationLegalTerm> {

    public LegalTermsFolder() {
        super(SignedOnlineApplicationLegalTerm.class);
        setOrderable(false);
        setAddable(false);
        setRemovable(false);

    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof SignedOnlineApplicationLegalTerm) {
            return new LegalTermForm();
        } else {
            return super.create(member);
        }
    }

    class LegalTermForm extends CEntityForm<SignedOnlineApplicationLegalTerm> {

        public LegalTermForm() {
            super(SignedOnlineApplicationLegalTerm.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            CLabel<String> caption = new CLabel<String>();
            caption.asWidget().addStyleName(FlexFormPanelTheme.StyleName.FormFlexPanelH1Label.name());
            mainPanel.setWidget(++row, 0, inject(proto().term().title(), caption));
            mainPanel.setWidget(++row, 0, inject(proto().term().body(), new CHtml()));

            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().signature())).customLabel("").labelPosition(LabelPosition.hidden)
                    .contentWidth("250px").componentWidth("250px").build());

            return mainPanel;
        }

        @Override
        public void generateMockData() {
            CustomerSignature signature = get(proto().signature()).getValue().duplicate();
            signature.agree().setValue(true);
            get(proto().signature()).setMockValue(signature);
        }
    }
}
