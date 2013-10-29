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
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;

public abstract class CPortalEntityForm<E extends IEntity> extends CEntityForm<E> {

    private final IFormView<? extends IEntity> view;

    private FormDecorator<E, CEntityForm<E>> decorator;

    private final String headerCaption;

    private final ThemeColor themeColor;

    public CPortalEntityForm(Class<E> clazz, IFormView<? extends IEntity> view, String headerCaption, ThemeColor themeColor) {
        super(clazz);
        this.view = view;
        this.headerCaption = headerCaption;
        this.themeColor = themeColor;
        setViewable(true);
    }

    protected String getHeaderCaption() {
        return headerCaption;
    }

    protected ThemeColor getThemeColor() {
        return themeColor;
    }

    public IFormView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected FormDecorator<E, CEntityForm<E>> createDecorator() {
        decorator = new FormDecorator<E, CEntityForm<E>>();

        decorator.setCaption(headerCaption);

        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getFooterPanel().setVisible(false);

        return decorator;
    }

    @Override
    public FormDecorator<E, CEntityForm<E>> getDecorator() {
        return decorator;
    }
}
