/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.IWizardView;
import com.propertyvista.portal.shared.ui.IWizardView.IWizardFormPresenter;

public abstract class AbstractWizardActivity<E extends IEntity, T extends IWizardView<E>> extends SecurityAwareActivity implements IWizardFormPresenter<E> {

    private static final I18n i18n = I18n.get(AbstractWizardActivity.class);

    private final T view;

    public AbstractWizardActivity(Class<T> viewType) {
        view = PortalSite.getViewFactory().getView(viewType);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
    }

    public T getView() {
        return view;
    }

    protected void onDiscard() {
        view.setPresenter(null);
        view.reset();
    }

    @Override
    public void onCancel() {
        onDiscard();
        super.onCancel();
    }

    @Override
    public void onStop() {
        onDiscard();
        super.onStop();
    }

    @Override
    public void finish() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }

    @Override
    public String mayStop() {
        if (view.isDirty()) {
            String entityName = view.getValue().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                return i18n.tr("Changes to {0} were not saved", view.getValue().getEntityMeta().getCaption());
            } else {
                return i18n.tr("Changes to {0} ''{1}'' were not saved.", view.getValue().getEntityMeta().getCaption(), entityName);
            }
        } else {
            return null;
        }
    }

    /**
     * Descendants may override this method to supply some initialization info.
     * 
     */
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void onStepSelected(WizardStep selectedStep) {

    }
}
