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
package com.propertyvista.crm.client.ui.crud.policies.leasesigning;

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
import com.propertyvista.domain.policy.dto.LeaseAgreementLegalPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;

public class LeaseAgreementLegalPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseAgreementLegalPolicyDTO> {

    private static final I18n i18n = I18n.get(LeaseAgreementLegalPolicyForm.class);

    public LeaseAgreementLegalPolicyForm(IForm<LeaseAgreementLegalPolicyDTO> view) {
        super(LeaseAgreementLegalPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createTermsPanel(), createConfirmationPanel());
    }

    private TwoColumnFlexFormPanel createTermsPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(i18n.tr("Agreement Step"));
        int row = -1;

        container.setH1(++row, 0, 2, proto().legal().getMeta().getCaption());
        container.setWidget(++row, 0, 2, inject(proto().legal(), new LegalTermFolder(isEditable())));

        return container;
    }

    private TwoColumnFlexFormPanel createConfirmationPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(i18n.tr("Confirmation Step"));
        int row = -1;

        container.setH1(++row, 0, 2, proto().confirmation().getMeta().getCaption());
        container.setWidget(++row, 0, 2, inject(proto().confirmation(), new ConfirmationTermFolder(isEditable())));

        return container;
    }

    private static class LegalTermFolder extends VistaBoxFolder<LeaseAgreementLegalTerm> {

        public LegalTermFolder(boolean isEditable) {
            super(LeaseAgreementLegalTerm.class, isEditable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if ((member instanceof LeaseAgreementLegalTerm)) {
                return new LegalTermEditor();
            }
            return super.create(member);
        }

        class LegalTermEditor extends CEntityForm<LeaseAgreementLegalTerm> {

            public LegalTermEditor() {
                super(LeaseAgreementLegalTerm.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = -1;

                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().title()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().body()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().signatureFormat()), 35).build());
                return main;
            }
        }
    }

    private static class ConfirmationTermFolder extends VistaBoxFolder<LeaseAgreementConfirmationTerm> {

        public ConfirmationTermFolder(boolean isEditable) {
            super(LeaseAgreementConfirmationTerm.class, isEditable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if ((member instanceof LeaseAgreementConfirmationTerm)) {
                return new ConfirmationTermEditor();
            }
            return super.create(member);
        }

        class ConfirmationTermEditor extends CEntityForm<LeaseAgreementConfirmationTerm> {

            public ConfirmationTermEditor() {
                super(LeaseAgreementConfirmationTerm.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = -1;

                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().title()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().body()), 35).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().signatureFormat()), 35).build());
                return main;
            }
        }
    }
}
