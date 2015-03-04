/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2015
 * @author VladL
 */
package com.propertyvista.crm.client.activity.policies.financialterms;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.financialterms.FinancialTermsPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.FinancialTermsPolicyCrudService;
import com.propertyvista.domain.policy.dto.FinancialTermsPolicyDTO;

public class FinancialTermsPolicyEditorActivity extends PolicyEditorActivityBase<FinancialTermsPolicyDTO> implements
        FinancialTermsPolicyEditorView.IPrimeEditorPresenter {

    public FinancialTermsPolicyEditorActivity(CrudAppPlace place) {
        super(FinancialTermsPolicyDTO.class, place, CrmSite.getViewFactory().getView(FinancialTermsPolicyEditorView.class), GWT
                .<AbstractPolicyCrudService<FinancialTermsPolicyDTO>> create(FinancialTermsPolicyCrudService.class));
    }
}
