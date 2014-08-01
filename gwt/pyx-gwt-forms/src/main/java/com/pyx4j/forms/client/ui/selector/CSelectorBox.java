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
package com.pyx4j.forms.client.ui.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CTextFieldBase;

public class CSelectorBox<E extends IEntity> extends CTextFieldBase<E, NSelectorBox<E>> implements HasOptionsChangeHandlers<List<E>> {

    private List<E> options = new ArrayList<E>();

    private IFormatter<E, String[]> optionPathFormatter;

    public CSelectorBox() {
        super();

        setFormatter(new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return value.getStringView();
            }
        });

        setOptionPathFormatter(new IFormatter<E, String[]>() {

            @Override
            public String[] format(E value) {
                return null;
            }
        });

        setNativeComponent(new NSelectorBox<E>(this));
    }

    public IFormatter<E, String[]> getOptionPathFormatter() {
        return optionPathFormatter;
    }

    public void setOptionPathFormatter(IFormatter<E, String[]> formatter) {
        this.optionPathFormatter = formatter;
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    public void setOptions(Collection<E> opt) {
        this.options = new ArrayList<E>(opt);
        if (getNativeComponent() != null) {
            getNativeComponent().processOptions(options);
        }
    }

}
