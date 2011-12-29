/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.policy;

import com.pyx4j.entity.client.CEntityEditor;

import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.AllowedIDsPolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.GymUsageFeePolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.NumberOfIDsPolicyEditorForm;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.policyform.PoolUsageFeePolicyEditorForm;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.policies.AllowedIDsPolicy;
import com.propertyvista.domain.policy.policies.GymUsageFeePolicy;
import com.propertyvista.domain.policy.policies.NumberOfIDsPolicy;
import com.propertyvista.domain.policy.policies.PoolUsageFeePolicy;

public class PolicyFormFactory {

    public static <P extends Policy> CEntityEditor<?> createPolicyEditorForm(Class<P> policy, boolean isEditable) {
        CEntityEditor<?> policyEditorForm = null;
        //@formatter:off
        do {
            if (NumberOfIDsPolicy.class.equals(policy)) { policyEditorForm = new NumberOfIDsPolicyEditorForm();  break; }                
            if (AllowedIDsPolicy.class.equals(policy)) { policyEditorForm = new AllowedIDsPolicyEditorForm(); break; }
            if (GymUsageFeePolicy.class.equals(policy)) { policyEditorForm = new GymUsageFeePolicyEditorForm(); break; }
            if (PoolUsageFeePolicy.class.equals(policy)) { policyEditorForm = new PoolUsageFeePolicyEditorForm(); break; }
        } while (false);
        //@formatter:on
        if (policyEditorForm == null) {
            throw new Error("No editor for policy '" + policy.getName() + "' was found");
        }
        policyEditorForm.setEditable(isEditable);
        return policyEditorForm;
    }
}
