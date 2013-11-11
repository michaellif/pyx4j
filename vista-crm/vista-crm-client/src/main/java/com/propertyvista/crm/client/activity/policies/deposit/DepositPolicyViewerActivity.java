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
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.deposit;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.deposit.DepositPolicyViewerView;
import com.propertyvista.crm.rpc.services.policies.policy.DepositPolicyCrudService;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;

public class DepositPolicyViewerActivity extends CrmViewerActivity<DepositPolicyDTO> {

    public DepositPolicyViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(DepositPolicyViewerView.class), GWT.<DepositPolicyCrudService> create(DepositPolicyCrudService.class));
    }
}
