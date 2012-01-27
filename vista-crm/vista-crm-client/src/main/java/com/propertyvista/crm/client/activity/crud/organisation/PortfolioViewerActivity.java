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
package com.propertyvista.crm.client.activity.crud.organisation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;

import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.OrganizationViewFactory;
import com.propertyvista.crm.rpc.services.organization.PortfolioCrudService;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioViewerActivity extends ViewerActivityBase<Portfolio> {

    @SuppressWarnings("unchecked")
    public PortfolioViewerActivity(Place place) {
        super(place, OrganizationViewFactory.instance(PortfolioViewerView.class), (AbstractCrudService<Portfolio>) GWT.create(PortfolioCrudService.class));
    }
}
