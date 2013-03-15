/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.dictionary;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.crud.lister.ILister;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ProductDictionaryView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureItemTypeCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceItemTypeCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.UtilityCrudService;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.Utility;

public class ProductDictionaryViewActivity extends AbstractActivity implements ProductDictionaryView.Presenter {

    private final AppPlace place;

    protected final ProductDictionaryView view;

    ILister.Presenter<ServiceItemType> serviceLister;

    ILister.Presenter<FeatureItemType> featureLister;

    ILister.Presenter<Utility> utilityLister;

    public ProductDictionaryViewActivity(AppPlace place) {
        this.place = place;
        this.view = SettingsViewFactory.instance(ProductDictionaryView.class);

        serviceLister = new ListerController<ServiceItemType>(view.getServiceListerView(),
                GWT.<ServiceItemTypeCrudService> create(ServiceItemTypeCrudService.class), ServiceItemType.class);
        featureLister = new ListerController<FeatureItemType>(view.getFeatureListerView(),
                GWT.<FeatureItemTypeCrudService> create(FeatureItemTypeCrudService.class), FeatureItemType.class);
        utilityLister = new ListerController<Utility>(view.getUtilityListerView(), GWT.<UtilityCrudService> create(UtilityCrudService.class), Utility.class);
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        serviceLister.populate();
        featureLister.populate();
        utilityLister.populate();
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public void edit() {
        // nothing needs here!..
    }

    @Override
    public void cancel() {
        // nothing needs here!..
    }

    @Override
    public void view(Key entityId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void approveFinal() {
        // TODO Auto-generated method stub
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
    }
}
