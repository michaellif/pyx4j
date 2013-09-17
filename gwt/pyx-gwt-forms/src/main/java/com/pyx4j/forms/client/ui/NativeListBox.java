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
import java.util.Comparator;
import java.util.List;

import com.pyx4j.widgets.client.ListBox;

public class NativeListBox<E> extends ListBox implements INativeListBox<E> {

    private final INativeListBox<E> implDelegate;

    private List<E> options;

    public NativeListBox(int visibleItems, INativeListBox<E> implDelegate) {
        super(true); // enable multiselect
        this.implDelegate = implDelegate;
        setVisibleItemCount(visibleItems);
    }

    @Override
    public void setOptions(Collection<E> options) {
        clear();
        this.options = new ArrayList<E>(options);
        if (options != null) {
            for (E item : options) {
                addItem(getItemName(item));
            }
        }
    }

    @Override
    public void setNativeValue(Collection<E> value) {
        setSelectedIndex(-1);
        if (value != null && options != null) {
            for (E v : value) {
                setItemSelected(options.indexOf(v), true);
            }
        }
    }

    @Override
    public List<E> getNativeValue() {
        List<E> selection = new ArrayList<E>();
        if (options != null) {
            for (int idx = 0; idx < options.size(); idx++) {
                if (this.isItemSelected(idx)) {
                    selection.add(options.get(idx));
                }
            }
        }
        return selection;
    }

    @Override
    public String getItemName(E item) {
        return implDelegate.getItemName(item);
    }

    @Override
    public void onNativeValueChange(Collection<E> values) {
        implDelegate.onNativeValueChange(values);
    }

    @Override
    public String itemCannotBeRemovedMessage(E item) {
        return implDelegate.itemCannotBeRemovedMessage(item);
    }

    @Override
    public Comparator<E> getComparator() {
        return implDelegate.getComparator();
    }
}