/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author vlads
 */
package com.propertyvista.crm.client.ui.crud.policies.portal.resident;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.policy.dto.ResidentPortalPolicyDTO;

public class ResidentPortalPolicyViewerViewImpl extends CrmViewerViewImplBase<ResidentPortalPolicyDTO> implements ResidentPortalPolicyViewerView {

    public ResidentPortalPolicyViewerViewImpl() {
        setForm(new ResidentPortalPolicyForm(this));
    }
}
