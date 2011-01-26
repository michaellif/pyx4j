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
package com.pyx4j.forms.client.gwt;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

import com.pyx4j.forms.client.ui.CListBox;
import com.pyx4j.forms.client.ui.CListBox.ListBoxDisplayProperties;
import com.pyx4j.forms.client.ui.INativeListBox;

public class NativeListBox<E> extends NativeTriggerComponent<List<E>> implements INativeListBox<E> {

    final private CListBox<E> cListBox;

    private final InnerListBox nativeListBox;

    private ListBoxDisplayProperties displayProperties;

    private int selectedIndex = -1;

    public NativeListBox(final CListBox<E> cListBox, ListBoxDisplayProperties properties) {
        super();
        this.cListBox = cListBox;
        nativeListBox = new InnerListBox();
        construct(nativeListBox);
        setTabIndex(cListBox.getTabIndex());
        setDisplayProperties(properties);

        setWidth(cListBox.getWidth());
        setHeight(cListBox.getHeight());

        nativeListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (!displayProperties.multipleSelect) {
                    // Only one item can be selected
                    int selectedFirst = nativeListBox.getSelectedIndex();
                    int count = nativeListBox.getItemCount();
                    for (int i = 0; i < count; i++) {
                        if (i == selectedFirst) {
                            continue;
                        }
                        if (nativeListBox.isItemSelected(i)) {
                            nativeListBox.setItemSelected(i, false);
                        }
                    }
                    if (selectedIndex != selectedFirst) {
                        cListBox.onSelectionChanged(selectedFirst);
                    }
                    selectedIndex = selectedFirst;
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !super.isReadOnly();
    }

    public void setDisplayProperties(ListBoxDisplayProperties properties) {
        this.displayProperties = properties;
        this.nativeListBox.setVisibleItemCount(properties.visibleItemCount);
    }

    public int getSelectedIndex() {
        return nativeListBox.getSelectedIndex();
    }

    public void setSelectedIndex(int index) {
        nativeListBox.setSelectedIndex(index);
        selectedIndex = index;
    }

    @Override
    protected void onTrigger(boolean show) {
        cListBox.onTrigger(show);
    }

    public void removeAllItems() {
        nativeListBox.clear();
        selectedIndex = -1;
    }

    public void removeItem(int index) {
        if (index < 0) {
            return;
        }
        nativeListBox.removeItem(index);
        if (selectedIndex == index) {
            selectedIndex = -1;
        }
    }

    public void refreshItem(int index) {
        if (index < 0) {
            return;
        }
        nativeListBox.setItemText(index, cListBox.getItemName(cListBox.getValue().get(index)));
    }

    class InnerListBox extends ListBox {

        public InnerListBox() {
            super(true);
        }

    }

    public void setNativeValue(List<E> value) {
        this.selectedIndex = -1;
        removeAllItems();
        if (value != null) {
            for (E v : value) {
                nativeListBox.addItem(cListBox.getItemName(v));
            }
        }
    }

    public CListBox<E> getCComponent() {
        return cListBox;
    }

}