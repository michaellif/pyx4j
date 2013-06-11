/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.autopaychange;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.policies.common.PolicyEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.policies.autopaychangepolicy.AutoPayChangePolicyEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.AutoPayChangePolicyCrudService;
import com.propertyvista.domain.policy.dto.AutoPayChangePolicyDTO;

public class AutoPayChangePolicyEditorActivity extends PolicyEditorActivityBase<AutoPayChangePolicyDTO> {

    public AutoPayChangePolicyEditorActivity(CrudAppPlace place) {
        super(place,

        PolicyViewFactory.instance(AutoPayChangePolicyEditorView.class),

        GWT.<AutoPayChangePolicyCrudService> create(AutoPayChangePolicyCrudService.class),

        AutoPayChangePolicyDTO.class);
    }

}
