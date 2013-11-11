/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.restrictions;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.policies.restrictions.RestrictionsPolicyViewerView;
import com.propertyvista.crm.rpc.services.policies.policy.RestrictionsPolicyCrudService;
import com.propertyvista.domain.policy.dto.RestrictionsPolicyDTO;

public class RestrictionsPolicyViewerActivity extends CrmViewerActivity<RestrictionsPolicyDTO> {

    public RestrictionsPolicyViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(RestrictionsPolicyViewerView.class), GWT
                .<RestrictionsPolicyCrudService> create(RestrictionsPolicyCrudService.class));
    }

}
