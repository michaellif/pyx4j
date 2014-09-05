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
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.IFormatter;

public class MenuPickerPanel<E> extends MenuBar implements IPickerPanel<E> {

    private PickerPopup<E> pickerPopup;

    private final OptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, String> optionsFormatter;

    private final int limit = 20;

    public MenuPickerPanel(OptionsGrabber<E> optionsGrabber, IFormatter<E, String> optionsFormatter) {
        this.optionsGrabber = optionsGrabber;
        this.optionsFormatter = optionsFormatter;

        setFocusOnHoverEnabled(false);
    }

    public int getNumItems() {
        return getItems().size();
    }

    /**
     * Returns the index of the menu item that is currently selected.
     * 
     * @return returns the selected item
     */
    public int getSelectedItemIndex() {
        // The index of the currently selected item can only be
        // obtained if the menu is showing.
        MenuItem selectedItem = getSelectedItem();
        if (selectedItem != null) {
            return getItems().indexOf(selectedItem);
        }
        return -1;
    }

    /**
     * Selects the item at the specified index in the menu. Selecting the item
     * does not perform the item's associated action; it only changes the style
     * of the item and updates the value of SuggestionMenu.selectedItem.
     * 
     * @param index
     *            index
     */
    public void selectItem(int index) {
        List<MenuItem> items = getItems();
        if (index > -1 && index < items.size()) {
            //TODO verify missing functionality from - itemOver(items.get(index), false);
            selectItem(items.get(index));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SuggestionMenuItem getSelectedItem() {
        return (SuggestionMenuItem) super.getSelectedItem();
    }

    @Override
    public void moveSelectionDown() {
        selectItem(getSelectedItemIndex() + 1);
    }

    @Override
    public void moveSelectionUp() {
        if (getSelectedItemIndex() == -1) {
            selectItem(getNumItems() - 1);
        } else {
            selectItem(getSelectedItemIndex() - 1);
        }
    }

    protected void showSuggestions(Collection<E> suggestions, String query) {
        // Hide the popup if there are no suggestions to display.
        boolean anySuggestions = (suggestions != null && suggestions.size() > 0);

        // Hide the popup before we manipulate the menu within it. If we do not
        // do this, some browsers will redraw the popup as items are removed
        // and added to the menu.
        if (isAttached()) {
            setVisible(false);
        }

        clearItems();
        for (final E suggestion : suggestions) {
            addItem(new SuggestionMenuItem(suggestion));
        }

        if (anySuggestions) {
            // Select the first item in the suggestion menu.
            selectItem(0);
        }

        setVisible(true);
    }

    @Override
    public void refreshSuggestions() {

        OptionsGrabber.Callback<E> callback = new OptionsGrabber.Callback<E>() {
            @Override
            public void onOptionsReady(OptionsGrabber.Request request, OptionsGrabber.Response<E> response) {
                showSuggestions(response.getOptions(), request.getQuery());
            }
        };

        String query = pickerPopup.getSelectorWidget().getViewerPanel().getQuery();
        optionsGrabber.grabOptions(new OptionsGrabber.Request(query == null ? "" : query, limit), callback);

    }

    private class SuggestionMenuItem extends MenuItem {

        private static final String STYLENAME_DEFAULT = "item";

        public SuggestionMenuItem(final E suggestion) {
            super(optionsFormatter.format(suggestion), true, (MenuBar) null);
            getElement().getStyle().setProperty("whiteSpace", "nowrap");
            setStyleName(STYLENAME_DEFAULT);

            setScheduledCommand(new ScheduledCommand() {
                @Override
                public void execute() {
                    pickerPopup.getSelectorWidget().getViewerPanel().setFocus(true);
                    pickerPopup.getSelectorWidget().setValue(suggestion);
                }
            });
        }
    }

    @Override
    public void setPickerPopup(PickerPopup<E> pickerPopup) {
        this.pickerPopup = pickerPopup;
    }

    @Override
    public void pickSelection() {
        // TODO Auto-generated method stub
    }

}
