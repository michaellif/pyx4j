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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.CSSClass;

public class TreePickerPanel<E> extends PickerPanel<E> {

    private final Tree tree;

    private final boolean multipleSelect;

    private final boolean plainList;

    private static TreeImages images = GWT.create(TreeImages.class);

    private boolean requiresOptionsRefresh = true;

    private final PickerScrollPanel scroll;

    public TreePickerPanel(ListBox<E> listBox, boolean multipleSelect, boolean plainList) {
        this.multipleSelect = multipleSelect;
        this.plainList = plainList;
        if (plainList) {
            tree = new Tree(images, true);
        } else {
            tree = new Tree();
        }
        scroll = new PickerScrollPanel(tree);
        setWidget(scroll);
    }

    @Override
    protected void setOptions(List<E> options) {
        requiresOptionsRefresh = (options == null);
        tree.clear();
        for (E option : options) {
            PickerTreeItem treeItem = null;
            if (multipleSelect) {
                CheckBox itemWidget = new CheckBox(option.toString());
                treeItem = new PickerTreeItem(itemWidget);
                tree.addItem(treeItem);
            } else {
                HTML itemWidget = new HTML(option.toString());
                //itemWidget.getElement().getStyle().setDisplay(Display.INLINE);
                treeItem = new PickerTreeItem(itemWidget);
                tree.addItem(treeItem);
            }

            if (!plainList) {
                treeItem.addItem("Loading...");
            }

            treeItem.setUserObject(option);

            treeItem.getElement().getStyle().setPadding(1, Unit.PX);
            treeItem.setStyleName(CSSClass.pyx4j_PickerLine.name());
        }
        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                //hide();
            }
        });
        //        tree.addMouseOverHandler(new MouseOverHandler() {
        //
        //            @Override
        //            public void onMouseOver(MouseOverEvent event) {
        //                event.getRelativeElement().getStyle().setBackgroundColor("green");
        //            }
        //        });
        //        tree.addMouseOutHandler(new MouseOutHandler() {
        //
        //            @Override
        //            public void onMouseOut(MouseOutEvent event) {
        //                event.getRelativeElement().getStyle().setBackgroundColor("red");
        //            }
        //        });

    }

    @Override
    public boolean requiresOptionsRefresh() {
        return requiresOptionsRefresh;
    }

    @Override
    protected void setOptions(List<E> options, E parent) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setSelection(Set<E> items) {
        final Iterator<TreeItem> itr = tree.treeItemIterator();
        while (itr.hasNext()) {
            TreeItem treeItem = itr.next();
            E item = (E) treeItem.getUserObject();
            treeItem.setSelected(items.contains(item));
            if (items.contains(item)) {
                tree.setSelectedItem(treeItem);
            }
        }
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

        public PickerTreeItem(Widget widget) {
            super(widget);
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if (!multipleSelect) {
                setStyleName(getElement(), CSSClass.pyx4j_PickerLine_Selected.name(), selected);
            }
        }

    }
}
