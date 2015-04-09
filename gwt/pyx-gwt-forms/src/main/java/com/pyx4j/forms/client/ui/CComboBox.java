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
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;

public class CComboBox<E> extends CFocusComponent<E, NComboBox<E>> implements HasOptionsChangeHandlers<List<E>>, IAcceptsText {

    public static interface AsyncOptionsReadyCallback<T> {
        public void onOptionsReady(List<T> opt);
    }

    private List<E> options;

    private NotInOptionsPolicy policy;

    private IFormatter<E, String> format;

    public enum NotInOptionsPolicy {
        KEEP, DISCARD;
    }

    public CComboBox() {
        this(null);
    }

    public CComboBox(boolean mandatory) {
        this();
        this.setMandatory(mandatory);
    }

    public CComboBox(NotInOptionsPolicy policy) {
        this(policy, new IFormatter<E, String>() {

            @Override
            public String format(E o) {
                if (o == null) {
                    return "";
                } else {
                    return o.toString();
                }

            }

        });
    }

    public CComboBox(NotInOptionsPolicy policy, IFormatter<E, String> format) {
        super();
        this.format = format;
        if (policy == null) {
            this.policy = NotInOptionsPolicy.KEEP;
        } else {
            this.policy = policy;
        }

        NComboBox<E> nativeComboBox = new NComboBox<E>(this);
        nativeComboBox.refreshOptions();
        setNativeComponent(nativeComboBox);
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    @SuppressWarnings("serial")
    private List<E> createOptionsImpl() {
        return new ArrayList<E>() {
            @SuppressWarnings("unchecked")
            @Override
            public int indexOf(Object o) {
                for (int i = 0; i < this.size(); i++) {
                    if (isValuesEqual(this.get(i), (E) o)) {
                        return i;
                    }
                }
                return -1;
            }
        };
    }

    public void setOptions(Collection<E> opt) {
        if (options == null) {
            options = createOptionsImpl();
        }
        options.clear();
        if (opt != null) {
            options.addAll(opt);
        }
        getNativeComponent().refreshOptions();
        OptionsChangeEvent.fire(this, getOptions());
    }

    public List<E> getOptions() {
        if (options == null) {
            options = createOptionsImpl();
        }
        return options;
    }

    /*
     * To be implemented by Async child
     */
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if (callback != null) {
            callback.onOptionsReady(getOptions());
        }
    }

    public void removeOption(E opt) {
        if (options.contains(opt)) {
            getNativeComponent().removeOption(opt);
        }
        options.remove(opt);
        if (isValuesEqual(getValue(), opt)) {
            setValue(null);
        }
    }

    public void updateOption(E opt) {
        if (options == null) {
            options = createOptionsImpl();
        }
        if (options.contains(opt)) {
            getNativeComponent().refreshOption(opt);
        } else {
            options.add(opt);
            getNativeComponent().refreshOptions();
        }
    }

    public void refreshOption(E opt) {
        getNativeComponent().refreshOption(opt);
    }

    public String getItemName(E o) {
        return format.format(o);
    }

    public void setFormat(IFormatter<E, String> format) {
        this.format = format;
        setValue(getValue(), false);
        getNativeComponent().refreshOptions();
    }

    @Override
    public void setMandatory(boolean mandatory) {
        if (isMandatory() != mandatory) {
            super.setMandatory(mandatory);
            getNativeComponent().refreshOptions();
        }
    }

    public NotInOptionsPolicy getPolicy() {
        return this.policy;
    }

    @Override
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        if (populate) {
            getNativeComponent().setPopulatedValue(value);
        }
    }

    @Override
    public void setValueByString(String name) {
        setValueByString(name, true, false);
    }

    public void setValueByString(final String name, final boolean fireEvent, final boolean populate) {
        if (name == null && !isMandatory()) {
            setValue(null, fireEvent, populate);
        } else {
            retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    for (E o : getOptions()) {
                        if (getItemName(o).equals(name)) {
                            setValue(o, fireEvent, populate);
                            break;
                        }
                    }
                }
            });
        }
    }
}
