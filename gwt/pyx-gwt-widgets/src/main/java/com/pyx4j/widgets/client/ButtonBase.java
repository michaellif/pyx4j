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
 * Created on Dec 4, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.pyx4j.gwt.commons.ui.HTML;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.concerns.AbstractConcern;
import com.pyx4j.gwt.commons.concerns.HasSecureConcernedChildren;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.gwt.commons.ui.HasStyle;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class ButtonBase extends FocusPanel implements HasWidgetConcerns, IFocusWidget, HasSecureConcernedChildren, HasStyle {

    private final HTML textLabel;

    private final FlowPanel imageHolder;

    private final ButtonFacesHandler facesHandler;

    private Command command;

    private boolean active = false;

    protected final List<AbstractConcern> concerns = new ArrayList<>();

    private final SecureConcernsHolder secureConcernsHolder = new SecureConcernsHolder();

    private String captionText;

    protected ButtonBase(ButtonFacesHandler facesHandler, String text, Command command, Permission... permission) {
        this.facesHandler = (facesHandler == null ? new ButtonFacesHandler() : facesHandler);
        this.command = command;

        this.facesHandler.init(this);

        textLabel = new HTML();
        setTextLabel(text);

        imageHolder = new FlowPanel();
        imageHolder.getStyle().setProperty("height", "100%");

        imageHolder.add(textLabel);

        setWidget(imageHolder);

        setPermission(permission);

        addClickHandlerPrivate(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                execute(new HumanInputInfo(event));
            }
        });
    }

    protected void execute(HumanInputInfo humanInputInfo) {
        if (isEnabled() && (command != null)) {
            active = !active;

            if (command instanceof HumanInputCommand) {
                ((HumanInputCommand) command).execute(humanInputInfo);
            } else {
                command.execute();
            }

            if (isActive()) {
                addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
            } else {
                removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());

            }
        }
    }

    protected HTML getTextLabel() {
        return textLabel;
    }

    public void setCommand(Command command) {
        this.command = command;
        applyVisibilityRules();
    }

    public Command getCommand() {
        return this.command;
    }

    private HandlerRegistration addClickHandlerPrivate(ClickHandler handler) {
        return super.addClickHandler(handler);
    }

    /**
     * @deprecated Use setCommand(new Command(){})
     */
    @Override
    @Deprecated
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        throw new UnsupportedOperationException();
    }

    public void setTextLabel(String label) {
        captionText = label;
        textLabel.setHTML(label);
        textLabel.setVisible(label != null);
    }

    protected HTML getTextLabelComponent() {
        return textLabel;
    }

    public void setCaption(String text) {
        captionText = text;
        textLabel.setText(text);
    }

    public String getCaption() {
        return captionText;
    }

    public void setTooltip(String text) {
        setTitle(text);
    }

    protected FlowPanel getImageHolder() {
        return imageHolder;
    }

    protected void updateImageState() {
        getImageHolder().getStyle().setProperty("paddingLeft", "0px");
        getImageHolder().getStyle().setProperty("background", "none");
    }

    // --- concerns implementation - start

    @Override
    public List<AbstractConcern> concerns() {
        return concerns;
    }

    @Override
    public SecureConcernsHolder secureConcernsHolder() {
        return secureConcernsHolder;
    }

    @Override
    public void applyVisibilityRules() {
        if (this.isAttached()) {
            super.setVisible(HasWidgetConcerns.super.isVisible());
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        applyConcernRules();
    }

    @Override
    public void setVisible(boolean visible) {
        setConcernsVisible(visible);
    }

    // Historic method to avoid refactoring
    public void setPermission(Permission... permission) {
        setVisibilityPermission(permission);
    }

    @Override
    public void applyEnablingRules() {
        boolean enabled = HasWidgetConcerns.super.isEnabled();
        getElement().setPropertyBoolean("disabled", !enabled);
        facesHandler.setEnabled(enabled);
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        HasWidgetConcerns.super.setSecurityContext(context);
        HasSecureConcernedChildren.super.setSecurityContext(context);
    }

    @Override
    public void inserConcernedParent(AbstractConcern parentConcern) {
        HasWidgetConcerns.super.inserConcernedParent(parentConcern);
        HasSecureConcernedChildren.super.inserConcernedParent(parentConcern);
    }

    @Override
    public void applyConcernRules() {
        HasWidgetConcerns.super.applyConcernRules();
        HasSecureConcernedChildren.super.applyConcernRules();
    }

    // --- concerns implementation - end

    // This is used for Toggle buttons
    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        this.fireEvent(new ClickEvent() {
        });
        facesHandler.setActive(isActive());
    }

    public void click() {
        DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), this);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        if (textLabel != null) {
            textLabel.ensureDebugId(baseID);
        }
    }

    @Override
    protected void onUnload() {
        facesHandler.onUnload();
    }

    /**
     * Not implemented in button
     */
    @Override
    @Deprecated
    public void setEditable(boolean editable) {
    }

    /**
     * Not implemented in button
     */
    @Override
    @Deprecated
    public boolean isEditable() {
        return false;
    }

    class ButtonFacesHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler, ClickHandler {

        private ButtonBase button;

        private boolean mouseOver = false;

        public ButtonFacesHandler() {
        }

        void init(ButtonBase button) {
            this.button = button;
            button.addMouseOverHandler(this);
            button.addMouseOutHandler(this);
            button.addMouseDownHandler(this);
            button.addMouseUpHandler(this);
            button.addClickHandlerPrivate(this);

        }

        public void setEnabled(boolean enabled) {
            if (button == null) {
                return;
            }
            if (enabled) {
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
                if (mouseOver) {
                    onMouseOver(null);
                }
            } else {
                button.addStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
                // IE8: fix for Buttons remain in Mouse Over position after they are clicked in filter
                mouseOver = false;
            }
        }

        public void setActive(boolean active) {
            if (button == null) {
                return;
            }
            if (button.isEnabled() && active) {
                button.addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
            } else {
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            mouseOver = true;
            if (button.isEnabled()) {
                button.addStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            mouseOver = false;
            if (button.isEnabled()) {
                button.removeStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
            }
        }

        public void onUnload() {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            mouseOver = false;
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (button.isEnabled()) {
                if (!button.isActive()) {
                    button.removeStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
                    button.addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                }
            }
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (button.isEnabled()) {
                if (button.isActive()) {
                    button.addStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                } else {
                    button.removeStyleDependentName(WidgetsTheme.StyleDependent.active.name());
                    button.addStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
                }
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            button.removeStyleDependentName(WidgetsTheme.StyleDependent.hover.name());
        }

        public ButtonBase getButton() {
            return button;
        }

    }

}
