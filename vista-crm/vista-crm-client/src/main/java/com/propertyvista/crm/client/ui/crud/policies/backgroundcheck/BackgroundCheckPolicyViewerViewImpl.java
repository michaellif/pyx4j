/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.policies.backgroundcheck;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyViewerViewImpl extends CrmViewerViewImplBase<BackgroundCheckPolicyDTO> implements BackgroundCheckPolicyViewerView {

    public BackgroundCheckPolicyViewerViewImpl() {
        setForm(new BackgroundCheckPolicyForm(this));
    }
}
