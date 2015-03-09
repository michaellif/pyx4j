/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2014
 * @author stanp
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.activity.policies.eviction;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.crud.policies.eviction.EvictionFlowPolicyEditorView;
import com.propertyvista.crm.rpc.services.policies.policy.EvictionFlowPolicyCrudService;
import com.propertyvista.domain.policy.dto.EvictionFlowPolicyDTO;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;

public class EvictionFlowPolicyEditorActivity extends PolicyEditorActivityBase<EvictionFlowPolicyDTO> implements
        EvictionFlowPolicyEditorView.IPrimeEditorPresenter {

    public EvictionFlowPolicyEditorActivity(CrudAppPlace place) {
        super(EvictionFlowPolicyDTO.class, place, CrmSite.getViewFactory().getView(EvictionFlowPolicyEditorView.class), GWT
                .<EvictionFlowPolicyCrudService> create(EvictionFlowPolicyCrudService.class));
    }

    @Override
    protected void onApplySuccess(Key result) {
        super.onApplySuccess(result);
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent(EvictionFlowPolicy.class));
    }

    @Override
    protected void onSaveSuccess(Key result) {
        super.onSaveSuccess(result);
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent(EvictionFlowPolicy.class));
    }
}
