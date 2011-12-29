/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 29, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.numberofids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyViewerView;
import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyViewerViewImpl;
import com.propertyvista.crm.rpc.services.policy.NumberOfIDsPolicyCrudService;
import com.propertyvista.domain.policy.dto.NumberOfIDsPolicyDTO;

public class NumberOfIDsViewerActivity extends ViewerActivityBase<NumberOfIDsPolicyDTO> implements NumberOfIDsPolicyViewerView.Presenter {

    public NumberOfIDsViewerActivity(Place place) {
        // TODO take the view from pool
        super(place, new NumberOfIDsPolicyViewerViewImpl(), (AbstractCrudService<NumberOfIDsPolicyDTO>) GWT.create(NumberOfIDsPolicyCrudService.class));
    }

}
