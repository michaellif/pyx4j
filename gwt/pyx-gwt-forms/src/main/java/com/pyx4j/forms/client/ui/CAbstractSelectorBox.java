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
 * Created on Sep 16, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.suggest.IOptionsGrabber;

public abstract class CAbstractSelectorBox<DATA, TYPE extends IEntity, WIDGET extends INativeFocusField<DATA>> extends CFocusComponent<DATA, WIDGET> implements
        IAcceptsWatermark, HasOptionsChangeHandlers<List<TYPE>> {

    private IFormatter<TYPE, String> formatter;

    private String watermark;

    private final IOptionsGrabber<TYPE> optionsGrabber;

    private IFormatter<TYPE, String[]> optionPathFormatter;

    public CAbstractSelectorBox(IOptionsGrabber<TYPE> optionsGrabber) {
        this.optionsGrabber = optionsGrabber;

        setOptionPathFormatter(new IFormatter<TYPE, String[]>() {

            @Override
            public String[] format(TYPE value) {
                return null;
            }
        });
    }

    public void setFormatter(IFormatter<TYPE, String> formatter) {
        this.formatter = formatter;
    }

    public final IFormatter<TYPE, String> getFormatter() {
        if (formatter == null) {
            setFormatter(new IFormatter<TYPE, String>() {
                @Override
                public String format(TYPE value) {
                    if (value == null) {
                        return null;
                    } else {
                        return value.getStringView();
                    }
                }
            });
        }
        return formatter;
    }

    public IOptionsGrabber<TYPE> getOptionsGrabber() {
        return optionsGrabber;
    }

    public IFormatter<TYPE, String[]> getOptionPathFormatter() {
        return optionPathFormatter;
    }

    public void setOptionPathFormatter(IFormatter<TYPE, String[]> formatter) {
        this.optionPathFormatter = formatter;
    }

    @Override
    public void setWatermark(String watermark) {
        this.watermark = watermark;
        if (asWidget() instanceof IWatermarkWidget) {
            ((IWatermarkWidget) asWidget()).setWatermark(watermark);
        }
    }

    @Override
    public String getWatermark() {
        return watermark;
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<TYPE>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    @Override
    protected String getDebugInfo() {
        StringBuilder info = new StringBuilder(super.getDebugInfo());
        info.append("watermark").append("=").append(getWatermark()).append(";");
        return info.toString();
    }

    @Override
    protected void setEditorValue(DATA value) {
        // TODO Auto-generated method stub
        super.setEditorValue(value);
    }
}
