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

import com.google.gwt.user.client.Window;
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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

public class EntityViewImpl<E extends IEntity> extends FlowPanel implements EntityView<E> {

    protected static final I18n i18n = I18n.get(EntityViewImpl.class);

    private final SimplePanel formHolder;

    @Deprecated
    private final FlowPanel footer;

    private CEntityForm<E> form;

    private EntityPresenter<E> presenter;

    private Layout widgetLayout = Layout.horisontal;

    public EntityViewImpl() {
        add(formHolder = new SimplePanel());
        add(footer = new FlowPanel());

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout();
            }

        });
    }

    public void doLayout() {
        Layout newWdgetLayout = getWidgetLayout();
        if (widgetLayout != newWdgetLayout) {
            updateDecoratorsLayout(form, newWdgetLayout);
            widgetLayout = newWdgetLayout;
        }
    }

    protected void setForm(CEntityForm<E> theForm) {
        form = theForm;
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

    public Layout getWidgetLayout() {
        Layout layout;
        switch (LayoutType.getLayoutType(Window.getClientWidth())) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            layout = Layout.vertical;
            break;

        case tabletLandscape:
        case monitor:
        case huge:
            layout = Layout.horisontal;
            break;
        default:
            layout = Layout.horisontal;
        }
        return layout;
    }

    public void updateDecoratorsLayout(CContainer<?, ?> container, Layout layout) {
        if (container.getComponents() == null) {
            return;
        }
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
