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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LegalDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;
import com.propertyvista.shared.config.VistaFeatures;

public class LegalDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<LegalDocumentationPolicyDTO> {

    public LegalDocumentationPolicyForm(IForm<LegalDocumentationPolicyDTO> view) {
        super(LegalDocumentationPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        if (VistaFeatures.instance().yardiIntegration()) {
            return Arrays.asList(//@formatter:off
                        createPaymentAuthorizationPanel() 
            );//@formatter:on
        } else {
            return Arrays.asList(//@formatter:off
                        createMainApplicatinoTermsPanel(),
                        createCoApplicatinoTermsPanel(),
                        createGuarantorApplicationPanel(),
                        createLeaseTermsPanel(),
                        createPaymentAuthorizationPanel() 
            );//@formatter:on
        }
    }

    private TwoColumnFlexFormPanel createMainApplicatinoTermsPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().mainApplication().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().mainApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private TwoColumnFlexFormPanel createCoApplicatinoTermsPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().coApplication().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().coApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private TwoColumnFlexFormPanel createGuarantorApplicationPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().guarantorApplication().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().guarantorApplication(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private TwoColumnFlexFormPanel createLeaseTermsPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().lease().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().lease(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private TwoColumnFlexFormPanel createPaymentAuthorizationPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(proto().paymentAuthorization().getMeta().getCaption());
        container.setWidget(0, 0, 2, inject(proto().paymentAuthorization(), new LegalTermsDescriptorFolder(isEditable())));
        return container;
    }

    private static class LegalTermsDescriptorFolder extends VistaBoxFolder<LegalTermsDescriptor> {

        public LegalTermsDescriptorFolder(boolean isEditable) {
            super(LegalTermsDescriptor.class, isEditable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if ((member instanceof LegalTermsDescriptor)) {
                return new LegalTermsForm(isEditable());
            }
            return super.create(member);
        }
    }
}
