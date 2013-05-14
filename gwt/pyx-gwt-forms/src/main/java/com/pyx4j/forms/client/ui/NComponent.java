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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
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

    private EditorPanel editorPanel;

    private ToggleButton triggerButton;

    private ViewerPanel viewerPanel;

    private Button actionButton;

    private Command navigationCommand;

    public NComponent(CCOMP cComponent) {
        super();
        this.cComponent = cComponent;

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

    class EditorPanel extends DockPanel implements HasDoubleClickHandlers {

        private final GroupFocusHandler focusHandlerManager;

        private final Set<HandlerRegistration> triggerButtonHandlerRegistrations = new HashSet<HandlerRegistration>();

        private String baseDebugID;

        private final SimplePanel triggerButtonHolder;

        public EditorPanel() {
            super();
            setStyleName(DefaultCComponentsTheme.StyleName.EditorPanel.name());
            setWidth("100%");

            SimplePanel editorHolder = new SimplePanel();
            editorHolder.setWidth("100%");
            editorHolder.addStyleName(DefaultCComponentsTheme.StyleName.Editor.name());
            editorHolder.setWidget(editor);
            add(editorHolder, DockPanel.CENTER);
            setCellVerticalAlignment(editorHolder, DockPanel.ALIGN_MIDDLE);

            focusHandlerManager = new GroupFocusHandler(this);

            if (editor instanceof NFocusComponent) {
                ((NFocusComponent<?, ?, ?, ?>) editor).addFocusHandler(focusHandlerManager);
                ((NFocusComponent<?, ?, ?, ?>) editor).addBlurHandler(focusHandlerManager);
            }

            triggerButtonHolder = new SimplePanel();
            add(triggerButtonHolder, DockPanel.EAST);

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

    class ViewerPanel extends DockPanel {

        private HandlerRegistration navigationCommandHandlerRegistration;

        private final SimplePanel viewerHolder;

        public ViewerPanel() {
            super();

            setStyleName(DefaultCComponentsTheme.StyleName.ViewerPanel.name());

            NComponent.this.getViewer().asWidget().setWidth("100%");

            viewerHolder = new SimplePanel();
            viewerHolder.addStyleName(DefaultCComponentsTheme.StyleName.Viewer.name());
            viewerHolder.setWidget(NComponent.this.getViewer());

            add(viewerHolder, DockPanel.CENTER);
            setCellVerticalAlignment(viewerHolder, DockPanel.ALIGN_MIDDLE);

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

                viewerHolder.addStyleDependentName(DefaultCComponentsTheme.StyleDependent.anchor.name());
            } else {
                viewerHolder.removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.anchor.name());
            }
        }

        public void setActionButton() {
            if (actionButton != null) {
                remove(actionButton);
            }

            if (actionButton != null) {
                add(actionButton, DockPanel.EAST);
            }

        }

    }
}
