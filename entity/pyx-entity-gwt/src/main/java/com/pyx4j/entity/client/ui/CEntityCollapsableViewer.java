package com.pyx4j.entity.client.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityContainer;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.widgets.client.images.WidgetsImages;

public abstract class CEntityCollapsableViewer<E extends IEntity> extends CEntityContainer<E> {
    private boolean collapsed;

    private final WidgetsImages images;

    private final VerticalPanel mainPanel = new VerticalPanel();

    private final SimplePanel collapsedPanel = new SimplePanel();

    private final SimplePanel expandedPanel = new SimplePanel();

    public CEntityCollapsableViewer(WidgetsImages images) {
        this.images = images;
    }

    public abstract IsWidget createExpandedContent(E value);

    public abstract IsWidget createCollapsedContent(E value);

    @Override
    protected ICollapsableDecorator<E> createDecorator() {
        return new BaseCollapsableDecorator<E>(images);
    }

    @Override
    public final IsWidget createContent() {
        mainPanel.setWidth("100%");
        mainPanel.add(collapsedPanel);
        mainPanel.add(expandedPanel);
        setCollapsed(true);

        return mainPanel;
    }

    @Override
    public Collection<? extends CComponent<?, ?>> getComponents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setComponentsValue(E value, boolean fireEvent, boolean populate) {
        collapsedPanel.setWidget(createCollapsedContent(value));
        expandedPanel.setWidget(createExpandedContent(value));
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        collapsedPanel.setVisible(collapsed);
        expandedPanel.setVisible(!collapsed);
    }

    public boolean isCollapsed() {
        return collapsed;
    }
}
