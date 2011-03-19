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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CListBox.AsyncOptionsReadyCallback;

public class CComboBox<E> extends CEditableComponent<E, NativeComboBox<E>> implements HasOptionsChangeHandlers<List<E>>, IAcceptText {

    private List<E> options;

    private String noSelectionText = "";

    private NotInOptionsPolicy policy;

    public enum NotInOptionsPolicy {
        KEEP, DISCARD;
    }

    public CComboBox() {
        this(null);
    }

    public CComboBox(String title) {
        this(title, null);
    }

    public CComboBox(String title, boolean mandatory) {
        this(title);
        this.setMandatory(mandatory);
    }

    public CComboBox(String title, NotInOptionsPolicy policy) {
        super(title);
        setWidth("100%");
        if (policy == null) {
            this.policy = NotInOptionsPolicy.KEEP;
        } else {
            this.policy = policy;
        }
    }

    @Override
    protected NativeComboBox<E> createWidget() {
        NativeComboBox<E> nativeComboBox = new NativeComboBox<E>(this);
        nativeComboBox.setOptions(options);
        return nativeComboBox;
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
                    if (isValuesEquals(this.get(i), (E) o)) {
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
        if (isWidgetCreated()) {
            asWidget().setOptions(options);
        }
        OptionsChangeEvent.fire(this, getOptions());
    }

    public List<E> getOptions() {
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
        if (isWidgetCreated() && (options.contains(opt))) {
            asWidget().removeOption(opt);
        }
        options.remove(opt);
        if (isValuesEquals(getValue(), opt)) {
            setValue(null);
        }
    }

    public void updateOption(E opt) {
        if (options == null) {
            options = createOptionsImpl();
        }
        if (options.contains(opt)) {
            if (isWidgetCreated()) {
                asWidget().refreshOption(opt);
            }
        } else {
            options.add(opt);
            if (isWidgetCreated()) {
                asWidget().setOptions(options);
            }
        }
    }

    public void refreshOption(E opt) {
        if (isWidgetCreated()) {
            asWidget().refreshOption(opt);
        }
    }

    public String getItemName(E o) {
        if (o == null) {
            return this.noSelectionText;
        } else {
            return o.toString();
        }
    }

    @Override
    public void setMandatory(boolean mandatory) {
        if (isMandatory() != mandatory) {
            super.setMandatory(mandatory);
            if (isWidgetCreated()) {
                asWidget().refreshOptions();
            }
        }
    }

    public String getNoSelectionText() {
        return this.noSelectionText;
    }

    public void setNoSelectionText(String noSelectionText) {
        this.noSelectionText = noSelectionText;
        if (isWidgetCreated()) {
            asWidget().refreshOptions();
        }
    }

    public NotInOptionsPolicy getPolicy() {
        return this.policy;
    }

    @Deprecated
    public void setValueByItemName(String name) {
        setValueByString(name);
    }

    @Override
    public void setValueByString(final String name) {
        if (name == null && !isMandatory()) {
            setValue(null);
        } else {
            retriveOptions(new AsyncOptionsReadyCallback<E>() {
                @Override
                public void onOptionsReady(List<E> opt) {
                    for (E o : getOptions()) {
                        if (getItemName(o).equals(name)) {
                            setValue(o);
                            break;
                        }
                    }
                }
            });
        }
    }

}
