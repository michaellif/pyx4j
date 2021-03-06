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
 * Created on 2011-01-19
 * @author vlads
 */
package com.pyx4j.forms.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Native component value change event, (Real Time) as opposite to CComponent ValueChangeEvent
 */
public class NativeValueChangeEvent<T> extends GwtEvent<NativeValueChangeHandler<T>> {

    private static Type<NativeValueChangeHandler<?>> TYPE;

    public static <T> void fire(HasNativeValueChangeHandlers<T> source, T value) {
        if (TYPE != null) {
            NativeValueChangeEvent<T> event = new NativeValueChangeEvent<T>(value);
            source.fireEvent(event);
        }
    }

    public static Type<NativeValueChangeHandler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<NativeValueChangeHandler<?>>();
        }
        return TYPE;
    }

    private final T value;

    public NativeValueChangeEvent(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toDebugString() {
        return super.toDebugString() + getValue();
    }

    @Override
    protected void dispatch(NativeValueChangeHandler<T> handler) {
        handler.onNValueChange(this);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Type<NativeValueChangeHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

}
