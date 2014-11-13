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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LeaseAgreementLegalPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseAgreementLegalTerm;

public class LeaseAgreementLegalPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseAgreementLegalPolicyDTO> {

    private static final I18n i18n = I18n.get(LeaseAgreementLegalPolicyForm.class);

    public LeaseAgreementLegalPolicyForm(IFormView<LeaseAgreementLegalPolicyDTO, ?> view) {
        super(LeaseAgreementLegalPolicyDTO.class, view);

        addTab(createTermsPanel(), i18n.tr("Agreement Step"));
        addTab(createConfirmationPanel(), i18n.tr("Confirmation Step"));

    }

    private IsWidget createTermsPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().legal().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().legal(), new LegalTermFolder(isEditable()));

        return formPanel;
    }

    private IsWidget createConfirmationPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().confirmation().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().confirmation(), new ConfirmationTermFolder(isEditable())));

        return formPanel;
    }

    private static class LegalTermFolder extends VistaBoxFolder<LeaseAgreementLegalTerm> {

        public LegalTermFolder(boolean isEditable) {
            super(LeaseAgreementLegalTerm.class, isEditable);
        }

        @Override
        protected CForm<LeaseAgreementLegalTerm> createItemForm(IObject<?> member) {
            return new LegalTermEditor();
        }

        class LegalTermEditor extends CForm<LeaseAgreementLegalTerm> {

            public LegalTermEditor() {
                super(LeaseAgreementLegalTerm.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().title()).decorate().componentWidth(200);
                formPanel.append(Location.Dual, proto().content()).decorate();
                formPanel.append(Location.Left, proto().signatureFormat()).decorate().componentWidth(200);
                return formPanel;
            }
        }
    }

    private static class ConfirmationTermFolder extends VistaBoxFolder<LeaseAgreementConfirmationTerm> {

        public ConfirmationTermFolder(boolean isEditable) {
            super(LeaseAgreementConfirmationTerm.class, isEditable);
        }

        @Override
        protected CForm<LeaseAgreementConfirmationTerm> createItemForm(IObject<?> member) {
            return new ConfirmationTermEditor();
        }

        class ConfirmationTermEditor extends CForm<LeaseAgreementConfirmationTerm> {

            public ConfirmationTermEditor() {
                super(LeaseAgreementConfirmationTerm.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().title()).decorate().componentWidth(200);
                formPanel.append(Location.Dual, proto().content()).decorate();
                formPanel.append(Location.Left, proto().signatureFormat()).decorate().componentWidth(200);
                return formPanel;
            }
        }
    }
}
