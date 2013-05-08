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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
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

    private EditorHolder editorHolder;

    private ToggleButton triggerButton;

    private ViewerHolder viewerHolder;

    private Button actionButton;

    private Command navigationCommand;

    public NComponent(CCOMP cComponent) {
        super();
        this.cComponent = cComponent;
    }

    public WIDGET getEditor() {
        return editor;
    }

    public void setTriggerButton(ToggleButton triggerButton) {
        this.triggerButton = triggerButton;
        if (editorHolder != null) {
            editorHolder.setTriggerButton(triggerButton);
        }
    }

    public void setActionButton(Button actionButton) {
        this.actionButton = actionButton;
        if (viewerHolder != null) {
            viewerHolder.setActionButton(actionButton);
        }
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
                viewerHolder = new ViewerHolder(actionButton);
            }
            onViewerInit();
            setWidget(viewerHolder);
        } else {
            if (editor == null) {
                editor = createEditor();
                onEditorCreate();
                editorHolder = new EditorHolder(triggerButton);
            }
            onEditorInit();
            setWidget(editorHolder);
        }
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        if (editorHolder != null) {
            return editorHolder.getGroupFocusHandler();
        } else {
            return null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
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

            if (editorHolder != null)
                editorHolder.ensureDebugId(debugId.debugId() + "-triggerPanel");

        }
        if (getViewer() != null) {
            getViewer().ensureDebugId(debugId.debugId());
        }
    }

    class EditorHolder extends HorizontalPanel implements HasDoubleClickHandlers {

        private ToggleButton triggerButton;

        private final GroupFocusHandler focusHandlerManager;

        private final Set<HandlerRegistration> triggerButtonHandlerRegistrations = new HashSet<HandlerRegistration>();

        private String baseDebugID;

        public EditorHolder(final ToggleButton triggerButton) {
            super();
            setStyleName(DefaultCComponentsTheme.StyleName.EditorHolder.name());

            NComponent.this.getEditor().asWidget().setWidth("100%");
            add(NComponent.this.getEditor());
            setCellWidth(NComponent.this.getEditor(), "100%");
            setCellVerticalAlignment(NComponent.this.getEditor(), ALIGN_MIDDLE);

            focusHandlerManager = new GroupFocusHandler(this);

            if (NComponent.this.getEditor() instanceof NFocusComponent) {
                ((NFocusComponent<?, ?, ?, ?>) NComponent.this.getEditor()).addFocusHandler(focusHandlerManager);
                ((NFocusComponent<?, ?, ?, ?>) NComponent.this.getEditor()).addBlurHandler(focusHandlerManager);
            }

            setTriggerButton(triggerButton);
        }

        public void setTriggerButton(final ToggleButton triggerButton) {
            if (this.triggerButton != null) {
                remove(this.triggerButton);
                for (HandlerRegistration handlerRegistration : triggerButtonHandlerRegistrations) {
                    handlerRegistration.removeHandler();
                }
                triggerButtonHandlerRegistrations.clear();
            }

            this.triggerButton = triggerButton;

            if (triggerButton != null) {

                triggerButtonHandlerRegistrations.add(triggerButton.addFocusHandler(focusHandlerManager));
                triggerButtonHandlerRegistrations.add(triggerButton.addBlurHandler(focusHandlerManager));

                add(triggerButton);
                setCellVerticalAlignment(triggerButton, ALIGN_MIDDLE);

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

                triggerButton.sinkEvents(Event.ONDBLCLICK);

                triggerButtonHandlerRegistrations.add(triggerButton.addKeyDownHandler(new KeyDownHandler() {

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
                }));

                triggerButtonHandlerRegistrations.add(this.addDoubleClickHandler(new DoubleClickHandler() {
                    @Override
                    public void onDoubleClick(DoubleClickEvent event) {
                        if (NComponent.this.isEditable() && NComponent.this.isEnabled()) {
                            if (!triggerButton.isChecked()) {
                                triggerButton.toggleChecked();
                            }
                        }
                    }
                }));

                triggerButton.ensureDebugId(CompositeDebugId.debugId(baseDebugID, CCompDebugId.trigger));

            }
        }

        public ToggleButton getTriggerButton() {
            return triggerButton;
        }

        protected GroupFocusHandler getGroupFocusHandler() {
            return focusHandlerManager;
        }

        @Override
        public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
            return addDomHandler(handler, DoubleClickEvent.getType());
        }

        @Override
        protected void onEnsureDebugId(String baseID) {
            baseDebugID = baseID;
            //super.onEnsureDebugId(baseID);
            ((Widget) NComponent.this.getEditor()).ensureDebugId(baseID);
        }
    }

    class ViewerHolder extends HorizontalPanel {

        private Button actionButton;

        public ViewerHolder(Button actionButton) {
            super();

            setStyleName(DefaultCComponentsTheme.StyleName.EditorHolder.name());

            NComponent.this.getViewer().asWidget().setWidth("100%");
            add(NComponent.this.getViewer());
            setCellWidth(NComponent.this.getViewer(), "100%");
            setCellVerticalAlignment(NComponent.this.getViewer(), ALIGN_MIDDLE);

            setActionButton(actionButton);

        }

        public void setActionButton(final Button actionButton) {
            if (this.actionButton != null) {
                remove(this.actionButton);
            }

            this.actionButton = actionButton;

            if (actionButton != null) {
                add(actionButton);
                setCellVerticalAlignment(actionButton, ALIGN_MIDDLE);

                String marginTop;
                if (BrowserType.isFirefox()) {
                    marginTop = "0";
                } else if (BrowserType.isIE()) {
                    marginTop = "1";
                } else {
                    //Chrome and Safari
                    marginTop = "2";
                }
                DOM.setStyleAttribute(actionButton.getElement(), "marginTop", marginTop);
                DOM.setStyleAttribute(actionButton.getElement(), "marginLeft", "4px");

            }

        }

        public Button getActionButton() {
            return actionButton;
        }

    }
}
