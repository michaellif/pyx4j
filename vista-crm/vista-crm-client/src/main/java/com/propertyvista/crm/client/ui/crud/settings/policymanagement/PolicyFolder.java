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
package com.propertyvista.crm.client.ui.crud.settings.policymanagement;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.AllowedIDsPolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.NumberOfIDsPolicyEditorForm;
import com.propertyvista.domain.policy.EffectivePolicyDTO;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.policies.AllowedIDs;
import com.propertyvista.domain.policy.policies.NumberOfIDs;

public class PolicyFolder extends VistaBoxFolder<EffectivePolicyDTO> {

    public PolicyFolder() {
        super(EffectivePolicyDTO.class, false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof EffectivePolicyDTO) {
            Class<?> policyClass = ((EffectivePolicyDTO) member).policy().getInstanceValueClass();
            CEntityEditor<? extends Policy> policyEditor = null;

            //@formatter:off
            // the most horrible part of this component starts HERE (it's bettter to colse your eyes!)
            do {
                if (NumberOfIDs.class.equals(policyClass)) { policyEditor = new NumberOfIDsPolicyEditorForm();  break; }                
                if (AllowedIDs.class.equals(policyClass)) { policyEditor = new AllowedIDsPolicyEditorForm(); break; }                
            } while (false);
            //@formatter:on
            if (policyEditor == null) {
                throw new Error("No editor for policy '" + policyClass.getName() + "' was found");
            }

            return new PolicyEditorFormContainer(policyEditor);
        }
        return super.create(member);
    }

    @SuppressWarnings("rawtypes")
    private static class PolicyEditorFormContainer extends CEntityDecoratableEditor<EffectivePolicyDTO> {

        private final CEntityEditor policyEditor;

        public PolicyEditorFormContainer(CEntityEditor policyEditor) {
            super(EffectivePolicyDTO.class);
            this.policyEditor = policyEditor;
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setWidget(++row, 0, policyEditor);

            return content;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPopulate() {
            // FIXME review this method when polymorphic entities can be somehow 'injected' into form
            super.onPopulate();

            policyEditor.populate(getValue().policy().cast());
            this.adopt(policyEditor);
        }
    }
}
