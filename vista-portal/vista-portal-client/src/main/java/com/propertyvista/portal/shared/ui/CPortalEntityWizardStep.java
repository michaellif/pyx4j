/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.wizardstep.WizardStepDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class CPortalEntityWizardStep<E extends IEntity> extends CEntityForm<E> {

    private static final I18n i18n = I18n.get(CPortalEntityWizardStep.class);

    private final IWizardStepView<? extends IEntity> view;

    private WizardStepDecorator<E> decorator;

    private final String headerCaption;

    private final String btnNextCaption;

    private final ThemeColor themeColor;

    public CPortalEntityWizardStep(Class<E> clazz, IWizardStepView<? extends IEntity> view, String headerCaption, String btnNextCaption, ThemeColor themeColor) {
        super(clazz);
        this.view = view;
        this.headerCaption = headerCaption;
        this.btnNextCaption = btnNextCaption;
        this.themeColor = themeColor;
        setViewable(true);
    }

    protected String getHeaderCaption() {
        return headerCaption;
    }

    protected ThemeColor getThemeColor() {
        return themeColor;
    }

    public IWizardStepView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected WizardStepDecorator<E> createDecorator() {
        decorator = new WizardStepDecorator<E>(btnNextCaption) {

            @Override
            protected void onNext() {
                if (!isValid()) {
                    setUnconditionalValidationErrorRendering(true);
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("There has been an error. Please check your data and try again."));
                } else {
                    getView().getPresenter().next();
                }
            }

            @Override
            protected void onPrevious() {
                //TODO
            }

            @Override
            protected void onCancel() {
                //TODO
            }
        };

        decorator.setCaption(getHeaderCaption());

        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(getThemeColor(), 1));

        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(getThemeColor(), 1));

        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(getThemeColor(), 1));

        return decorator;
    }
}
