/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.wizard;

import com.google.gwt.event.logical.shared.SelectionEvent;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;

public abstract class VistaWizardForm<E extends IEntity> extends CEntityWizard<E> {

    private final IWizard<? extends IEntity> view;

    private WizardDecorator<E> decorator;

    private final String caption;

    private final String endButtonCaption;

    public VistaWizardForm(Class<E> rootClass, final IWizard<? extends IEntity> view, String caption, String endButtonCaption) {
        super(rootClass);
        this.view = view;
        this.caption = caption;
        this.endButtonCaption = endButtonCaption;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        view.onStepChange();
    }

    public IWizard<? extends IEntity> getView() {
        return view;
    }

    @Override
    public WizardDecorator<E> getDecorator() {
        return decorator;
    }

    @Override
    protected void onFinish() {
        view.getPresenter().finish();
    }

    @Override
    protected void onCancel() {
        view.getPresenter().cancel();
    };

    @Override
    protected IDecorator<?> createDecorator() {
        decorator = new WizardDecorator<E>(endButtonCaption);
        decorator.setCaption(caption);
        return decorator;
    }

    public void calculateButtonsState() {
        if (decorator != null) {
            decorator.calculateButtonsState();
        }
    }

    public void setCaption(String stepTitle) {
        if (decorator != null) {
            decorator.setCaption(stepTitle);
        }
    }

}
