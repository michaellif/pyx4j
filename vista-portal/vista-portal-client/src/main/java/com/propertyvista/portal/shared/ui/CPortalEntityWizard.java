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
package com.propertyvista.portal.shared.ui;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.wizard.CEntityWizard;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.shared.PortalSite;

public class CPortalEntityWizard<E extends IEntity> extends CEntityWizard<E> {

    private final IWizardView<? extends IEntity> view;

    private final String headerCaption;

    private final String endButtonCaption;

    private final ThemeColor themeColor;

    public CPortalEntityWizard(Class<E> rootClass, final IWizardView<? extends IEntity> view, String headerCaption, String endButtonCaption,
            ThemeColor themeColor) {
        super(rootClass, new VistaEditorsComponentFactory());
        this.view = view;
        this.headerCaption = headerCaption;
        this.endButtonCaption = endButtonCaption;
        this.themeColor = themeColor;
    }

    @Override
    protected void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);
        view.onStepChange();
        PortalSite.scrollToTop();
    }

    public IWizardView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected void onFinish() {
        view.getPresenter().submit();
    }

    @Override
    protected void onCancel() {
        view.getPresenter().cancel();
    };

    @Override
    protected IDecorator<?> createDecorator() {
        WizardDecorator<E> decorator = new WizardDecorator<E>(endButtonCaption);

        decorator.getBtnPrevious().setVisible(false);
        decorator.setCaption(headerCaption);

        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        return decorator;
    }

    public void calculateButtonsState() {
        if (getDecorator() != null) {
            ((WizardDecorator<?>) getDecorator()).calculateButtonsState();
        }
    }

}
