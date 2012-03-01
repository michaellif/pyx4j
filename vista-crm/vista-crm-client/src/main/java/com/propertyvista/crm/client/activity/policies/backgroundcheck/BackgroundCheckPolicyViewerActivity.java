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
package com.propertyvista.crm.client.activity.policies.backgroundcheck;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.backgroundcheck.BackgroundCheckPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.BackgroundCheckPolicyCrudService;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyViewerActivity extends CrmViewerActivity<BackgroundCheckPolicyDTO> {

    public BackgroundCheckPolicyViewerActivity(Place place) {
        super(place, PolicyViewFactory.instance(BackgroundCheckPolicyViewerView.class), GWT
                .<BackgroundCheckPolicyCrudService> create(BackgroundCheckPolicyCrudService.class));
    }
}
