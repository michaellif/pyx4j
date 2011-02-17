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

import java.util.EnumSet;

import com.pyx4j.forms.client.gwt.NativeRadioGroup;

public class CRadioGroup<E extends Enum<E>> extends CEditableComponent<E, NativeRadioGroup<E>> {

    public enum Layout {
        VERTICAL, HORISONTAL;
    }

    public enum YesNo {
        yes, no
    }

    private final Layout layout;

    private final Class<E> optionsClass;

    private String fieldname;

    public CRadioGroup(Class<E> optionsClass, Layout layout, String fieldname) {
        this(null, optionsClass, layout, fieldname);
    }

    public CRadioGroup(String title, Class<E> optionsClass, Layout layout, String fieldname) {
        super(title);
        this.layout = layout;
        this.optionsClass = optionsClass;
        this.fieldname = fieldname;
    }

    public CRadioGroup(Class<E> optionsClass, Layout layout) {
        this(null, optionsClass, layout, optionsClass.getName());
    }

    public Layout getLayout() {
        return layout;
    }

    public Class<E> getOptionsClass() {
        return optionsClass;
    }

    public EnumSet<E> getOptions() {
        return EnumSet.allOf(getOptionsClass());
    }

    @Override
    protected NativeRadioGroup<E> initWidget() {
        return new NativeRadioGroup<E>(this);
    }

    public String getFieldName() {
        return fieldname;
    }

}
