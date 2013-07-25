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
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;

public abstract class VistaWizardForm<E extends IEntity> extends CEntityWizard<E> {

    private final IWizard<? extends IEntity> view;

    public VistaWizardForm(Class<E> rootClass, final IWizard<? extends IEntity> view) {
        super(rootClass);
        this.view = view;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        view.onStepChange();
    }

    public IWizard<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected void onFinish() {
        view.getPresenter().finish();
    }

}
