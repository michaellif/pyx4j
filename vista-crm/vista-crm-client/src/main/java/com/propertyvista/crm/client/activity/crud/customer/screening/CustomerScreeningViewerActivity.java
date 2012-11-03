/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.screening;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.customer.screening.CustomerScreeningViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.CustomerViewFactory;
import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningCrudService;
import com.propertyvista.domain.tenant.CustomerScreening;

public class CustomerScreeningViewerActivity extends CrmViewerActivity<CustomerScreening> {

    @SuppressWarnings("unchecked")
    public CustomerScreeningViewerActivity(CrudAppPlace place) {
        super(place, CustomerViewFactory.instance(CustomerScreeningViewerView.class), (AbstractCrudService<CustomerScreening>) GWT
                .create(CustomerScreeningCrudService.class));
    }
}
