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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LeaseTermsPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseTermsPolicyDTO> {

    public LeaseTermsPolicyForm() {
        this(false);
    }

    public LeaseTermsPolicyForm(boolean viewMode) {
        super(LeaseTermsPolicyDTO.class, viewMode);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
            new TabDescriptor(createTenantSummaryTermsPanel(), proto().tenantSummaryTerms().getMeta().getCaption()),
            new TabDescriptor(createGuarantorSummaryTermsPanel(), proto().guarantorSummaryTerms().getMeta().getCaption()),
            new TabDescriptor(createOneTimePaymentTermsPanel(), proto().oneTimePaymentTerms().getMeta().getCaption()),
            new TabDescriptor(createRecurrentPaymentTermsPanel(), proto().recurrentPaymentTerms().getMeta().getCaption())
        );//@formatter:on
    }

    private Widget createTenantSummaryTermsPanel() {
        FormFlexPanel container = new FormFlexPanel();
        container.setWidget(0, 0, inject(proto().tenantSummaryTerms(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private Widget createGuarantorSummaryTermsPanel() {
        FormFlexPanel container = new FormFlexPanel();
        container.setWidget(0, 0, inject(proto().guarantorSummaryTerms(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private Widget createOneTimePaymentTermsPanel() {
        FormFlexPanel container = new FormFlexPanel();
        container.setWidget(0, 0, inject(proto().oneTimePaymentTerms(), new LegalTermsForm(isEditable())));
        return container;
    }

    private Widget createRecurrentPaymentTermsPanel() {
        FormFlexPanel container = new FormFlexPanel();
        container.setWidget(0, 0, inject(proto().recurrentPaymentTerms(), new LegalTermsForm(isEditable())));
        return container;
    }

    private static class LegalTermsDescriptorFolder extends VistaBoxFolder<LegalTermsDescriptor> {

        public LegalTermsDescriptorFolder(boolean isEditable) {
            super(LegalTermsDescriptor.class, isEditable);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if ((member instanceof LegalTermsDescriptor)) {
                return new LegalTermsForm(isEditable());
            }
            return super.create(member);
        }
    }
}
