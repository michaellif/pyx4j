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
package com.propertyvista.crm.client.ui.crud.settings.policymanagement;

import com.pyx4j.entity.client.CEntityEditor;

import com.propertyvista.domain.policy.Policy;

public class PolicyEditorFormFactory {

    @SuppressWarnings("unused")
    public static <P extends Policy> CEntityEditor<?> createPolicyEditorForm(Class<P> policy, boolean isEditable) {
        CEntityEditor<?> policyEditorForm = null;
        //@formatter:off
        do {
        } while (false);
        //@formatter:on
        if (policyEditorForm == null) {
            throw new Error("No editor for policy '" + policy.getName() + "' was found");
        }
        policyEditorForm.setEditable(isEditable);
        return policyEditorForm;
    }
}
