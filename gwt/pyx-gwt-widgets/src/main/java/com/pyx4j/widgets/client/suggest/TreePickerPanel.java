/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 5, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.combobox.TreeImages;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class TreePickerPanel<E> extends ScrollPanel implements IPickerPanel<E> {

    private static TreeImages images = GWT.create(TreeImages.class);

    private PickerPopup<E> pickerPopup;

    private final OptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, String> optionsFormatter;

    private final IFormatter<E, String[]> optionPathFormatter;

    private final int limit = 20;

    private final PickerTree tree;

    public TreePickerPanel(OptionsGrabber<E> optionsGrabber, IFormatter<E, String> optionsFormatter, IFormatter<E, String[]> optionPathFormatter) {
        this.optionsGrabber = optionsGrabber;
        this.optionsFormatter = optionsFormatter;
        this.optionPathFormatter = optionPathFormatter;

        getElement().getStyle().setProperty("overflowY", "auto");
        getElement().getStyle().setProperty("overflowX", "hidden");

        if (optionPathFormatter == null) {
            tree = new PickerTree(images, true);
        } else {
            tree = new PickerTree();
        }
        setWidget(tree);

        getElement().getStyle().setProperty("maxHeight", "200px");
    }

    @Override
    public void setPickerPopup(PickerPopup<E> pickerPopup) {
        this.pickerPopup = pickerPopup;
    }

    @Override
    public void moveSelectionDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveSelectionUp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pickSelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshOptions(String query) {
        OptionsGrabber.Callback<E> callback = new OptionsGrabber.Callback<E>() {
            @Override
            public void onOptionsReady(OptionsGrabber.Request request, OptionsGrabber.Response<E> response) {
                showOptions(response.getOptions(), request.getQuery());
            }
        };

        optionsGrabber.grabOptions(new OptionsGrabber.Request(query == null ? "" : query, limit), callback);
    }

    protected void showOptions(Collection<E> options, String query) {
        setVisible(false);
        tree.clear();
        for (E option : options) {
            PickerTreeItem treeItem = new PickerTreeItem(option);
            tree.addItem(treeItem);

            treeItem.setUserObject(option);

            treeItem.getElement().getStyle().setPadding(1, Unit.PX);
            treeItem.setStyleName(WidgetTheme.StyleName.SelectionBoxPickerItem.name());
        }
        setVisible(true);
    }

    class PickerTree extends Tree {

        public PickerTree(Resources resources, boolean useLeafImages) {
            super(resources, useLeafImages);
        }

        public PickerTree() {
            super();
        }

    }

    class PickerTreeItem extends TreeItem {

        private final E value;

        public PickerTreeItem(E value) {
            super();
            this.value = value;
            HTML label = new HTML(optionsFormatter.format(value));
            setWidget(label);

        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            setStyleDependentName(WidgetTheme.StyleDependent.selected.name(), selected);
        }

        public E getValue() {
            return value;
        }

    }

}
