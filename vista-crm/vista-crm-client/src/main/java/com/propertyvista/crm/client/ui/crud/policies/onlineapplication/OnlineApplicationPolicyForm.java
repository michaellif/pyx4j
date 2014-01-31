/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.onlineapplication;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.OnlineApplicationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;

public class OnlineApplicationPolicyForm extends PolicyDTOTabPanelBasedForm<OnlineApplicationPolicyDTO> {

    private static final I18n i18n = I18n.get(OnlineApplicationPolicyForm.class);

    public OnlineApplicationPolicyForm(IForm<OnlineApplicationPolicyDTO> view) {
        super(OnlineApplicationPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        TwoColumnFlexFormPanel legalTermsPanel = new TwoColumnFlexFormPanel(i18n.tr("Legal Step"));

        {
            int row = -1;
            legalTermsPanel.setH1(++row, 0, 2, proto().legalTerms().getMeta().getCaption());
            legalTermsPanel.setWidget(++row, 0, 2, inject(proto().legalTerms(), new LegalTermFolder(isEditable())));
        }

        TwoColumnFlexFormPanel confirmationTermsPanel = new TwoColumnFlexFormPanel(i18n.tr("Confirmation Step"));

        {
            int row = -1;
            confirmationTermsPanel.setH1(++row, 0, 2, proto().confirmationTerms().getMeta().getCaption());
            confirmationTermsPanel.setWidget(++row, 0, 2, inject(proto().confirmationTerms(), new LegalTermFolder(isEditable())));
        }

        return Arrays.asList(legalTermsPanel, confirmationTermsPanel);
    }

    private static class LegalTermFolder extends VistaBoxFolder<OnlineApplicationLegalTerm> {

        public LegalTermFolder(boolean isEditable) {
            super(OnlineApplicationLegalTerm.class, isEditable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if ((member instanceof OnlineApplicationLegalTerm)) {
                return new LegalTermEditor();
            }
            return super.create(member);
        }

        class LegalTermEditor extends CEntityForm<OnlineApplicationLegalTerm> {

            public LegalTermEditor() {
                super(OnlineApplicationLegalTerm.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = -1;

                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().title()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().body()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().signatureFormat()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().applyToRole()), 35).build());
                return main;
            }
        }
    }
}
