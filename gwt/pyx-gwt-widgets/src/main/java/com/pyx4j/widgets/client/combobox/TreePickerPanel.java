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
 * Created on Jul 20, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.combobox;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.commons.css.CSSClass;

public class TreePickerPanel<E> extends PickerPanel<E> {

    private static TreeImages images = GWT.create(TreeImages.class);

    private final ListBox<E> listBox;

    private final PickerTree tree;

    private final boolean multipleSelect;

    private final boolean plainList;

    private boolean requiredOptionsRefresh = true;

    private final PickerScrollPanel scroll;

    public TreePickerPanel(ListBox<E> listBox, boolean multipleSelect, boolean plainList) {
        this.listBox = listBox;
        this.multipleSelect = multipleSelect;
        this.plainList = plainList;
        if (plainList) {
            tree = new PickerTree(images, true);
        } else {
            tree = new PickerTree();
        }
        scroll = new PickerScrollPanel(tree);
        setWidget(scroll);
    }

    @Override
    protected void setOptions(List<E> options) {
        requiredOptionsRefresh = (options == null);
        tree.clear();
        for (E option : options) {
            PickerTreeItem treeItem = null;
            treeItem = new PickerTreeItem(option);
            tree.addItem(treeItem);
            if (!plainList) {
                treeItem.addItem(SafeHtmlUtils.fromTrustedString("Loading..."));
            }

            treeItem.setUserObject(option);

            treeItem.getElement().getStyle().setPadding(1, Unit.PX);
            treeItem.setStyleName(CSSClass.pyx4j_PickerLine.name());
        }
    }

    @Override
    public boolean isRequiredOptionsRefresh() {
        return requiredOptionsRefresh;
    }

    @Override
    protected void setSelection(Collection<E> items) {
        final Iterator<TreeItem> itr = tree.treeItemIterator();
        while (itr.hasNext()) {
            PickerTreeItem treeItem = (PickerTreeItem) itr.next();
            E item = (E) treeItem.getUserObject();
            treeItem.setChecked(items.contains(item));
        }
    }

    protected Set<E> getSelection() {
        Set<E> items = new HashSet<E>();
        final Iterator<TreeItem> itr = tree.treeItemIterator();
        while (itr.hasNext()) {
            PickerTreeItem treeItem = (PickerTreeItem) itr.next();
            E item = (E) treeItem.getUserObject();
            if (treeItem.isChecked()) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public void setFocus(boolean focus) {
        tree.setFocus(focus);
    }

    @Override
    public void ensureSelectedIsVisible() {
        if (tree.getSelectedItem() != null) {
            scroll.ensureVisible(tree.getSelectedItem().getWidget());
        }

    }

    class PickerTreeItem extends TreeItem {

        private final E value;

        private CheckBox checkBox;

        private boolean checked;

        public PickerTreeItem(E value) {
            super();
            this.value = value;
            if (multipleSelect) {
                checkBox = new CheckBox(value.toString());
                checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        checked = event.getValue();
                    }
                });
                setWidget(checkBox);
            } else {
                HTML label = new HTML(value.toString());
                setWidget(label);
            }

        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (!multipleSelect) {
                setStyleName(getElement(), CSSClass.pyx4j_PickerLine_Selected.name(), checked);
            } else {
                checkBox.setValue(checked);
            }
        }

        public boolean isChecked() {
            return checked;
        }

    }

    class PickerTree extends Tree {

        public PickerTree(Resources resources, boolean useLeafImages) {
            super(resources, useLeafImages);
        }

        public PickerTree() {
            super();
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (!multipleSelect) {
                int eventType = DOM.eventGetType(event);
                switch (eventType) {
                case Event.ONCLICK:
                    hide();
                    break;
                case Event.ONKEYPRESS: {
                    if (KeyboardListener.KEY_ENTER == event.getKeyCode()) {
                        hide();
                    }
                }
                    break;
                }
            }
        }

    }
}
