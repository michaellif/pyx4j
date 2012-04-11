/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.misc;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.misc.MiscPolicyEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.crm.rpc.services.policies.policy.MiscPolicyCrudService;
import com.propertyvista.domain.policy.dto.MiscPolicyDTO;

public class MiscPolicyEditorActivity extends PolicyEditorActivityBase<MiscPolicyDTO> {

    public MiscPolicyEditorActivity(CrudAppPlace place) {
        super(place,

        PolicyViewFactory.instance(MiscPolicyEditorView.class),

        GWT.<AbstractPolicyCrudService<MiscPolicyDTO>> create(MiscPolicyCrudService.class),

        MiscPolicyDTO.class);
    }

}
