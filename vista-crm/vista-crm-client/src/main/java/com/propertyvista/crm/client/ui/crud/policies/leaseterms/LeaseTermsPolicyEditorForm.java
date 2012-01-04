/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LeaseTermsPolicyEditorForm extends CEntityDecoratableEditor<LegalTermsPolicy> {

    private static final I18n i18n = I18n.get(LeaseTermsPolicyEditorForm.class);

    public LeaseTermsPolicyEditorForm() {
        super(LegalTermsPolicy.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setH1(++row, 0, 1, proto().summaryTerms().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().summaryTerms(), new LegalTermsDescriptorFolder(isEditable())));

        content.setH1(++row, 0, 1, proto().oneTimePaymentTerms().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().oneTimePaymentTerms(), new LegalTermsEditorForm(isEditable())));

        content.setH1(++row, 0, 1, proto().recurrentPaymentTerms().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().recurrentPaymentTerms(), new LegalTermsEditorForm(isEditable())));

        return content;

        // TODO for some reason the tab panel doesn't show the content
//        VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);
//
//        tabPanel.add(inject(proto().summaryTerms(), new LegalTermsDescriptorFolder(isEditable())), proto().summaryTerms().getMeta().getCaption());
//        tabPanel.add(inject(proto().paymentTerms1(), new LegalTermsEditorForm(isEditable())), proto().paymentTerms1().getMeta().getCaption());
//        tabPanel.add(inject(proto().paymentTerms2(), new LegalTermsEditorForm(isEditable())), proto().paymentTerms2().getMeta().getCaption());
//        tabPanel.setSize("100%", "100%");
//        tabPanel.setDisableMode(isEditable());
//        tabPanel.selectTab(0);
//        return tabPanel;

    }

    private static class LegalTermsDescriptorFolder extends VistaBoxFolder<LegalTermsDescriptor> {

        public LegalTermsDescriptorFolder(boolean isEditable) {
            super(LegalTermsDescriptor.class, isEditable);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if ((member instanceof LegalTermsDescriptor)) {
                return new LegalTermsEditorForm(isEditable());
            }
            return super.create(member);
        }
    }
}
