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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.ICloneable;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.gwt.ListSelectionPopup;
import com.pyx4j.forms.client.gwt.NativeListBox;
import com.pyx4j.forms.client.validators.HasRequiredValueValidationMessage;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

//TODO add another CLIstBox impl for NativeInLineListBox (make CListBoxBase)
public class CListBox<E> extends CEditableComponent<List<E>, NativeListBox<E>> implements HasOptionsChangeHandlers<List<E>>, HasSelectionHandlers<E>,
        HasRequiredValueValidationMessage<E> {

    public static enum Layout {
        PLAIN, TRIGGERED, INLINE;
    }

    public static class ListBoxDisplayProperties {

        public boolean multipleSelect = true;

        public int visibleItemCount = 8;
    }

    public static interface AsyncOptionsReadyCallback<T> {

        public void onOptionsReady(List<T> opt);

    }

    private ListBoxDisplayProperties displayProperties;

    private Layout layout;

    private INativeListBox<E> nativeListBox;

    private final ArrayList<E> options = new ArrayList<E>();

    private List<E> requiredValues;

    private Comparator<E> comparator = null;

    private ListSelectionPopup<E> pop;

    public CListBox() {
        this(null, Layout.TRIGGERED);
    }

    public CListBox(String title, Layout layout) {
        super(title);
        this.layout = layout;
        this.displayProperties = new ListBoxDisplayProperties();
        setWidth("100%");
    }

    /**
     * Enable multiple selections in view.
     * 
     * @param multipleSelect
     *            specifies if multiple selection is enabled
     */
    public void setMultipleSelect(boolean multipleSelect) {
        this.displayProperties.multipleSelect = multipleSelect;
        if (nativeListBox != null) {
            nativeListBox.setDisplayProperties(displayProperties);
        }
    }

    public void setVisibleItemCount(int visibleItemCount) {
        this.displayProperties.visibleItemCount = visibleItemCount;
        if (nativeListBox != null) {
            nativeListBox.setDisplayProperties(displayProperties);
        }
    }

    @Override
    protected NativeListBox<E> initWidget() {
        NativeListBox<E> nativeListBox = new NativeListBox<E>(this, displayProperties);
        if (layout == Layout.PLAIN) {
            (nativeListBox).setTrigger(false);
        }
        return nativeListBox;
    }

    @Override
    public void populate(List<E> value) {
        // sort
        if (value != null) {
            if (getComparator() != null) {
                Collections.sort(value, getComparator());
            }
        }
        super.populate(value);
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
    public void setValue(List<E> value) {
        if (value != null) {
            if (getComparator() != null) {
                Collections.sort(value, getComparator());
            }
        }
        super.setValue(value);
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

    public void removeValue(E v) {
        E selected = getSelected();
        if (nativeListBox != null) {
            nativeListBox.removeItem(getValueIndex(v));
        }
        getValue().remove(v);
        if (EqualsHelper.equals(selected, v)) {
            setSelected(null);
        }
    }

    public void refreshItem(E v) {
        if ((v != null) && (nativeListBox != null)) {
            nativeListBox.refreshItem(getValueIndex(v));
        }
    }

    int getOptionIndex(E item) {
        if (item == null) {
            return -1;
        }
        return options.indexOf(item);
    }

    int getValueIndex(E item) {
        if ((item == null) || (getValue() == null)) {
            return -1;
        }
        return getValue().indexOf(item);
    }

    public void onTrigger(boolean show) {
        if (!show) {
            if (pop != null) {
                pop.hide();
                pop = null;
            }
            return;
        }
        OkCancelOption callback = new OkCancelOption() {
            @Override
            public boolean onClickOk() {
                CListBox.this.setValue(pop.getSelectedItems());
                pop = null;
                return true;
            }

            @Override
            public boolean onClickCancel() {
                pop = null;
                return true;
            }
        };

        pop = new ListSelectionPopup<E>(CommonsStringUtils.nvl_concat(getTitle(), "Selection Dialog", " "), callback) {
            @Override
            public String getItemName(E item) {
                return CListBox.this.getItemName(item);
            }
        };
        if (this.getDebugId() != null) {
            pop.ensureDebugId(this.getDebugId().toString());
        }
        pop.setComparator(getComparator());
        pop.setRequiredValues(getRequiredValues());
        pop.setHasRequiredValueValidationMessage(this);
        this.retriveOptions(new AsyncOptionsReadyCallback<E>() {
            @Override
            public void onOptionsReady(List<E> opt) {
                pop.setOptionalItems(opt);
            }
        });
        pop.setSelectedItems(this.getValue());

        ((Widget) nativeListBox).setSize("400px", "200px");

        pop.setBody((Widget) nativeListBox);
        pop.show();

    }

    public E getSelected() {
        if (nativeListBox != null) {
            int idx = nativeListBox.getSelectedIndex();
            if ((idx == -1) || (getValue() == null)) {
                return null;
            }
            return getValue().get(idx);
        } else {
            return null;
        }
    }

    public void setSelected(E item) {
        if (nativeListBox != null) {
            nativeListBox.setSelectedIndex(getValueIndex(item));
        }
    }

    public void onSelectionChanged(int newValueIndex) {
        E newValue = null;
        if ((newValueIndex != -1) && (getValue() != null)) {
            newValue = getValue().get(newValueIndex);
        }
        SelectionEvent.fire(this, newValue);
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
}
