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
import com.pyx4j.forms.client.ui.form.EditableFormDecorator;

public abstract class CPortalEntityEditor<E extends IEntity> extends CPortalEntityForm<E> {

    public CPortalEntityEditor(Class<E> clazz, IEditorView<? extends IEntity> view, String headerCaption, ThemeColor themeColor) {
        super(clazz, view, headerCaption, themeColor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IEditorView<E> getView() {
        return (IEditorView<E>) super.getView();
    }

    @Override
    protected EditableFormDecorator<E> createDecorator() {
        EditableFormDecorator<E> decorator = new EditableFormDecorator<E>() {

            @Override
            protected void onEdit() {
                getView().getPresenter().edit();
            }

            @Override
            protected void onSave() {
                getView().getPresenter().save();
            }

            @Override
            protected void onCancel() {
                getView().getPresenter().populate();
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
