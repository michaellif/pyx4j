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
import java.util.Collection;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.suggest.SelectorListBox;

public class NSelectorListBox<E extends IEntity> extends NFocusField<Collection<E>, SelectorListBox<E>, CSelectorListBox<E>, HTML> implements IWatermarkWidget {

    public NSelectorListBox(final CSelectorListBox<E> cSuggestBox) {
        super(cSuggestBox);
    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @Override
    protected SelectorListBox<E> createEditor() {
        return new SelectorListBox<E>(getCComponent().getOptionsGrabber(), new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return getCComponent().getFormatter().format(value);
            }
        }, new IFormatter<E, String[]>() {

            @Override
            public String[] format(E value) {
                return getCComponent().getOptionPathFormatter().format(value);
            }
        });
    }

    @Override
    public void setNativeValue(Collection<E> value) {
        if (isViewable()) {
            StringBuilder text = new StringBuilder();
            for (E val : value) {
                if (text.length() == 0) {
                    text.append(getCComponent().getFormatter().format(val));
                } else {
                    text.append(",").append(getCComponent().getFormatter().format(val));
                }
            }
            getViewer().setText(text.toString());
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public Collection<E> getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

    @Override
    public void setWatermark(String watermark) {
        if (getEditor() instanceof IWatermarkWidget) {
            ((IWatermarkWidget) getEditor()).setWatermark(watermark);
        }
    }

    @Override
    public String getWatermark() {
        if (getEditor() instanceof IWatermarkWidget) {
            return ((IWatermarkWidget) getEditor()).getWatermark();
        } else {
            return null;
        }
    }
}
