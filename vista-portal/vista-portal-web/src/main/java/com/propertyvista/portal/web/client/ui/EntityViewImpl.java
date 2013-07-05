/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.web.client.ui.residents.Edit;

public class EntityViewImpl<E extends IEntity> extends FlowPanel implements EntityView<E> {

    protected static final I18n i18n = I18n.get(EntityViewImpl.class);

    private final SimplePanel formHolder;

    @Deprecated
    private final FlowPanel footer;

    private CEntityForm<E> form;

    private EntityPresenter<E> presenter;

    public EntityViewImpl(CEntityForm<E> form) {
        add(formHolder = new SimplePanel());
        add(footer = new FlowPanel());

        if (form != null) {
            setForm(form);
        }
    }

    protected void setForm(CEntityForm<E> theForm) {
        form = theForm;

        // set form role-behavior:
        if (this instanceof Edit) {
            form.setEditable(true);
            form.setViewable(false);
        } else {
            form.setEditable(false);
            form.setViewable(true);
        }

        form.initContent();
        formHolder.setWidget(form.asWidget());
    }

    @Deprecated
    protected void addToFooter(IsWidget widget) {
        footer.add(widget);
    }

    @Deprecated
    protected void removeFromFooter(IsWidget widget) {
        footer.remove(widget);
    }

    @Override
    public void setPresenter(EntityPresenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(E value) {
        form.reset();
        form.populate(value);
    }

    public EntityPresenter<E> getPresenter() {
        return presenter;
    }

    public CEntityForm<E> getForm() {
        return form;
    }

    public static void updateDecoratorsLayout(CContainer<?, ?> container, Layout layout) {
        for (CComponent<?, ?> component : container.getComponents()) {
            if (component.getDecorator() instanceof WidgetDecorator) {
                WidgetDecorator decorator = (WidgetDecorator) component.getDecorator();
                decorator.setLayout(layout);
            }
            if (component instanceof CContainer) {
                updateDecoratorsLayout((CContainer<?, ?>) component, layout);
            }
        }
    }
}
