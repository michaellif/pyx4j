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

import java.text.ParseException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.events.HasNValueChangeHandlers;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public abstract class CTextFieldBase<DATA, WIDGET extends INativeTextComponent<DATA>> extends CTextComponent<DATA, WIDGET> implements IAcceptText,
        HasNValueChangeHandlers<String> {

    private IFormatter<DATA, String> formatter;

    private IParser<DATA> parser;

    public CTextFieldBase() {
        super();
        addComponentValidator(new TextBoxParserValidator<DATA>());
    }

    public void setFormatter(IFormatter<DATA, String> formatter) {
        this.formatter = formatter;
    }

    public final IFormatter<DATA, String> getFormatter() {
        if (formatter == null) {
            setFormatter(new IFormatter<DATA, String>() {
                @Override
                public String format(DATA value) {
                    if (value == null) {
                        return null;
                    } else {
                        return value.toString();
                    }
                }
            });
        }
        return formatter;
    }

    public final String format(DATA value) {
        String text = null;
        try {
            text = getFormatter().format(value);
        } catch (Exception ignore) {
        }
        return text == null ? "" : text;
    }

    public String getFormattedValue() {
        return format(getValue());
    }

    public final void setParser(IParser<DATA> parser) {
        this.parser = parser;
    }

    public final IParser<DATA> getParser() {
        return parser;
    }

    @Override
    public void setValueByString(String name) {
        try {
            setValue(getParser().parse(name));
        } catch (ParseException e) {
            // TODO : log something here?..
        }
    }

    public void requestFocus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                INativeField<DATA> impl = getNativeComponent();
                if (impl instanceof FocusWidget) {
                    ((FocusWidget) impl).setFocus(true);
                }
            }
        });
    }

    @Override
    public boolean isValueEmpty() {
        if (!CommonsStringUtils.isEmpty(getNativeComponent().getNativeText())) {
            return false;
        }
        return super.isValueEmpty() || ((getValue() instanceof String) && CommonsStringUtils.isEmpty((String) getValue()));
    }

    @Override
    public boolean isValuesEqual(DATA value1, DATA value2) {
        //This takes in consideration that 2 values can be null but actual state of component is not empty after failed parsing
        return value1 != null && value1 == value2;
    }

    /**
     * Native component value change event handler, (Real Time) as opposite to CComponent
     */
    @Override
    public HandlerRegistration addNValueChangeHandler(NValueChangeHandler<String> handler) {
        return addHandler(handler, NValueChangeEvent.getType());
    }
}
