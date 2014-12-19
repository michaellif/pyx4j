/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2014
 * @author VladL
 */
package com.propertyvista.crm.client.activity.policies.legalquestions;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.legalquestions.LegalQuestionsPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.legalquestions.LegalQuestionsPolicyViewerViewImpl;
import com.propertyvista.crm.rpc.services.policies.policy.LegalQuestionsPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalQuestionsPolicyDTO;

public class LegalQuestionsPolicyViewerActivity extends CrmViewerActivity<LegalQuestionsPolicyDTO> implements
        LegalQuestionsPolicyViewerView.IPrimeViewerPresenter {

    public LegalQuestionsPolicyViewerActivity(CrudAppPlace place) {
        // TODO take the view from pool
        super(LegalQuestionsPolicyDTO.class, place, new LegalQuestionsPolicyViewerViewImpl(), GWT
                .<AbstractCrudService<LegalQuestionsPolicyDTO>> create(LegalQuestionsPolicyCrudService.class));
    }

}
