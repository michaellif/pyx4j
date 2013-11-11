/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.paymenttypeselection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.paymenttypeselection.PaymentTypeSelectionPolicyListerView;
import com.propertyvista.crm.rpc.services.policies.policy.PaymentTypeSelectionPolicyCrudService;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;

public class PaymentTypeSelectionPolicyListerActivity extends AbstractListerActivity<PaymentTypeSelectionPolicyDTO> {

    public PaymentTypeSelectionPolicyListerActivity(Place place) {
        super(place,  CrmSite.getViewFactory().getView(PaymentTypeSelectionPolicyListerView.class), GWT
                .<AbstractListService<PaymentTypeSelectionPolicyDTO>> create(PaymentTypeSelectionPolicyCrudService.class), PaymentTypeSelectionPolicyDTO.class);
    }

}
