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

import java.util.Collection;
import java.util.Vector;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.gwt.NativeGroupBoxPanel;

/**
 * 
 * This is a border panel that has single CContainer as a child.
 * 
 */
public class CGroupBoxPanel extends CContainer<NativeGroupBoxPanel> {

    public static enum Layout {

        PLAIN,

        /**
         * Child visibility does not changes on collapse and expand.
         */
        COLLAPSIBLE,

        /**
         * Child would become invisible when panel is Collapsed.
         */
        CHECKBOX_TOGGLE;
    }

    private final Layout layout;

    private CContainer component;

    private final Collection<CComponent<?>> componentCollection = new Vector<CComponent<?>>();

    private boolean expended = true;

    private boolean innerCommponentInitialized = false;

    public CGroupBoxPanel(String title) {
        this(title, true);
    }

    public CGroupBoxPanel(String title, boolean collapsible) {
        this(title, collapsible ? Layout.COLLAPSIBLE : Layout.PLAIN);
    }

    public CGroupBoxPanel(String title, Layout layout) {
        super(title);
        this.layout = layout;
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addHandler(handler, KeyUpEvent.getType());
    }

    //TODO shouldn't be public. When all the components of UI will be CComponents - change visibility
    @Override
    public NativeGroupBoxPanel initWidget() {
        NativeGroupBoxPanel nativePanel = new NativeGroupBoxPanel(this, layout);
        if (isExpended()) {
            initInnerComponent();
        }
        applyAccessibilityRules();

        nativePanel.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                fireEvent(event);
            }
        });
        return nativePanel;
    }

    private void initInnerComponent() {
        if (!innerCommponentInitialized && component != null && isWidgetCreated()) {
            asWidget().add((INativeComponent) component.asWidget(), null);
            innerCommponentInitialized = true;
        }
    }

    @Override
    public void addComponent(CComponent<?> component) {
        if (!(component instanceof CContainer)) {
            throw new RuntimeException("Can't add CComponent that is not a CContainer to CGroupBoxPanel");
        }
        this.component = (CContainer) component;
        componentCollection.clear();
        componentCollection.add(this.component);
        component.setParent(this);
    }

    @Override
    public IAccessAdapter getContainerAccessAdapter() {
        if (layout == Layout.CHECKBOX_TOGGLE) {
            return new ContainerAccessAdapter(this) {
                @Override
                public boolean isVisible(CComponent<?> component) {
                    return expended && super.isVisible(component);
                }
            };
        } else {
            return super.getContainerAccessAdapter();
        }
    }

    public void addAllComponents(CComponent<?>[] componentsArray) {
        throw new NotApplicableException();
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        return this.componentCollection;
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
        if (expended) {
            initInnerComponent();
        }
        if (isWidgetCreated() && isCollapsible()) {
            asWidget().setExpanded(expended);
        } else if ((layout == Layout.CHECKBOX_TOGGLE) && (component != null)) {
            component.applyVisibilityRules();
        }
    }

    public boolean isExpended() {
        return expended;
    }

    public void onExpended(boolean value) {
        expended = value;
        if (expended) {
            initInnerComponent();
        }
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.EXPENDED_PROPERTY);
        if ((layout == Layout.CHECKBOX_TOGGLE) && (component != null)) {
            component.applyVisibilityRules();
        }
    }

    public boolean isCollapsible() {
        return layout != Layout.PLAIN;
    }

    public Layout getLayout() {
        return layout;
    }

}
