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
package com.propertyvista.portal.client.ptapp.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.WizardStepPresenter;
import com.propertyvista.portal.client.ptapp.ui.WizardStepView;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.rpc.pt.services.AbstractWizardServices;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.rpc.AppPlace;

public class WizardStepActivity<E extends IEntity & IBoundToApplication, T extends WizardStepPresenter<E>> extends AbstractActivity implements
        WizardStepPresenter<E> {

    protected static final Logger log = LoggerFactory.getLogger(WizardStepActivity.class);

    private final WizardStepView<E, T> view;

    private E entity;

    private final Class<E> clazz;

    private final AbstractWizardServices<E> wizardServices;

    @SuppressWarnings("unchecked")
    public WizardStepActivity(WizardStepView<E, T> view, Class<E> clazz, AbstractWizardServices<E> wizardServices) {
        this.view = view;
        this.clazz = clazz;
        view.setPresenter((T) this);

        this.wizardServices = wizardServices;
//        if (clazz.equals(Pets.class) || clazz.equals(Charges.class) || clazz.equals(UnitSelection.class)) {
//            this.wizardServices = wizardServices;
//        } else {
//            this.wizardServices = null;
//        }
    }

    public WizardStepActivity<E, T> withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        if (this.wizardServices == null) {
            log.warn("Should not see this");
//            oldGet();
        } else {
            wizardServices.retrieve(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    if (result == null) {
                        E newEntity = EntityFactory.create(clazz);

                        createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

                            @Override
                            public void onSuccess(E result) {
                                entity = result;
                                log.info("CREATED {}", entity);
                                view.populate(entity);
                            }

                        });
                    } else {
                        entity = result;
                        log.info("LOADED {}", entity);
                        view.populate(entity);
                    }
                }
            }, null);
        }
    }

//    @Deprecated
//    private void oldGet() {
//        RPCManager.execute(PotentialTenantServices.RetrieveByPK.class, EntityCriteriaByPK.create(clazz, entity), new DefaultAsyncCallback<IEntity>() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void onSuccess(IEntity result) {
//                if (result == null) {
//                    E newEntity = EntityFactory.create(clazz);
//
//                    createNewEntity(newEntity, new DefaultAsyncCallback<E>() {
//
//                        @Override
//                        public void onSuccess(E result) {
//                            entity = result;
//                            log.info("CREATED {}", entity);
//                            view.populate(entity);
//                        }
//
//                    });
//                } else {
//                    entity = (E) result;
//                    log.info("LOADED {}", entity);
//                    view.populate(entity);
//                }
//            }
//        });
//
//    }

    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

    @Override
    public void save(E entity) {
        if (this.wizardServices != null) {
            wizardServices.save(new DefaultAsyncCallback<E>() {
                @Override
                public void onSuccess(E result) {
                    log.info("SAVED {}", result);
                    WizardStepActivity.this.entity = result;
                    PtAppWizardManager.instance().nextStep();
                }
            }, entity);
        } else {
            log.warn("SHOULD NOT SEE THIS");
//            oldSave(entity);
        }
    }

//    @Deprecated
//    private void oldSave(E entity) {
//        RPCManager.execute(PotentialTenantServices.Save.class, entity, new DefaultAsyncCallback<IEntity>() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void onSuccess(IEntity result) {
//                log.info("SAVED {}", result);
//                WizardStepActivity.this.entity = (E) result;
//                PtAppWizardManager.instance().nextStep();
//            }
//        });
//    }

    protected WizardStepView<E, T> getView() {
        return view;
    }

}