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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LeaseApplicationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm;

public class LeaseApplicationPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseApplicationPolicyDTO> {

    private static final I18n i18n = I18n.get(LeaseApplicationPolicyForm.class);

    public LeaseApplicationPolicyForm(IForm<LeaseApplicationPolicyDTO> view) {
        super(LeaseApplicationPolicyDTO.class, view);

        FormPanel legalTermsFormPanel = new FormPanel(this);

        {
            legalTermsFormPanel.h1(proto().legalTerms().getMeta().getCaption());
            legalTermsFormPanel.append(Location.Left, proto().legalTerms(), new LegalTermFolder(isEditable()));
        }
        addTab(legalTermsFormPanel, i18n.tr("Legal Step"));

        FormPanel confirmationTermsFormPanel = new FormPanel(this);

        {
            confirmationTermsFormPanel.h1(proto().confirmationTerms().getMeta().getCaption());
            confirmationTermsFormPanel.append(Location.Left, proto().confirmationTerms(), new ConfirmationTermFolder(isEditable()));
        }
        addTab(confirmationTermsFormPanel, i18n.tr("Confirmation Step"));

    }

    private static class LegalTermFolder extends VistaBoxFolder<LeaseApplicationLegalTerm> {

        public LegalTermFolder(boolean isEditable) {
            super(LeaseApplicationLegalTerm.class, isEditable);
        }

        @Override
        protected CForm<LeaseApplicationLegalTerm> createItemForm(IObject<?> member) {
            return new LegalTermEditor();
        }

        class LegalTermEditor extends CForm<LeaseApplicationLegalTerm> {

            public LegalTermEditor() {
                super(LeaseApplicationLegalTerm.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().applyToRole()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().title()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().body()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().signatureFormat()).decorate().componentWidth(200);
                return formPanel;
            }
        }
    }

    private static class ConfirmationTermFolder extends VistaBoxFolder<LeaseApplicationConfirmationTerm> {

        public ConfirmationTermFolder(boolean isEditable) {
            super(LeaseApplicationConfirmationTerm.class, isEditable);
        }

        @Override
        protected CForm<LeaseApplicationConfirmationTerm> createItemForm(IObject<?> member) {
            return new ConfirmationTermEditor();
        }

        class ConfirmationTermEditor extends CForm<LeaseApplicationConfirmationTerm> {

            public ConfirmationTermEditor() {
                super(LeaseApplicationConfirmationTerm.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().applyToRole()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().title()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().body()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().signatureFormat()).decorate().componentWidth(200);
                return formPanel;
            }
        }
    }
}
