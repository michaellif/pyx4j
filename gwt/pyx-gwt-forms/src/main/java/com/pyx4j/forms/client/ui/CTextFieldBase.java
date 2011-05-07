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
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.events.PropertyChangeEvent;

public abstract class CTextFieldBase<DATA_TYPE, WIDGET_TYPE extends Widget & INativeTextComponent<DATA_TYPE>> extends CTextComponent<DATA_TYPE, WIDGET_TYPE>
        implements IAcceptText {

    private IFormat<DATA_TYPE> format;

    public CTextFieldBase(String title) {
        super(title);
        setWidth("100%");
    }

    public CTextFieldBase() {
        this(null);
    }

    public void setFormat(IFormat<DATA_TYPE> format) {
        this.format = format;
    }

    public IFormat<DATA_TYPE> getFormat() {
        return format;
    }

    @Override
    public void setValue(DATA_TYPE value) {
        if (getValue() == null && value == null) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
        } else {
            super.setValue(value);
        }
    }

    @Override
    public void setValueByString(String name) {
        try {
            setValue(getFormat().parse(name));
        } catch (ParseException e) {
            // TODO : log something here?..
        }
    }

    public void requestFocus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                INativeEditableComponent<DATA_TYPE> impl = asWidget();
                if (impl instanceof FocusWidget) {
                    ((FocusWidget) impl).setFocus(true);
                }
            }
        });
    }

    @Override
    public boolean isValueEmpty() {
        if (isWidgetCreated()) {
            if (!CommonsStringUtils.isEmpty(asWidget().getNativeText())) {
                return false;
            }
        }
        return super.isValueEmpty() || ((getValue() instanceof String) && CommonsStringUtils.isEmpty((String) getValue()));
    }

    public boolean isParsedSuccesfully() {
        if (isWidgetCreated()) {
            return asWidget().isParsedSuccesfully();
        }
        return true;
    }

    @Override
    public void onEditingStop() {
        super.onEditingStop();

        if (isParsedSuccesfully()) {
            setNativeValue(getValue());
        }
    }

    @Override
    protected boolean update(DATA_TYPE value) {
        boolean res = super.update(value);
        if (!res) {
            revalidate(); // let TextBoxParserValidator to work!..
        }
        return res;
    }
}
