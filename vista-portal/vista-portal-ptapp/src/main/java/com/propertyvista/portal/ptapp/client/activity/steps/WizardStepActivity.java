/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepPresenter;
import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepView;
import com.propertyvista.portal.rpc.ptapp.services.AbstractWizardService;

public class WizardStepActivity<E extends IEntity, T extends WizardStepPresenter<E>> extends AbstractActivity implements WizardStepPresenter<E> {

    protected static final Logger log = LoggerFactory.getLogger(WizardStepActivity.class);

    private final WizardStepView<E, T> view;

    private E entity;

    private final Class<E> clazz;

    private final AbstractWizardService<E> wizardServices;

    protected AppPlace currentPlace;

    @SuppressWarnings("unchecked")
    public WizardStepActivity(WizardStepView<E, T> view, Class<E> clazz, AbstractWizardService<E> wizardServices) {
        this.view = view;
        this.clazz = clazz;
        view.setPresenter((T) this);

        this.wizardServices = wizardServices;
    }

    public WizardStepActivity<E, T> withPlace(AppPlace place) {
        currentPlace = place;
        return this;
    }

    protected Key getCurrentTenantId() {
        return null;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        wizardServices.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                if (result == null) {
                    E newEntity = EntityFactory.create(clazz);

                    createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

                        @Override
                        public void onSuccess(E result) {
                            entity = result;
                            //log.info("CREATED {}", entity);
                            view.populate(entity);
                        }

                    });
                } else {
                    entity = result;
                    //log.info("LOADED {}", entity);
                    view.populate(entity);
                }
            }
        }, getCurrentTenantId());

    }

    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

    @Override
    public void save(E entity) {
        wizardServices.save(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                //log.info("SAVED {}", result);
                WizardStepActivity.this.entity = result;
                PtAppSite.getWizardManager().nextStep();
            }
        }, entity);
    }

    protected WizardStepView<E, T> getView() {
        return view;
    }

}