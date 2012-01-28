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
 * Created on 2010-04-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Collection;

import com.pyx4j.widgets.client.RadioGroup;

public abstract class CRadioGroup<E> extends CFocusComponent<E, NRadioGroup<E>> {

    private final RadioGroup.Layout layout;

    private IFormat<E> format;

    public CRadioGroup(RadioGroup.Layout layout) {
        this(null, layout);
    }

    public CRadioGroup(String title, RadioGroup.Layout layout) {
        super(title);
        this.layout = layout;
    }

    public RadioGroup.Layout getLayout() {
        return layout;
    }

    public abstract Collection<E> getOptions();

    public IFormat<E> getFormat() {
        return format;
    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    @Override
    protected NRadioGroup<E> createWidget() {
        return new NRadioGroup<E>(this);
    }

}
