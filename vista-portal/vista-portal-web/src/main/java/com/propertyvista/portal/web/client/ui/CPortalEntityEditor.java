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
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.form.EditableFormDecorator;

import com.propertyvista.portal.web.client.ui.IEditorView.IEditorPresenter;

public abstract class CPortalEntityEditor<E extends IEntity> extends CEntityForm<E> {

    private final IEditorView<? extends IEntity> view;

    private EditableFormDecorator<E> decorator;

    private final String headerCaption;

    private final ThemeColor themeColor;

    public CPortalEntityEditor(Class<E> clazz, IEditorView<? extends IEntity> view, String headerCaption, ThemeColor themeColor) {
        this(clazz, null, view, headerCaption, themeColor);
    }

    public CPortalEntityEditor(Class<E> clazz, IEditableComponentFactory factory, IEditorView<? extends IEntity> view, String headerCaption,
            ThemeColor themeColor) {
        super(clazz, factory);
        this.view = view;
        this.headerCaption = headerCaption;
        this.themeColor = themeColor;
        setViewable(true);
    }

    public IFormView<? extends IEntity> getView() {
        return view;
    }

    @Override
    protected IDecorator<?> createDecorator() {
        decorator = new EditableFormDecorator<E>() {

            @Override
            protected void onEdit() {
                view.getPresenter().edit();
            }

            @Override
            protected void onSave() {
                view.getPresenter().save();
            }

            @Override
            protected void onCancel() {
                view.getPresenter().cancel();
            }
        };

        decorator.setCaption(headerCaption);

        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getMainPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getHeaderPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopWidth", "5px");
        decorator.getFooterPanel().getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

        return decorator;
    }
}
