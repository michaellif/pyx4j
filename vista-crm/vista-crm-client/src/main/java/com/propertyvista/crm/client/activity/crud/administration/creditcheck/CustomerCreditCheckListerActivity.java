/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.creditcheck;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.administration.creditchecks.CustomerCreditCheckListerView;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.CustomerCreditCheckCrudService;

public class CustomerCreditCheckListerActivity extends AbstractListerActivity<CustomerCreditCheckDTO> {

    public CustomerCreditCheckListerActivity(Place place) {
        super(place,  CrmSite.getViewFactory().instantiate(CustomerCreditCheckListerView.class), GWT
                .<CustomerCreditCheckCrudService> create(CustomerCreditCheckCrudService.class), CustomerCreditCheckDTO.class);
    }

}
