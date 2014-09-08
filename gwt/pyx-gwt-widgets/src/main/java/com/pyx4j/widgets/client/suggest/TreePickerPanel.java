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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.combobox.TreeImages;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class TreePickerPanel<E> extends ScrollPanel implements IPickerPanel<E> {

    private static TreeImages images = GWT.create(TreeImages.class);

    private HandlerRegistration handlerRegistration;

    private final OptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, String> optionsFormatter;

    private final IFormatter<E, String[]> optionPathFormatter;

    private final int limit = 20;

    private final PickerTree tree;

    public TreePickerPanel(OptionsGrabber<E> optionsGrabber, IFormatter<E, String> optionsFormatter, IFormatter<E, String[]> optionPathFormatter) {
        this.optionsGrabber = optionsGrabber;
        this.optionsFormatter = optionsFormatter;
        this.optionPathFormatter = optionPathFormatter;

        setStyleName(WidgetTheme.StyleName.SelectionPickerPanel.name());

        if (optionPathFormatter == null) {
            tree = new PickerTree(images, true);
        } else {
            tree = new PickerTree();
        }
        setWidget(tree);

        getElement().getStyle().setProperty("maxHeight", "200px");

    }

    @Override
    public void setPickerPopup(final PickerPopup<E> pickerPopup) {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
        if (pickerPopup != null) {
            handlerRegistration = tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

                @Override
                public void onSelection(SelectionEvent<TreeItem> event) {
                    pickerPopup.pickSelection();
                }
            });
        }
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

        tree.setOptions(options, query);

        if (options.size() > 0) {
            setVisible(true);
        }
    }

    @Override
    public void moveSelectionDown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveSelectionUp() {

    }

    @Override
    public E getSelection() {
        return tree.getSelection();
    }

    class PickerTree extends Tree {

        public PickerTree() {
            super();
        }

        public PickerTree(Resources resources, boolean useLeafImages) {
            super(resources, useLeafImages);
        }

        public void setOptions(Collection<E> options, String query) {
            clear();

            for (E option : options) {
                PickerTreeItem treeItem = new PickerTreeItem(option);
                addItem(treeItem);
                treeItem.setUserObject(option);
            }
        }

        @SuppressWarnings("unchecked")
        public E getSelection() {
            return ((PickerTreeItem) getSelectedItem()).getValue();
        }

        class PickerTreeItem extends TreeItem {

            private final E value;

            private final HTML label;

            public PickerTreeItem(E value) {
                super();
                this.value = value;
                label = new HTML(optionsFormatter.format(value));
                label.setStyleName(WidgetTheme.StyleName.SelectionPickerPanelItem.name());
                setWidget(label);
            }

            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                label.setStyleDependentName(WidgetTheme.StyleDependent.selected.name(), selected);
            }

            public E getValue() {
                return value;
            }

        }
    }

}
