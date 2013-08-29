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
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.event.logical.shared.SelectionEvent;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

public abstract class AbstractWizardForm<E extends IEntity> extends CEntityWizard<E> {

    private final IWizardView<? extends IEntity> view;

    private WizardDecorator<E> decorator;

    private final String headerCaption;

    private final String endButtonCaption;

    private final ThemeColor themeColor;

    public AbstractWizardForm(Class<E> rootClass, final IWizardView<? extends IEntity> view, String headerCaption, String endButtonCaption,
            ThemeColor themeColor) {
        super(rootClass);
        this.view = view;
        this.headerCaption = headerCaption;
        this.endButtonCaption = endButtonCaption;
        this.themeColor = themeColor;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        view.onStepChange();
    }

    public IWizardView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected void onFinish() {
        view.getPresenter().finish();
    }

    @Override
    protected IDecorator<?> createDecorator() {
        decorator = new WizardDecorator<E>(endButtonCaption) {
            @Override
            protected void onFinish() {
                view.getPresenter().finish();
            };

            @Override
            protected void onCancel() {
                view.getPresenter().cancel();
            };
        };

        decorator.getBtnPrevious().setVisible(false);
        decorator.setCaption(headerCaption);

        decorator.getContentHolder().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getContentHolder().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        return decorator;
    }

    public void calculateButtonsState() {
        if (decorator != null) {
            decorator.calculateButtonsState();
        }
    }

}
