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
package com.propertyvista.crm.client.activity.crud.settings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.settings.dictionary.ServiceDictionaryView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.FeatureItemTypeCrudService;
import com.propertyvista.crm.rpc.services.ServiceItemTypeCrudService;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceDictionaryViewActivity extends AbstractActivity implements ServiceDictionaryView.Presenter {

    protected final ServiceDictionaryView view;

    IListerView.Presenter serviceLister;

    IListerView.Presenter featureLister;

    @SuppressWarnings("unchecked")
    public ServiceDictionaryViewActivity(Place place) {
        this.view = (ServiceDictionaryView) SettingsViewFactory.instance(ServiceDictionaryView.class);

        serviceLister = new ListerActivityBase<ServiceItemType>(view.getServiceListerView(),
                (AbstractCrudService<ServiceItemType>) GWT.create(ServiceItemTypeCrudService.class), ServiceItemType.class);
        featureLister = new ListerActivityBase<ServiceItemType>(view.getFeatureListerView(),
                (AbstractCrudService<ServiceItemType>) GWT.create(FeatureItemTypeCrudService.class), ServiceItemType.class);

        setPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);
        populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) serviceLister).onStop();
        ((AbstractActivity) featureLister).onStop();
        super.onStop();
    }

    @Override
    public void setPlace(Place place) {
        serviceLister.setPlace(place);
        serviceLister.setPlace(place);
    }

    @Override
    public void populate() {
        serviceLister.populate();
        featureLister.populate();
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
    public Presenter getServiceListerPresenter() {
        return serviceLister;
    }

    @Override
    public Presenter getFeatureListerPresenter() {
        return featureLister;
    }

}
