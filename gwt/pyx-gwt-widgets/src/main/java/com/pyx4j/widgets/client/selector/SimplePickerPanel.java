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
package com.pyx4j.widgets.client.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class SimplePickerPanel<E> extends ScrollPanel implements IPickerPanel<E> {

    private static TreeImages images = GWT.create(TreeImages.class);

    private HandlerRegistration handlerRegistration;

    private final IOptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, SafeHtml> optionFormatter;

    private final int limit = 20;

    private final static int SUGGESTIONS_PER_PAGE = 14;

    private final PickerTree tree;

    public SimplePickerPanel(IOptionsGrabber<E> optionsGrabber, IFormatter<E, SafeHtml> optionFormatter) {
        this.optionsGrabber = optionsGrabber;
        this.optionFormatter = optionFormatter;

        setStyleName(WidgetsTheme.StyleName.SelectionPickerPanel.name());

        if (optionFormatter == null) {
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
    public void refreshOptions(String query, final Collection<E> ignoreOptions) {

        IOptionsGrabber.Callback<E> callback = new IOptionsGrabber.Callback<E>() {
            @Override
            public void onOptionsReady(IOptionsGrabber.Request request, IOptionsGrabber.Response<E> response) {
                showOptions(response.getOptions(), request.getQuery(), ignoreOptions);
            }
        };

        optionsGrabber.grabOptions(new IOptionsGrabber.Request(query == null ? "" : query, ignoreOptions != null ? limit + ignoreOptions.size() : limit),
                callback);
    }

    protected void showOptions(Collection<E> options, String query, Collection<E> ignoreOptions) {
        setVisible(false);

        if (ignoreOptions != null && ignoreOptions.size() != 0 && options != null) {
            options.removeAll(ignoreOptions);
        }

        List<E> suggestions = new ArrayList<E>(options);

        tree.setOptions(suggestions.subList(0, suggestions.size() < SUGGESTIONS_PER_PAGE ? suggestions.size() : SUGGESTIONS_PER_PAGE), query);

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
                PickerTreeItem treeItem = new PickerTreeItem(option, query);
                addItem(treeItem);
                treeItem.setUserObject(option);
            }
        }

        @SuppressWarnings("unchecked")
        public E getSelection() {
            PickerTreeItem selectedItem = (PickerTreeItem) getSelectedItem();
            return selectedItem == null ? null : selectedItem.getValue();
        }

        @Override
        public void setFocus(boolean focus) {

        }

        class PickerTreeItem extends TreeItem {

            private final E value;

            private final HTML label;

            public PickerTreeItem(E value, String query) {
                super();
                this.value = value;
                SafeHtml formattedValue = optionFormatter.format(value);
                label = new HTML((query.equals("") ? formattedValue : OptionQueryHighlighter.highlight(formattedValue, query)));
                label.setStyleName(WidgetsTheme.StyleName.SelectionPickerPanelItem.name());
                setWidget(label);
            }

            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                label.setStyleDependentName(WidgetsTheme.StyleDependent.selected.name(), selected);
            }

            public E getValue() {
                return value;
            }

        }
    }
}
