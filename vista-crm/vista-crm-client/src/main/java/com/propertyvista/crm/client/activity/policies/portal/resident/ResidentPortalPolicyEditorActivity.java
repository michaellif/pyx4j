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
package com.propertyvista.crm.client.activity.policies.portal.resident;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.portal.resident.ResidentPortalPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.ResidentPortalPolicyCrudService;
import com.propertyvista.domain.policy.dto.ResidentPortalPolicyDTO;

public class ResidentPortalPolicyEditorActivity extends PolicyEditorActivityBase<ResidentPortalPolicyDTO> {

    public ResidentPortalPolicyEditorActivity(CrudAppPlace place) {
        super(ResidentPortalPolicyDTO.class, place, CrmSite.getViewFactory().getView(ResidentPortalPolicyEditorView.class), GWT
                .<ResidentPortalPolicyCrudService> create(ResidentPortalPolicyCrudService.class));
    }
}
