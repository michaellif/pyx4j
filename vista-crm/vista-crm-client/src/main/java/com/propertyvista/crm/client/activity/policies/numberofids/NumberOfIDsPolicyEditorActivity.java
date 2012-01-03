/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.numberofids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.policies.numberofids.NumberOfIDsPolicyEdtiorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.PolicyViewFactory;
import com.propertyvista.crm.rpc.services.policies.policy.NumberOfIDsPolicyCrudService;
import com.propertyvista.domain.policy.dto.NumberOfIDsPolicyDTO;

public class NumberOfIDsPolicyEditorActivity extends EditorActivityBase<NumberOfIDsPolicyDTO> implements NumberOfIDsPolicyEdtiorView.Presenter {

    public NumberOfIDsPolicyEditorActivity(Place place) {
        super(place, PolicyViewFactory.instance(NumberOfIDsPolicyEdtiorView.class), (AbstractCrudService<NumberOfIDsPolicyDTO>) GWT
                .create(NumberOfIDsPolicyCrudService.class), NumberOfIDsPolicyDTO.class);
    }

}
