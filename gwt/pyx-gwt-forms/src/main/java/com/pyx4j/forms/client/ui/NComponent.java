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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IWidget;
import com.pyx4j.widgets.client.ToggleButton;

public abstract class NComponent<DATA, WIDGET extends IWidget, CCOMP extends CComponent<DATA, ?>, VIEWER extends Widget> extends SimplePanel implements
        INativeComponent<DATA> {

    private WIDGET editor;

    private VIEWER viewer;

    private final CCOMP cComponent;

    private boolean viewable;

    private EditorPanel editorPanel;

    private ToggleButton triggerButton;

    private ViewerPanel viewerPanel;

    private Button actionButton;

    private Command navigationCommand;

    public NComponent(CCOMP cComponent) {
        super();
        this.cComponent = cComponent;
        setWidth(cComponent.getWidth());
    }

    public final WIDGET getEditor() {
        return editor;
    }

    public final VIEWER getViewer() {
        return viewer;
    }

    public void setTriggerButton(ToggleButton triggerButton) {
        this.triggerButton = triggerButton;
        if (editorPanel != null) {
            editorPanel.setTriggerButton();
        }
    }

    public void setActionButton(Button actionButton) {
        this.actionButton = actionButton;
        if (viewerPanel != null) {
            viewerPanel.setActionButton();
        }
    }

    @Override
    public void setNavigationCommand(Command navigationCommand) {
        this.navigationCommand = navigationCommand;
        if (viewerPanel != null) {
            viewerPanel.setNavigationCommand();
        }
    }

    @Override
    public CCOMP getCComponent() {
        return cComponent;
    }

    protected abstract WIDGET createEditor();

    protected abstract VIEWER createViewer();

    protected void onEditorCreate() {
        getEditor().setWidth("100%");
        setDebugId(getCComponent().getDebugId());
    }

    protected void onEditorInit() {
        setNativeValue(cComponent.getValue());
    }

    protected void onViewerCreate() {
        getViewer().setWidth("100%");
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
                viewerPanel = new ViewerPanel();
            }
            onViewerInit();
            setWidget(viewerPanel);
        } else {
            if (editor == null) {
                editor = createEditor();
                onEditorCreate();
                editorPanel = new EditorPanel();
            }
            onEditorInit();
            setWidget(editorPanel);
        }
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        if (editorPanel != null) {
            return editorPanel.getGroupFocusHandler();
        } else {
            return null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (triggerButton != null) {
            triggerButton.setEnabled(isEditable() && enabled);
        }

        if (editor != null) {
            editor.setEnabled(enabled);
            if (enabled) {
                editor.removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            } else {
                editor.addStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (isViewable()) {
            return false;
        } else {
            return editor.isEnabled();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        if (triggerButton != null) {
            triggerButton.setEnabled(isEnabled() && editable);
        }

        if (editor != null) {
            editor.setEditable(editable);
            if (editable) {
                editor.removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            } else {
                editor.addStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            }
        }
    }

    @Override
    public boolean isEditable() {
        if (isViewable()) {
            return false;
        } else {
            return editor.isEditable();
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO remove this, or make configurable
        if (debugId == null) {
            return;
        }

        assert (debugId != null) : "Unassigned DebugId in native component of " + getCComponent().shortDebugInfo();
        if (editor != null) {
            editor.ensureDebugId(debugId.debugId());
        }
        if (viewer != null) {
            viewer.ensureDebugId(debugId.debugId());
        }
    }

    class EditorPanel extends FlowPanel implements HasDoubleClickHandlers {

        private final GroupFocusHandler focusHandlerManager;

        private final Set<HandlerRegistration> triggerButtonHandlerRegistrations = new HashSet<HandlerRegistration>();

        private String baseDebugID;

        private final SimplePanel triggerButtonHolder;

        public EditorPanel() {
            super();
            setStyleName(DefaultCComponentsTheme.StyleName.EditorPanel.name());
            getElement().getStyle().setProperty("display", "table");
            setWidth("100%");

            SimplePanel editorHolder = new SimplePanel();
            editorHolder.setWidth("100%");
            editorHolder.getElement().getStyle().setProperty("display", "table-cell");
            editorHolder.setWidget(editor);
            add(editorHolder);

            focusHandlerManager = new GroupFocusHandler(this);

            if (editor instanceof NFocusComponent) {
                ((NFocusComponent<?, ?, ?, ?>) editor).addFocusHandler(focusHandlerManager);
                ((NFocusComponent<?, ?, ?, ?>) editor).addBlurHandler(focusHandlerManager);
            }

            triggerButtonHolder = new SimplePanel();
            triggerButtonHolder.getElement().getStyle().setProperty("display", "table-cell");
            triggerButtonHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

            add(triggerButtonHolder);

            setTriggerButton();
        }

        public void setTriggerButton() {
            if (triggerButtonHolder.getWidget() != null) {
                triggerButtonHolder.clear();
                for (HandlerRegistration handlerRegistration : triggerButtonHandlerRegistrations) {
                    handlerRegistration.removeHandler();
                }
                triggerButtonHandlerRegistrations.clear();
            }

            if (triggerButton != null) {

                triggerButtonHandlerRegistrations.add(triggerButton.addFocusHandler(focusHandlerManager));
                triggerButtonHandlerRegistrations.add(triggerButton.addBlurHandler(focusHandlerManager));

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
                triggerButtonHolder.setWidget(triggerButton);
            }
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
            ((Widget) editor).ensureDebugId(baseID);
        }
    }

    class ViewerPanel extends FlowPanel {

        private HandlerRegistration navigationCommandHandlerRegistration;

        private final Link viewerHolder;

        private final SimplePanel actionButtonHolder;

        public ViewerPanel() {
            super();

            setStyleName(DefaultCComponentsTheme.StyleName.ViewerPanel.name());
            getElement().getStyle().setProperty("display", "table");
            setWidth("100%");

            viewerHolder = new Link();
            viewerHolder.setWidth("100%");
            viewerHolder.getElement().getStyle().setProperty("display", "table-cell");
            viewerHolder.setWidget(viewer);
            add(viewerHolder);

            actionButtonHolder = new SimplePanel();
            actionButtonHolder.getElement().getStyle().setProperty("display", "table-cell");
            add(actionButtonHolder);

            setActionButton();

            setNavigationCommand();

        }

        public void setNavigationCommand() {
            if (navigationCommandHandlerRegistration != null) {
                navigationCommandHandlerRegistration.removeHandler();
            }
            NComponent.this.getViewer().sinkEvents(Event.ONCLICK);

            if (navigationCommand != null) {
                navigationCommandHandlerRegistration = addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        navigationCommand.execute();

                    }
                }, ClickEvent.getType());

                viewerHolder.setEnabled(true);
            } else {
                viewerHolder.setEnabled(false);
            }
        }

        public void setActionButton() {
            if (actionButtonHolder != null) {
                actionButtonHolder.clear();
            }
            if (actionButton != null) {
                actionButtonHolder.setWidget(actionButton);
            }
        }

        class Link extends SimplePanel {
            private static final String DEFAULT_HREF = "javascript:;";

            public Link() {
                super(DOM.createAnchor());
                AnchorElement.as(getElement()).setHref(DEFAULT_HREF);
                setStylePrimaryName(DefaultWidgetsTheme.StyleName.Anchor.name());
                setEnabled(false);
            }

            public void setEnabled(boolean enabled) {
                DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);
                if (enabled) {
                    removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                } else {
                    addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                }
            }

        }

    }
}
