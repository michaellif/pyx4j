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
 * Created on Feb 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.RadioGroup;

public class CRadioGroupEnum<E extends Enum<E>> extends CRadioGroup<E> {

    public CRadioGroupEnum(Class<E> optionsClass, RadioGroup.Layout layout) {
        this(optionsClass, EnumSet.allOf(optionsClass), layout);
    }

    public CRadioGroupEnum(Class<E> optionsClass, Collection<E> opt, RadioGroup.Layout layout) {
        super(layout);
        setFormat(new IFormatter<E>() {
            @Override
            public String format(E value) {
                if (value != null) {
                    return value.toString();
                } else {
                    return "";
                }
            }

        });
        setNativeComponent(new NRadioGroup<E>(this));
        super.setOptions(opt);

    }
}
