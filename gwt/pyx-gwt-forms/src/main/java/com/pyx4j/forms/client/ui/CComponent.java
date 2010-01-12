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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public abstract class CComponent<E extends INativeComponent> implements HasHandlers, HasPropertyChangeHandlers {

    private String name;

    private String title;

    private String toolTip;

    private CContainer parent;

    private CLayoutConstraints constraints;

    private boolean inheritContainerAccessRules = true;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    ComponentAccessAdapter defaultAccessAdapter;

    private HandlerManager handlerManager;

    private String width = "";

    private String height = "";

    /**
     * Basic information would be available in server log
     */
    public static String runtimeCrashInfo(CComponent<?> component) {
        if (component == null) {
            return "n/a";
        }
        return component.getClass() + " " + component.getTitle();
    }

    public CComponent() {
        this(null);
    }

    public CComponent(String title) {
        this.title = title;
        defaultAccessAdapter = new ComponentAccessAdapter();
        addAccessAdapter(defaultAccessAdapter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TITLE_PROPERTY);
    }

    public String getComponentDebugID() {
        if (getName() != null) {
            return getName();
        } else {
            return getTitle();
        }
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        if (getNativeComponent() != null) {
            getNativeComponent().setWidth(width);
        }
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        if (getNativeComponent() != null) {
            getNativeComponent().setHeight(height);
        }
    }

    public void setParent(CContainer parent) {
        this.parent = parent;
        if (inheritContainerAccessRules) {
            addAccessAdapter(parent.getContainerAccessAdapter());
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    protected HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
    }

    protected final boolean hasEventHandlers(GwtEvent.Type<?> type) {
        if (handlerManager == null) {
            return false;
        } else {
            return handlerManager.isEventHandled(type);
        }
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

    public CContainer getParent() {
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

    public abstract E getNativeComponent();

    public abstract E initNativeComponent();

    protected void applyVisibilityRules() {
        boolean visible = isVisible();
        if (getNativeComponent() != null && getNativeComponent().isVisible() != visible) {
            getNativeComponent().setVisible(visible);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY);
        }
    }

    protected void applyEnablingRules() {
        boolean enabled = isEnabled();
        if (getNativeComponent() != null && getNativeComponent().isEnabled() != enabled) {
            getNativeComponent().setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.ENABLED_PROPERTY);
        }
    }

    protected void applyAccessibilityRules() {
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
