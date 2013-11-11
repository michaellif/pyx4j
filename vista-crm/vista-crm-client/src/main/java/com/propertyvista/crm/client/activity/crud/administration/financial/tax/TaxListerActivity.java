/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.financial.tax;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.administration.financial.tax.TaxListerView;
import com.propertyvista.crm.rpc.services.admin.TaxCrudService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxListerActivity extends AbstractListerActivity<Tax> {

    public TaxListerActivity(Place place) {
        super(place,  CrmSite.getViewFactory().getView(TaxListerView.class), GWT.<AbstractListService<Tax>> create(TaxCrudService.class), Tax.class);
    }
}
