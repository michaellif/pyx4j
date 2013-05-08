/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IWidget;
import com.pyx4j.widgets.client.ToggleButton;

public abstract class NComponent<DATA, WIDGET extends IWidget, CCOMP extends CComponent<DATA, ?>, VIEWER extends Widget> extends SimplePanel implements
        INativeComponent<DATA> {

    private WIDGET editor;

    private VIEWER viewer;

    private final CCOMP cComponent;

    private boolean viewable;

    private TriggerPanel triggerPanel;

    private ToggleButton triggerButton;

    public NComponent(CCOMP cComponent) {
        this(cComponent, null);
    }

    public NComponent(CCOMP cComponent, ToggleButton triggerButton) {
        super();
        this.cComponent = cComponent;
        this.triggerButton = triggerButton;
    }

    public WIDGET getEditor() {
        return editor;
    }

    protected ToggleButton getTriggerButton() {
        return triggerButton;
    }

    public VIEWER getViewer() {
        return viewer;
    }

    @Override
    public CCOMP getCComponent() {
        return cComponent;
    }

    protected abstract WIDGET createEditor();

    protected abstract VIEWER createViewer();

    protected void onEditorCreate() {
        editor.asWidget().setWidth(getCComponent().getWidth());
        setDebugId(getCComponent().getDebugId());
    }

    protected void onEditorInit() {
        setNativeValue(cComponent.getValue());
    }

    protected void onViewerCreate() {
        setDebugId(getCComponent().getDebugId());
    }

    protected void onViewerInit() {
        setNativeValue(cComponent.getValue());
    }

    @Override
    public void setViewable(boolean viewable) {
        this.viewable = viewable;
        if (viewable) {
            if (viewer == null) {
                viewer = createViewer();
                onViewerCreate();
            }
            onViewerInit();
            setWidget(viewer);
        } else {
            if (editor == null) {
                editor = createEditor();
                onEditorCreate();
                if (triggerButton != null) {
                    triggerPanel = new TriggerPanel(triggerButton);
                }
            }
            onEditorInit();
            if (triggerButton == null) {
                setWidget(editor);
            } else {
                setWidget(triggerPanel);
            }
        }
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        if (triggerPanel != null) {
            return triggerPanel.getGroupFocusHandler();
        } else {
            return null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        Button triggerButton = getTriggerButton();
        if (triggerButton != null) {
            triggerButton.setEnabled(isEditable() && enabled);
        }

        if (getEditor() != null) {
            getEditor().setEnabled(enabled);
            if (enabled) {
                getEditor().removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            } else {
                getEditor().addStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (isViewable()) {
            return false;
        } else {
            return getEditor().isEnabled();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        Button triggerButton = getTriggerButton();
        if (triggerButton != null) {
            triggerButton.setEnabled(isEnabled() && editable);
        }

        if (getEditor() != null) {
            getEditor().setEditable(editable);
            if (editable) {
                getEditor().removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            } else {
                getEditor().addStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            }
        }
    }

    @Override
    public boolean isEditable() {
        if (isViewable()) {
            return false;
        } else {
            return getEditor().isEditable();
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO remove this, or make configurable
        if (debugId == null) {
            return;
        }

        assert (debugId != null) : "Unassigned DebugId in native component of " + getCComponent().shortDebugInfo();
        if (getEditor() != null) {
            getEditor().ensureDebugId(debugId.debugId());

            if (triggerPanel != null)
                triggerPanel.ensureDebugId(debugId.debugId() + "-triggerPanel");

        }
        if (getViewer() != null) {
            getViewer().ensureDebugId(debugId.debugId());
        }
    }

    class TriggerPanel extends HorizontalPanel implements HasDoubleClickHandlers {

        private final ToggleButton triggerButton;

        private final GroupFocusHandler focusHandlerManager;

        public TriggerPanel(final ToggleButton triggerButton) {
            super();
            this.triggerButton = triggerButton;
            setStyleName(DefaultCComponentsTheme.StyleName.TriggerPannel.name());

            NComponent.this.getEditor().asWidget().setWidth("100%");
            add(NComponent.this.getEditor());
            setCellWidth(NComponent.this.getEditor(), "100%");

            focusHandlerManager = new GroupFocusHandler(this);

            if (NComponent.this.getEditor() instanceof NFocusComponent) {
                ((NFocusComponent<?, ?, ?, ?>) NComponent.this.getEditor()).addFocusHandler(focusHandlerManager);
                ((NFocusComponent<?, ?, ?, ?>) NComponent.this.getEditor()).addBlurHandler(focusHandlerManager);
            }

            triggerButton.addFocusHandler(focusHandlerManager);
            triggerButton.addBlurHandler(focusHandlerManager);

            add(triggerButton);
            setCellVerticalAlignment(triggerButton, ALIGN_TOP);

            String marginTop;
            if (BrowserType.isFirefox()) {
                marginTop = "0";
            } else if (BrowserType.isIE()) {
                marginTop = "1";
            } else {
                //Chrome and Safari
                marginTop = "2";
            }
            DOM.setStyleAttribute(triggerButton.getElement(), "marginTop", marginTop);
            DOM.setStyleAttribute(triggerButton.getElement(), "marginLeft", "4px");

            triggerButton.addKeyDownHandler(new KeyDownHandler() {

                @Override
                public void onKeyDown(KeyDownEvent event) {
                    switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_TAB:
                    case KeyCodes.KEY_ESCAPE:
                    case KeyCodes.KEY_UP:
                        if (triggerButton.isChecked()) {
                            triggerButton.toggleChecked();
                        }
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (!triggerButton.isChecked()) {
                            triggerButton.toggleChecked();
                        }
                        break;
                    }

                }
            });

            triggerButton.sinkEvents(Event.ONDBLCLICK);

            this.addDoubleClickHandler(new DoubleClickHandler() {
                @Override
                public void onDoubleClick(DoubleClickEvent event) {
                    if (NComponent.this.isEditable() && NComponent.this.isEnabled()) {
                        if (!triggerButton.isChecked()) {
                            triggerButton.toggleChecked();
                        }
                    }
                }
            });
        }

        @Override
        protected void onEnsureDebugId(String baseID) {
            //super.onEnsureDebugId(baseID);
            ((Widget) NComponent.this.getEditor()).ensureDebugId(baseID);
            // Special name for selenium to fire events instead of click
            triggerButton.ensureDebugId(CompositeDebugId.debugId(baseID, CCompDebugId.trigger));
        }

        public Button getTriggerButton() {
            return triggerButton;
        }

        protected GroupFocusHandler getGroupFocusHandler() {
            return focusHandlerManager;
        }

        @Override
        public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
            return addDomHandler(handler, DoubleClickEvent.getType());
        }

    }

    class NavigPanel extends HorizontalPanel {

        private final Button navigButton;

        public NavigPanel(ImageResource navigImage) {
            super();

            setStyleName(DefaultCComponentsTheme.StyleName.TriggerPannel.name());

            NComponent.this.getViewer().asWidget().setWidth("100%");
            add(NComponent.this.getViewer());
            setCellWidth(NComponent.this.getViewer(), "100%");

            navigButton = new Button(navigImage);

            add(navigButton);
            setCellVerticalAlignment(navigButton, ALIGN_TOP);

            String marginTop;
            if (BrowserType.isFirefox()) {
                marginTop = "0";
            } else if (BrowserType.isIE()) {
                marginTop = "1";
            } else {
                //Chrome and Safari
                marginTop = "2";
            }
            DOM.setStyleAttribute(navigButton.getElement(), "marginTop", marginTop);
            DOM.setStyleAttribute(navigButton.getElement(), "marginLeft", "4px");

        }

        public Button getNavigButton() {
            return navigButton;
        }

    }
}
