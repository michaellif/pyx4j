/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.pad;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.pad.PADPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.PADPolicyCrudService;
import com.propertyvista.domain.policy.dto.PADPolicyDTO;

public class PADPolicyViewerActivity extends CrmViewerActivity<PADPolicyDTO> {

    public PADPolicyViewerActivity(CrudAppPlace place) {
        super(place, PolicyViewFactory.instance(PADPolicyViewerView.class), GWT.<PADPolicyCrudService> create(PADPolicyCrudService.class));
    }
}
