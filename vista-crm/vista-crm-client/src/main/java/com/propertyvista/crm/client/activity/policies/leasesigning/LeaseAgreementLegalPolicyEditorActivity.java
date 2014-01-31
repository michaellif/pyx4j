/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.leasesigning;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.leasesigning.LeaseAgreementLegalPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseAgreementLegalPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseAgreementLegalPolicyDTO;

public class LeaseAgreementLegalPolicyEditorActivity extends PolicyEditorActivityBase<LeaseAgreementLegalPolicyDTO> {

    public LeaseAgreementLegalPolicyEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(LeaseAgreementLegalPolicyEditorView.class), GWT
                .<LeaseAgreementLegalPolicyCrudService> create(LeaseAgreementLegalPolicyCrudService.class), LeaseAgreementLegalPolicyDTO.class);
    }
}
