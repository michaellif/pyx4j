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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.widgets.client.style.CSSClass;

public class TreePickerPanel<E> extends PickerPanel<E> {

    private final Tree tree;

    private final boolean multipleSelect;

    private static TreeImages images = GWT.create(TreeImages.class);

    public TreePickerPanel(ListBox<E> listBox, boolean multipleSelect) {
        this.multipleSelect = multipleSelect;
        if (multipleSelect) {
            tree = new Tree();

        } else {
            tree = new Tree(images, true);
        }
        PickerScrollPanel scroll = new PickerScrollPanel(tree);
        setWidget(scroll);
    }

    @Override
    protected void setOptions(List<E> options) {
        tree.clear();
        for (E option : options) {
            TreeItem treeItem = null;
            if (multipleSelect) {
                CheckBox itemWidget = new CheckBox(option.toString());
                itemWidget.setWidth("100%");
                treeItem = tree.addItem(itemWidget);
                treeItem.addItem("Leaf");
            } else {
                treeItem = tree.addItem(option.toString());
            }
            treeItem.getElement().getStyle().setPadding(1, Unit.PX);
            treeItem.setStyleName(CSSClass.pyx4j_PickerLine.name());

        }
    }

}
