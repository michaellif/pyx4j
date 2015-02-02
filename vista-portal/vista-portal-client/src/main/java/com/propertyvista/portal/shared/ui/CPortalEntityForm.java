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
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;

public abstract class CPortalEntityForm<E extends IEntity> extends CForm<E> {

    private final IViewerView<? extends IEntity> view;

    private FormDecorator<E> decorator;

    private final String headerCaption;

    private IsWidget footerActionWidget;

    private final ThemeColor themeColor;

    public CPortalEntityForm(Class<E> clazz, IViewerView<? extends IEntity> view, String headerCaption, ThemeColor themeColor) {
        this(clazz, view, headerCaption, null, themeColor);
    }

    public CPortalEntityForm(Class<E> clazz, IViewerView<? extends IEntity> view, String headerCaption, IsWidget footerActionWidget, ThemeColor themeColor) {
        super(clazz, new VistaEditorsComponentFactory());
        this.view = view;
        this.headerCaption = headerCaption;
        this.footerActionWidget = footerActionWidget;
        this.themeColor = themeColor;
        setViewable(true);
    }

    protected String getHeaderCaption() {
        return headerCaption;
    }

    protected ThemeColor getThemeColor() {
        return themeColor;
    }

    public IViewerView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected FormDecorator<E> createDecorator() {
        decorator = new FormDecorator<E>();

        decorator.setCaption(headerCaption);

        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        if (footerActionWidget == null) {
            decorator.getFooterPanel().setVisible(false);
        } else {
            decorator.addFooterToolbarWidget(footerActionWidget);
            decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
            decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));
        }

        return decorator;
    }
}
