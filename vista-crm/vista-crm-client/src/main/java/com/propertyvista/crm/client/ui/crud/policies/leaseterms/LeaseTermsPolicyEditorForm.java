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

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LeaseTermsPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<LeaseTermsPolicyDTO> {

    public LeaseTermsPolicyEditorForm(IEditableComponentFactory factory) {
        super(LeaseTermsPolicyDTO.class, factory);
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(

        new TabDescriptor(inject(proto().summaryTerms(), new LegalTermsDescriptorFolder(isEditable())).asWidget(), proto().summaryTerms().getMeta()
                .getCaption()),

        new TabDescriptor(inject(proto().oneTimePaymentTerms(), new LegalTermsEditorForm(isEditable())).asWidget(), proto().oneTimePaymentTerms().getMeta()
                .getCaption()),

        new TabDescriptor(inject(proto().recurrentPaymentTerms(), new LegalTermsEditorForm(isEditable())).asWidget(), proto().recurrentPaymentTerms().getMeta()
                .getCaption())

        );
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
