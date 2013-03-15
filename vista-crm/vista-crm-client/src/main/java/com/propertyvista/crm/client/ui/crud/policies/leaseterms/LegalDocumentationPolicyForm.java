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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LegalDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LegalDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<LegalDocumentationPolicyDTO> {

    public LegalDocumentationPolicyForm(IForm<LegalDocumentationPolicyDTO> view) {
        super(LegalDocumentationPolicyDTO.class, view);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
            createMainApplicatinoTermsPanel(),
            createCoApplicatinoTermsPanel(),
            createGuarantorApplicationPanel(),
            createLeaseTermsPanel(),
            createPaymentAuthorizationPanel() 
        );//@formatter:on
    }

    private FormFlexPanel createMainApplicatinoTermsPanel() {
        FormFlexPanel container = new FormFlexPanel(proto().mainApplication().getMeta().getCaption());
        container.setWidget(0, 0, inject(proto().mainApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private FormFlexPanel createCoApplicatinoTermsPanel() {
        FormFlexPanel container = new FormFlexPanel(proto().coApplication().getMeta().getCaption());
        container.setWidget(0, 0, inject(proto().coApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private FormFlexPanel createGuarantorApplicationPanel() {
        FormFlexPanel container = new FormFlexPanel(proto().guarantorApplication().getMeta().getCaption());
        container.setWidget(0, 0, inject(proto().guarantorApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private FormFlexPanel createLeaseTermsPanel() {
        FormFlexPanel container = new FormFlexPanel(proto().lease().getMeta().getCaption());
        container.setWidget(0, 0, inject(proto().lease(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private FormFlexPanel createPaymentAuthorizationPanel() {
        FormFlexPanel container = new FormFlexPanel(proto().paymentAuthorization().getMeta().getCaption());
        container.setWidget(0, 0, inject(proto().paymentAuthorization(), new LegalTermsDescriptorFolder(isEditable())));
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
