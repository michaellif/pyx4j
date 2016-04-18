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
 */
package com.pyx4j.forms.client.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.WhiteSpace;
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
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.HumanInputCommand;
import com.pyx4j.widgets.client.HumanInputInfo;
import com.pyx4j.widgets.client.IWidget;
import com.pyx4j.widgets.client.WidgetDebugId;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class NField<DATA_TYPE, EDITOR extends IWidget, CCOMP extends CField<DATA_TYPE, ?>, VIEWER extends Widget> extends NComponent<DATA_TYPE, CCOMP>
        implements INativeField<DATA_TYPE> {

    private EDITOR editor;

    private VIEWER viewer;

    private boolean viewable;

    private EditorPanel editorPanel;

    private ViewerPanel viewerPanel;

    private Button triggerButton;

    private Button clearButton;

    private Button actionButton;

    private Command navigationCommand;

    public NField(CCOMP cComponent) {
        super(cComponent);
    }

    @Override
    public final EDITOR getEditor() {
        return editor;
    }

    @Override
    public final VIEWER getViewer() {
        return viewer;
    }

    public void setTriggerButton(Button triggerButton) {
        this.triggerButton = triggerButton;
        if (editorPanel != null) {
            editorPanel.setTriggerButton();
        }
    }

    public Button getTriggerButton() {
        return triggerButton;
    }

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
        if (editorPanel != null) {
            editorPanel.setClearButton();
        }
    }

    /**
     * The button will be assigned DebugId from component
     */
    public void setActionButton(Button actionButton) {
        this.actionButton = actionButton;
        if (viewerPanel != null) {
            viewerPanel.initActionButton();
        }
    }

    @Override
    public void setNavigationCommand(Command navigationCommand) {
        this.navigationCommand = navigationCommand;
        if (viewerPanel != null) {
            viewerPanel.initNavigationCommand();
        }
    }

    protected abstract EDITOR createEditor();

    protected abstract VIEWER createViewer();

    protected void onEditorCreate() {
        setDebugId(getCComponent().getDebugId());
    }

    @Override
    public void init() {
        setViewable(getCComponent().isViewable());
    }

    protected void onEditorInit() {
        if (getCComponent().isPopulated()) {
            setNativeValue(getCComponent().getValue());
        }
    }

    protected void onViewerCreate() {
        setDebugId(getCComponent().getDebugId());
    }

    protected void onViewerInit() {
        if (getCComponent().isPopulated()) {
            setNativeValue(getCComponent().getValue());
        }
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
        if (!isViewable()) {
            editor.setEnabled(enabled);
            editorPanel.setEnabled(isEditable() && enabled);
            if (enabled) {
                editor.removeStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
            } else {
                editor.addStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
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
        if (!isViewable()) {
            editor.setEditable(editable);
            editorPanel.setEnabled(isEnabled() && editable);
            if (editable) {
                editor.removeStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
            } else {
                editor.addStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
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
            editor.setDebugId(debugId);
        }
        if (viewer != null) {
            viewer.ensureDebugId(debugId.debugId());
        }

        if (getTriggerButton() != null) {
            getTriggerButton().setDebugId(new CompositeDebugId(debugId, CCompDebugId.Triger));
        }
        if (actionButton != null) {
            getTriggerButton().setDebugId(new CompositeDebugId(debugId, CCompDebugId.Action));
        }
    }

    class EditorPanel extends FlowPanel implements HasDoubleClickHandlers {

        private final GroupFocusHandler groupFocusHandler;

        private final Set<HandlerRegistration> triggerButtonHandlerRegistrations = new HashSet<HandlerRegistration>();

        private final Set<HandlerRegistration> clearButtonHandlerRegistrations = new HashSet<HandlerRegistration>();

        private String baseDebugID;

        private final SimplePanel triggerButtonHolder;

        private final SimplePanel clearButtonHolder;

        private final SimplePanel editorHolder;

        public EditorPanel() {
            super();
            setStyleName(CComponentTheme.StyleName.FieldEditorPanel.name());
            getElement().getStyle().setProperty("display", "table");
            setWidth("100%");

            editorHolder = new SimplePanel();
            editorHolder.getStyle().setWhiteSpace(WhiteSpace.NORMAL);
            editorHolder.getStyle().setProperty("display", "table-cell");
            editorHolder.setWidth("100%");
            add(editorHolder);

            groupFocusHandler = new GroupFocusHandler(this);

            if (editor instanceof NFocusField) {
                ((NFocusField<?, ?, ?, ?>) editor).addFocusHandler(groupFocusHandler);
                ((NFocusField<?, ?, ?, ?>) editor).addBlurHandler(groupFocusHandler);
            }

            { // Trigger Button
                triggerButtonHolder = new SimplePanel();
                triggerButtonHolder.getStyle().setProperty("display", "table-cell");
                triggerButtonHolder.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

                add(triggerButtonHolder);

                setTriggerButton();
            }

            { // Clear Button
                clearButtonHolder = new SimplePanel();
                clearButtonHolder.getStyle().setProperty("display", "table-cell");
                clearButtonHolder.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

                add(clearButtonHolder);

                setClearButton();
            }

        }

        public void setEnabled(boolean enabled) {
            if (triggerButton != null) {
                triggerButton.setEnabled(enabled);
            }
            if (clearButton != null) {
                clearButton.setEnabled(enabled);
            }
        }

        @Override
        protected void onLoad() {
            editorHolder.setWidget(editor);
            super.onLoad();
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

                triggerButtonHandlerRegistrations.add(triggerButton.addFocusHandler(groupFocusHandler));
                triggerButtonHandlerRegistrations.add(triggerButton.addBlurHandler(groupFocusHandler));

                triggerButtonHandlerRegistrations.add(triggerButton.addKeyDownHandler(new KeyDownHandler() {

                    @Override
                    public void onKeyDown(KeyDownEvent event) {
                        switch (event.getNativeKeyCode()) {
                        case KeyCodes.KEY_TAB:
                        case KeyCodes.KEY_ESCAPE:
                        case KeyCodes.KEY_UP:
                            if (triggerButton.isActive()) {
                                triggerButton.toggleActive();
                            }
                            break;
                        case KeyCodes.KEY_DOWN:
                            if (!triggerButton.isActive()) {
                                triggerButton.toggleActive();
                            }
                            break;
                        }

                    }
                }));

                triggerButton.sinkEvents(Event.ONDBLCLICK);

                triggerButtonHandlerRegistrations.add(this.addDoubleClickHandler(new DoubleClickHandler() {
                    @Override
                    public void onDoubleClick(DoubleClickEvent event) {
                        if (NField.this.isEditable() && NField.this.isEnabled()) {
                            if (!triggerButton.isActive()) {
                                triggerButton.toggleActive();
                            }
                        }
                    }
                }));

                triggerButton.ensureDebugId(CompositeDebugId.debugId(baseDebugID, WidgetDebugId.trigger));
                triggerButtonHolder.setWidget(triggerButton);
            }
        }

        public void setClearButton() {
            if (clearButtonHolder.getWidget() != null) {
                clearButtonHolder.clear();
                for (HandlerRegistration handlerRegistration : clearButtonHandlerRegistrations) {
                    handlerRegistration.removeHandler();
                }
                clearButtonHandlerRegistrations.clear();
            }

            if (clearButton != null) {

                clearButtonHandlerRegistrations.add(clearButton.addFocusHandler(groupFocusHandler));
                clearButtonHandlerRegistrations.add(clearButton.addBlurHandler(groupFocusHandler));

                clearButton.ensureDebugId(CompositeDebugId.debugId(baseDebugID, WidgetDebugId.trigger));
                clearButton.getStyle().setDisplay(Display.BLOCK);
                clearButtonHolder.setWidget(clearButton);
            }
        }

        protected GroupFocusHandler getGroupFocusHandler() {
            return groupFocusHandler;
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

        private final SimplePanel viewerLabelHolder;

        private final Link viewerLinkHolder;

        private final SimplePanel actionButtonHolder;

        public ViewerPanel() {
            super();

            setStyleName(CComponentTheme.StyleName.FieldViewerPanel.name());
            getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
            setWidth("100%");

            viewerLabelHolder = new SimplePanel();
            viewerLabelHolder.getStyle().setWhiteSpace(WhiteSpace.NORMAL);
            viewerLabelHolder.setWidth("100%");
            viewerLabelHolder.getStyle().setDisplay(Display.INLINE_BLOCK);
            add(viewerLabelHolder);

            viewerLinkHolder = new Link();
            viewerLinkHolder.setWidth("100%");
            viewerLinkHolder.getStyle().setDisplay(Display.INLINE_BLOCK);
            add(viewerLinkHolder);

            actionButtonHolder = new SimplePanel();
            actionButtonHolder.getStyle().setDisplay(Display.INLINE_BLOCK);
            add(actionButtonHolder);

            initActionButton();

            initNavigationCommand();

        }

        @Override
        protected void onLoad() {
            if (navigationCommand != null) {
                viewerLinkHolder.setWidget(viewer);
            } else {
                viewerLabelHolder.setWidget(viewer);
            }
            super.onLoad();
        }

        public void initNavigationCommand() {
            if (navigationCommandHandlerRegistration != null) {
                navigationCommandHandlerRegistration.removeHandler();
            }
            NField.this.getViewer().sinkEvents(Event.ONCLICK);

            if (navigationCommand != null) {
                navigationCommandHandlerRegistration = addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        if (navigationCommand instanceof HumanInputCommand) {
                            ((HumanInputCommand) navigationCommand).execute(new HumanInputInfo(event));
                        } else {
                            navigationCommand.execute();
                        }

                    }
                }, ClickEvent.getType());

                viewerLabelHolder.setVisible(false);
                viewerLinkHolder.setWidget(viewer);
                viewerLinkHolder.setVisible(true);
            } else {
                viewerLinkHolder.setVisible(false);
                viewerLabelHolder.setWidget(viewer);
                viewerLabelHolder.setVisible(true);
            }
        }

        public void initActionButton() {
            if (actionButtonHolder != null) {
                actionButtonHolder.clear();
            }
            if (actionButton != null) {
                actionButtonHolder.setWidget(actionButton);
                actionButtonHolder.setVisible(true);
            } else {
                actionButtonHolder.setVisible(false);
            }
        }

        class Link extends SimplePanel {
            private static final String DEFAULT_HREF = "javascript:;";

            public Link() {
                super(DOM.createAnchor());
                AnchorElement.as(getElement()).setHref(DEFAULT_HREF);
                setStylePrimaryName(WidgetsTheme.StyleName.Anchor.name());
            }
        }

    }

    @Override
    public void showErrors(boolean show) {
        getCComponent().setVisited(show);
    }

}
