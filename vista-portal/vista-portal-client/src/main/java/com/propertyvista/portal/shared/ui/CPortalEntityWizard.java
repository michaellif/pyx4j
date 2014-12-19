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
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.shared.PortalSite;

public class CPortalEntityWizard<E extends IEntity> extends CEntityWizard<E> {

    private final IWizardView<? extends IEntity> view;

    public CPortalEntityWizard(Class<E> rootClass, final IWizardView<? extends IEntity> view, String headerCaption, String endButtonCaption,
            ThemeColor themeColor) {
        this(rootClass, view, new PortalEntityWizardDecorator<E>(headerCaption, endButtonCaption, themeColor));
    }

    public CPortalEntityWizard(Class<E> rootClass, final IWizardView<? extends IEntity> view, WizardDecorator<E> decorator) {
        super(rootClass, new VistaEditorsComponentFactory());
        setDecorator(decorator);
        this.view = view;
    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);
        calculateButtonsState();
        PortalSite.scrollToTop();
        if (view.getPresenter() != null) {
            view.getPresenter().onStepSelected(selectedStep);
        }
    }

    public IWizardView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected void onFinish() {
        view.getPresenter().finish();
    }

    @Override
    protected void onCancel() {
        view.getPresenter().cancel();
    };

    public void calculateButtonsState() {
        if (getDecorator() instanceof WizardDecorator) {
            ((WizardDecorator<E>) getDecorator()).calculateButtonsState();
        }
    }

    static class PortalEntityWizardDecorator<E extends IEntity> extends WizardDecorator<E> {

        public PortalEntityWizardDecorator(String headerCaption, String endButtonCaption, ThemeColor themeColor) {
            super(endButtonCaption);
            getBtnPrevious().setVisible(false);
            setCaption(headerCaption);

            getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

            getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

            getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));
        }
    }
}
