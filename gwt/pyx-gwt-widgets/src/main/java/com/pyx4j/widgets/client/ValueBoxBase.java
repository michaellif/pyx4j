/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Aug 26, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.widgets.client.event.shared.HasPasteHandlers;
import com.pyx4j.widgets.client.event.shared.PasteEvent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public abstract class ValueBoxBase<E> extends Composite implements IValueWidget<E>, IFocusGroup, HasValueChangeHandlers<E>, HasPasteHandlers, IWatermarkWidget {

    private E value;

    private IParser<E> parser;

    private IFormatter<E, String> formatter;

    private boolean parsedOk = true;

    private String parseExceptionMessage;

    private com.google.gwt.user.client.ui.TextBoxBase textBoxWidget;

    private final SimplePanel textBoxHolder;

    private final FlowPanel contentPanel;

    private Button actionButton;

    private TextWatermark watermark;

    private IDebugId debugId;

    private final GroupFocusHandler groupFocusHandler;

    public ValueBoxBase() {
        contentPanel = new FlowPanel();
        contentPanel.setStyleName(WidgetTheme.StyleName.TextBoxContainer.name());
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        textBoxHolder = new SimplePanel();
        textBoxHolder.getElement().getStyle().setMarginRight(0, Unit.PX);

        contentPanel.add(textBoxHolder);

        sinkEvents(Event.ONPASTE);

        groupFocusHandler = new GroupFocusHandler(this);

        initWidget(contentPanel);

    }

    protected void setTextBoxWidget(final com.google.gwt.user.client.ui.TextBoxBase textBoxWidget) {
        assert this.textBoxWidget == null : "TextBox already set";
        this.textBoxWidget = textBoxWidget;
        if (this.debugId != null) {
            this.textBoxWidget.ensureDebugId(this.debugId.debugId());
        }
        textBoxWidget.setStyleName(WidgetTheme.StyleName.TextBox.name());
        if (textBoxWidget instanceof com.google.gwt.user.client.ui.TextBox) {
            contentPanel.addStyleDependentName(WidgetTheme.StyleDependent.singleLine.name());
        }

        textBoxWidget.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                contentPanel.addStyleDependentName(WidgetTheme.StyleDependent.focused.name());
            }
        });

        textBoxWidget.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                contentPanel.removeStyleDependentName(WidgetTheme.StyleDependent.focused.name());
            }
        });

        textBoxWidget.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                try {
                    setValue(getParser().parse(event.getValue()), true, null);
                } catch (ParseException e) {
                    setValue(null, false, e.getMessage());
                }
            }
        });

        textBoxHolder.setWidget(textBoxWidget);

        groupFocusHandler.addFocusable(textBoxWidget);

    }

    protected com.google.gwt.user.client.ui.TextBoxBase getTextBoxWidget() {
        return textBoxWidget;
    }

    @Override
    public E getValue() {
        return value;
    }

    @Override
    public void setValue(E value) {
        if (this.value == null && value == null) {
            return;
        } else if (this.value != null && this.value.equals(value)) {
            return;
        }
        this.parsedOk = true;
        this.value = value;
        textBoxWidget.setText(getFormatter().format(value));
        if (watermark != null) {
            watermark.show();
        }
    }

    protected void setValue(E value, boolean parsedOk, String parseExceptionMessage) {
        if (this.parsedOk == parsedOk) {
            if (this.value == null && value == null) {
                return;
            } else if (this.value != null && this.value.equals(value)) {
                return;
            }
        }
        this.value = value;
        this.parsedOk = parsedOk;
        if (parsedOk) {
            textBoxWidget.setText(getFormatter().format(value));
            this.parseExceptionMessage = null;
        } else {
            this.parseExceptionMessage = parseExceptionMessage;
        }
        if (watermark != null) {
            watermark.show();
        }
        ValueChangeEvent.fire(this, getValue());
    }

    @Override
    public void setParser(IParser<E> parser) {
        this.parser = parser;
    }

    protected IParser<E> getParser() {
        return parser;
    }

    @Override
    public void setFormatter(IFormatter<E, String> formatter) {
        this.formatter = formatter;
    }

    protected IFormatter<E, String> getFormatter() {
        return formatter;
    }

    @Override
    public boolean isParsedOk() {
        return parsedOk;
    }

    @Override
    public String getParseExceptionMessage() {
        return parseExceptionMessage;
    }

    @Override
    public GroupFocusHandler getGroupFocusHandler() {
        return groupFocusHandler;
    }

    public void setAction(Command command, ImageResource imageResource) {
        if (actionButton != null) {
            contentPanel.remove(actionButton);
        }
        if (command == null) {
            textBoxHolder.getElement().getStyle().setMarginRight(0, Unit.PX);
            actionButton = null;
        } else {
            actionButton = new Button(imageResource, command) {

                @Override
                protected void onAttach() {
                    super.onAttach();
                    textBoxHolder.getElement().getStyle().setMarginRight(actionButton.getOffsetWidth(), Unit.PX);
                }

            };
            actionButton.setEnabled(isEditable() && isEnabled());
            actionButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
            actionButton.getElement().getStyle().setTop(0, Unit.PX);
            actionButton.getElement().getStyle().setRight(0, Unit.PX);

            actionButton.setStyleName(WidgetTheme.StyleName.TextBoxActionButton.name());

            groupFocusHandler.addFocusable(actionButton);

            contentPanel.add(actionButton);

            if (this.debugId != null) {
                this.actionButton.ensureDebugId(CompositeDebugId.debugId(this.debugId, WidgetDebugId.trigger));
            }
        }

    }

    public boolean isActive() {
        if (actionButton != null) {
            return actionButton.isActive();
        } else {
            return false;
        }
    }

    public void toggleActive() {
        if (actionButton != null) {
            actionButton.toggleActive();
        }
    }

    @Override
    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = createWatermark();
        }
        watermark.setWatermark(text);
    }

    protected TextWatermark createWatermark() {
        return new TextWatermark(textBoxWidget) {

            @Override
            public String getText() {
                return textBoxWidget.getText();
            }

            @Override
            public void setText(String text) {
                textBoxWidget.setText(text);
            }
        };
    }

    @Override
    public String getWatermark() {
        return watermark.getWatermark();
    }

    @Override
    public void setEditable(boolean editable) {
        textBoxWidget.setReadOnly(!editable);
        if (actionButton != null) {
            actionButton.setEnabled(isEditable() && isEnabled());
        }
        contentPanel.setStyleDependentName(WidgetTheme.StyleDependent.readonly.name(), !editable);
    }

    @Override
    public boolean isEditable() {
        return !textBoxWidget.isReadOnly();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textBoxWidget.setEnabled(enabled);
        if (actionButton != null) {
            actionButton.setEnabled(isEditable() && isEnabled());
        }
        contentPanel.setStyleDependentName(WidgetTheme.StyleDependent.disabled.name(), !enabled);
    }

    @Override
    public boolean isEnabled() {
        return textBoxWidget.isEnabled();
    }

    @Override
    public void addStyleDependentName(String styleSuffix) {
        textBoxWidget.addStyleDependentName(styleSuffix);
    }

    @Override
    public void removeStyleDependentName(String styleSuffix) {
        textBoxWidget.removeStyleDependentName(styleSuffix);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        this.debugId = debugId;
        textBoxWidget.ensureDebugId(debugId.debugId());
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textBoxWidget.addChangeHandler(handler);
    }

    public void setNameProperty(String name) {
        textBoxWidget.setName(name);
    }

    @Override
    public int getTabIndex() {
        return textBoxWidget.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        textBoxWidget.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        textBoxWidget.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        textBoxWidget.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return groupFocusHandler.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return groupFocusHandler.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBoxWidget.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBoxWidget.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBoxWidget.addKeyPressHandler(handler);
    }

    @Override
    public HandlerRegistration addPasteHandler(PasteHandler handler) {
        return addHandler(handler, PasteEvent.getType());
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
        case Event.ONPASTE:
            event.stopPropagation();
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    PasteEvent.fire(ValueBoxBase.this);
                }
            });

            break;
        }
    }

}
