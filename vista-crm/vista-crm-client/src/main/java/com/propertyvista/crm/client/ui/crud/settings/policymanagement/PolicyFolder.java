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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.AllowedIDsPolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.GymUsageFeePolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.NumberOfIDsPolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.PoolUsageFeePolicyEditorForm;
import com.propertyvista.domain.policy.EffectivePolicyDTO;
import com.propertyvista.domain.policy.PolicyPresetAtNode;
import com.propertyvista.domain.policy.policies.AllowedIDs;
import com.propertyvista.domain.policy.policies.GymUsageFeePolicy;
import com.propertyvista.domain.policy.policies.NumberOfIDs;
import com.propertyvista.domain.policy.policies.PoolUsageFeePolicy;

public class PolicyFolder extends VistaBoxFolder<EffectivePolicyDTO> {

    private static final I18n i18n = I18n.get(PolicyFolder.class);

    public PolicyFolder() {
        super(EffectivePolicyDTO.class, false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if ((member instanceof EffectivePolicyDTO)) {
            return new PolicyEditorFormContainer();
        }
        return super.create(member);
    }

    @SuppressWarnings("rawtypes")
    private static class PolicyEditorFormContainer extends CEntityDecoratableEditor<EffectivePolicyDTO> {
        private FormFlexPanel contentPanel;

        private SimplePanel policyEditorPanel;

        public PolicyEditorFormContainer() {
            super(EffectivePolicyDTO.class);
        }

        @Override
        public IsWidget createContent() {
            contentPanel = new FormFlexPanel();
            contentPanel.setSize("100%", "100%");

            VerticalPanel controlsPanel = new VerticalPanel();
            controlsPanel.setSize("15em", "100%");
            controlsPanel.add(new DecoratorBuilder(inject(proto().inheritedFrom().nodeType())).customLabel(i18n.tr("Inherited From")).componentWidth(5)
                    .labelWidth(7).build());
            contentPanel.setWidget(1, 0, controlsPanel);
            contentPanel.getFlexCellFormatter().setWidth(0, 0, "100");

            policyEditorPanel = new SimplePanel();
            policyEditorPanel.setSize("100%", "100%");
            contentPanel.setWidget(1, 1, policyEditorPanel);

            return contentPanel;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPopulate() {
            // FIXME review this method when polymorphic entities can be somehow 'injected' into form
            super.onPopulate();

            Class<?> policyClass = getValue().policy().getInstanceValueClass();
            CEntityEditor policyEditor = null;
            //@formatter:off
            do {
                if (NumberOfIDs.class.equals(policyClass)) { policyEditor = new NumberOfIDsPolicyEditorForm();  break; }                
                if (AllowedIDs.class.equals(policyClass)) { policyEditor = new AllowedIDsPolicyEditorForm(); break; }
                if (GymUsageFeePolicy.class.equals(policyClass)) { policyEditor = new GymUsageFeePolicyEditorForm(); break; }
                if (PoolUsageFeePolicy.class.equals(policyClass)) { policyEditor = new PoolUsageFeePolicyEditorForm(); break; }
            } while (false);
            //@formatter:on
            if (policyEditor == null) {
                throw new Error("No editor for policy '" + policyClass.getName() + "' was found");
            }

            adopt(policyEditor);
            policyEditor.initContent();
            policyEditor.populate(getValue().policy().cast());
            contentPanel.setH1(0, 0, 2, getValue().policy().cast().getEntityMeta().getCaption());
            policyEditorPanel.clear();
            policyEditorPanel.setWidget(policyEditor);

            PolicyPresetAtNode inheritedFrom = getValue().inheritedFrom();
            if (inheritedFrom.isNull()) {
                get(proto().inheritedFrom().nodeType()).setVisible(false);
            } else {
                get(proto().inheritedFrom().nodeType()).setVisible(true);
            }
        }

    }
}
