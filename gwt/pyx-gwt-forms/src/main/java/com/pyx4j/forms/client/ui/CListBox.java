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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.ICloneable;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox.AsyncOptionsReadyCallback;
import com.pyx4j.forms.client.validators.HasRequiredValueValidationMessage;

/**
 * A Multi-select collection box has two selection modes - single panel Classic style and two-panel Add-Remove style
 */
public class CListBox<E> extends CFocusComponent<List<E>, NListBox<E>> implements HasOptionsChangeHandlers<List<E>>, HasSelectionHandlers<E>,
        HasRequiredValueValidationMessage<E> {

    public enum SelectionMode {
        SINGLE_PANEL, TWO_PANEL
    }

    private final int visibleItemCount = 8;

    private final SelectionMode mode;

    private final NListBox<E> nativeListBox;

    private final ArrayList<E> options = new ArrayList<E>();

    private List<E> requiredValues;

    private Comparator<E> comparator = null;

    public CListBox() {
        this((String) null);
    }

    public CListBox(SelectionMode mode) {
        this(null, mode);
    }

    public CListBox(String title) {
        this(title, SelectionMode.SINGLE_PANEL);
    }

    public CListBox(String title, SelectionMode mode) {
        super(title);
        this.mode = mode;
        nativeListBox = new NListBox<E>(this);
        setNativeWidget(nativeListBox);
        asWidget().setWidth("100%");
        setVisibleItemCount(visibleItemCount);
    }

    public SelectionMode getSelectionMode() {
        return mode;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        nativeListBox.setVisibleItemCount(visibleItemCount);
    }

    public int getVisibleItemCount() {
        return nativeListBox.getVisibleItemCount();
    }

    @Override
    protected List<E> preprocessValue(List<E> value, boolean fireEvent, boolean populate) {
        if (value != null && getComparator() != null) {
            Collections.sort(value, getComparator());
        }
        return value;
    }

    /**
     * Clone each element of the list. Since the value may change and it is important for
     * isDirty().
     * 
     * Warning, does not Override super populateMutable
     */
    @SuppressWarnings("unchecked")
    public <T extends ICloneable<E>> void populateMutable(List<T> value) {
        populate((List<E>) value);
        //        if (value != null) {
        //            initValue = new FullyEqualArrayList(value);
        //        }
        throw new RuntimeException("TODO!!!!!!!!!!!!!!!");
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<E> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    public void setOptions(List<E> opt) {
        options.clear();
        if (opt != null) {
            if (getComparator() != null) {
                Collections.sort(opt, getComparator());
            }
            options.addAll(opt);
        }
        getWidget().refreshOptions();
        OptionsChangeEvent.fire(this, getOptions());
    }

    @SuppressWarnings("unchecked")
    public List<E> getOptions() {
        return (List<E>) options.clone();
    }

    /*
     * To be implemented by Async child
     */
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        if (callback != null) {
            callback.onOptionsReady(getOptions());
        }
    }

    int getOptionIndex(E item) {
        if (item == null) {
            return -1;
        }
        return options.indexOf(item);
    }

    public String getItemName(E o) {
        if (o == null) {
            return "-- null --";
        } else {
            return o.toString();
        }
    }

    public Comparator<E> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    public List<E> getRequiredValues() {
        return requiredValues;
    }

    public void setRequiredValues(List<E> requiredValues) {
        this.requiredValues = requiredValues;
    }

    @Override
    public String getValidationMessage(E value) {
        return "Required value \"" + getItemName(value) + "\" can't be removed";
    }

    public String format(Collection<E> value) {
        if (value == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (E item : value) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(getItemName(item));
            }
            return sb.toString();
        }
    }
}
