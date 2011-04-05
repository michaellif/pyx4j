/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public abstract class CComponent<WIDGET_TYPE extends Widget & INativeComponent> implements HasHandlers, HasPropertyChangeHandlers, IsWidget {

    private String title;

    private String toolTip;

    private CContainer<?> parent;

    private CLayoutConstraints constraints;

    private boolean inheritContainerAccessRules = true;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    ComponentAccessAdapter defaultAccessAdapter;

    private WIDGET_TYPE widget;

    private EventBus eventBus;

    private String width = "";

    private String height = "";

    private String stylePrefix = null;

    private IDebugId debugId;

    public CComponent() {
        this(null);
    }

    public CComponent(String title) {
        this.title = title;
        defaultAccessAdapter = new ComponentAccessAdapter();
        addAccessAdapter(defaultAccessAdapter);
    }

    /**
     * Basic information would be available in server log
     */
    public static String runtimeCrashInfo(CComponent<?> component) {
        if (component == null) {
            return "n/a";
        }
        return component.getClass() + " " + component.getTitle();
    }

    public void setStylePrefix(String stylePrefix) {
        this.stylePrefix = stylePrefix;
        if (isWidgetCreated()) {
            ((INativeComponent) asWidget()).installStyles(stylePrefix);
        }
    }

    public String getStylePrefix() {
        return stylePrefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TITLE_PROPERTY);
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        if (isWidgetCreated()) {
            asWidget().setWidth(width);
        }
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        if (isWidgetCreated()) {
            asWidget().setHeight(height);
        }
    }

    public void setParent(CContainer<?> parent) {
        this.parent = parent;
        if (inheritContainerAccessRules) {
            addAccessAdapter(parent.getContainerAccessAdapter());
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

    protected EventBus ensureHandlers() {
        return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    public void inheritContainerAccessRules(boolean inherit) {
        inheritContainerAccessRules = inherit;
        if (parent != null) {
            if (inherit) {
                if (!containsAccessAdapter(parent.getContainerAccessAdapter())) {
                    addAccessAdapter(parent.getContainerAccessAdapter());
                }
            } else {
                removeAccessAdapter(parent.getContainerAccessAdapter());
            }
        }
    }

    public CContainer<?> getParent() {
        return parent;
    }

    public boolean isEnabled() {
        for (IAccessAdapter adapter : accessAdapters) {
            if (!adapter.isEnabled(this)) {
                return false;
            }
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        defaultAccessAdapter.setEnabled(enabled);
        applyEnablingRules();
    }

    public boolean isVisible() {
        for (IAccessAdapter adapter : accessAdapters) {
            if (!adapter.isVisible(this)) {
                return false;
            }
        }
        return true;
    }

    public void setVisible(boolean visible) {
        defaultAccessAdapter.setVisible(visible);
        applyVisibilityRules();
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        return addHandler(handler, PropertyChangeEvent.getType());
    }

    public void addAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.add(adapter);
        applyAccessibilityRules();
    }

    public void removeAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.remove(adapter);
        applyAccessibilityRules();
    }

    public boolean containsAccessAdapter(IAccessAdapter adapter) {
        return accessAdapters.contains(adapter);
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
    }

    protected abstract WIDGET_TYPE createWidget();

    protected void onWidgetCreated() {
        applyAccessibilityRules();
    }

    public boolean isWidgetCreated() {
        return widget != null;
    }

    @Override
    public WIDGET_TYPE asWidget() {
        if (widget == null) {
            try {
                widget = createWidget();
            } catch (Throwable e) {
                throw new Error("Widget could not be initialized", e);
            }
            if (getDebugId() != null) {
                setDebugId(getDebugId());
            }
            onWidgetCreated();
        }
        return widget;
    }

    public IDebugId getDebugId() {
        if ((parent != null) && (debugId != null)) {
            return new CompositeDebugId(parent.getDebugId(), debugId);
        } else {
            return debugId;
        }
    }

    public void setDebugId(IDebugId debugId) {
        this.debugId = debugId;
        if ((widget != null) && (debugId != null)) {
            widget.ensureDebugId(getDebugId().getDebugIdString());
        }
    }

    public void applyVisibilityRules() {
        boolean visible = isVisible();
        if (isWidgetCreated() && asWidget().isVisible() != visible) {
            asWidget().setVisible(visible);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY);
        }
    }

    public void applyEnablingRules() {
        boolean enabled = isEnabled();
        if (isWidgetCreated() && asWidget().isEnabled() != enabled) {
            asWidget().setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.ENABLED_PROPERTY);
        }
    }

    public void applyAccessibilityRules() {
        applyVisibilityRules();
        applyEnablingRules();
    }

    protected Collection<IAccessAdapter> getAccessAdapters() {
        return accessAdapters;
    }

    public CLayoutConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CLayoutConstraints constraints) {
        this.constraints = constraints;
    }

}
