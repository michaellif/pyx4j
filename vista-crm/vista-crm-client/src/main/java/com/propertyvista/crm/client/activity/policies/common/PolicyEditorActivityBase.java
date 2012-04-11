/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;

public abstract class PolicyEditorActivityBase<POLICY_DTO extends PolicyDTOBase> extends EditorActivityBase<POLICY_DTO> {

    public PolicyEditorActivityBase(CrudAppPlace place, IEditorView<POLICY_DTO> view, AbstractPolicyCrudService<POLICY_DTO> service,
            Class<POLICY_DTO> entityClass) {
        super(place, view, service, entityClass);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<POLICY_DTO> callback) {
        ((AbstractPolicyCrudService<POLICY_DTO>) getService()).createNewPolicy(new DefaultAsyncCallback<POLICY_DTO>() {
            @Override
            public void onSuccess(POLICY_DTO result) {
                callback.onSuccess(result);
            }
        });
    }

}
