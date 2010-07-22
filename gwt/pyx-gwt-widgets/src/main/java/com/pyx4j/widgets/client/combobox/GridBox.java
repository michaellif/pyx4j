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
 * Created on Jul 21, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.combobox;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTMLMultipleListBox.java
 * 
 * Works very similarly to HTMLListBox, except it utilizes checkboxes to make it a
 * multiple select (as opposed to CTRL+Click). Clicking on an item still calls onChange(),
 * but checking an item now selects it (not just clicking it), and also calls onChecked()
 * 
 * @author carl.scott
 * 
 */
public abstract class GridBox extends ScrollPanel {

    class SelectableHTML extends SimplePanel {

        private final CheckBox checkBox;

        private final HTML html;

        public SelectableHTML(final String name, final String value) {
            this(name, value, null);
        }

        public SelectableHTML(final String name, final String value, final String toolTip) {
            super();
            checkBox = new CheckBox();
            checkBox.addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
                    if (checkBox.isChecked()) {
                        selectedValues.put(value, null);
                        onChecked(value, true);
                    } else if (selectedValues.containsKey(value)) {
                        selectedValues.remove(value);
                        onChecked(value, false);
                    }
                }
            });

            html = new HTML(name);
            html.setWordWrap(false);

            final Grid checktable = new Grid(1, 2);
            checktable.setWidget(0, 0, checkBox);
            checktable.setWidget(0, 1, html);
            setWidget(checktable);

            setStyleName("CIPD_ListBox_Item");
        }

        public String getText() {
            return html.getText();
        }

        public boolean isSelected() {
            return checkBox.isChecked();
        }

        @Override
        public void onBrowserEvent(final Event evt) {
            switch (DOM.eventGetType(evt)) {
            case Event.ONMOUSEOVER: {
                break;
            }
            case Event.ONMOUSEOUT: {
                break;
            }
            }
        }

        public void setChecked(final boolean isChecked) {
            checkBox.setChecked(isChecked);
        }

        public void setEnabled(final boolean isEnabled) {
            checkBox.setEnabled(isEnabled);
        }

        public void setHTML(final String text) {
            html.setHTML(text);
        }

    }

    protected ArrayList<String> items;

    protected Grid table;

    protected int selectedIndex;

    protected HashMap<String, String> selectedValues;

    protected boolean isEnabled = true;

    protected String width;

    public GridBox(final String width) {
        super();
        items = new ArrayList<String>();
        selectedValues = new HashMap<String, String>();
        setWidth(this.width = width);
    }

    public void addItem(final String name, final String value) {
        addItem(name, value, null);
    }

    public void addItem(final String name, final String value, final String toolTip) {
        table.setWidget(items.size(), 0, new SelectableHTML(name, value, toolTip));
        items.add(value);

        table.getColumnFormatter().setWidth(0, width);
    }

    @Override
    public void clear() {
        items.clear();
        if (table != null)
            remove(table);
        selectedIndex = -1;
        selectedValues.clear();
    }

    public ArrayList<String> getCheckedValues() {
        final ArrayList<String> checked = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            if (((SelectableHTML) table.getWidget(i, 0)).isSelected()) {
                checked.add(items.get(i));
            }
        }
        return checked;
        // return new ArrayList(selectedValues.keySet());
    }

    public int getItemCount() {
        return items.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Grid getTableView() {
        return table;
    }

    public String getText(final int index) {
        return ((SelectableHTML) table.getWidget(index, 0)).getText();
    }

    public String getValue(final int index) {
        return items.get(index);
    }

    public int indexOf(final String value) {
        return items.indexOf(value);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public abstract void onChange(String selectedValue);

    /**
     * Method that occurs when an item is checked.
     * 
     * @param checkedValue
     *            the value of the item checked
     * @param isChecked
     *            the isChecked() value of the checkbox
     */
    public abstract void onChecked(String checkedValue, boolean isChecked);

    public void setChecked(final int index, final boolean isChecked) {
        ((SelectableHTML) table.getWidget(index, 0)).setChecked(isChecked);
        if (isChecked) {
            selectedValues.put(items.get(index), null);
        } else {
            selectedValues.remove(items.get(index));
        }
    }

    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        for (int i = 0; i < items.size(); i++) {
            ((SelectableHTML) table.getWidget(i, 0)).setEnabled(isEnabled);
        }
    }

    public void setSelectedIndex(final int index) {
        selectedIndex = index;
        for (int i = 0; i < items.size(); i++) {
            ((SelectableHTML) table.getWidget(i, 0)).setStyleName(i == index ? "CIPD_ListBox_ItemSelected" : "CIPD_ListBox_Item");
        }
    }

    public void setText(final String text, final int index) {
        ((SelectableHTML) table.getWidget(index, 0)).setHTML(text);
    }

    public void init(int size) {
        table = new Grid(size, 1);
        table.addTableListener(new TableListener() {
            public void onCellClicked(final SourcesTableEvents sender, final int row, final int cell) {
                if (isEnabled) {
                    setSelectedIndex(row);
                    onChange(items.get(row));
                }
            }
        });
        setWidget(table);
    }
}