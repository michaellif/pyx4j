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
 * Created on Jul 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.ITextWidget;
import com.pyx4j.widgets.client.TextWatermark;
import com.pyx4j.widgets.client.WatermarkComponent;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class SelectorTextBox<E> extends Composite implements WatermarkComponent, ITextWidget, HasEnabled, HasAllKeyHandlers, HasSelectionHandlers<E> {

    private boolean editable = true;

    private TextWatermark watermark;

    private int limit = 20;

    private final OptionsGrabber<E> optionsGrabber;

    private final SuggestionDisplay display;

    private final InputTextBox box;

    private E value;

    private final IFormatter<E, String> valueFormatter;

    private final IFormatter<E, String[]> optionPathFormatter;

    public SelectorTextBox(final OptionsGrabber<E> optionsGrabber, IFormatter<E, String> valueFormatter, IFormatter<E, String[]> optionPathFormatter) {
        this.valueFormatter = valueFormatter;
        this.optionPathFormatter = optionPathFormatter;
        this.optionsGrabber = optionsGrabber;

        this.box = new InputTextBox();
        this.display = new SuggestionDisplay();

        initWidget(box);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<E> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    /**
     * Gets the limit for the number of suggestions that should be displayed for
     * this box. It is up to the current {@link SuggestOracle} to enforce this
     * limit.
     * 
     * @return the limit for the number of suggestions
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Get the {@link SuggestionDisplay} used to display suggestions.
     * 
     * @return the {@link SuggestionDisplay}
     */
    public SuggestionDisplay getSuggestionDisplay() {
        return display;
    }

    @Override
    public int getTabIndex() {
        return box.getTabIndex();
    }

    public E getValue() {
        return value;
    }

    private void setValue(E value) {
        this.value = value;
        if (value == null) {
            setText("");
        } else {
            setText(valueFormatter.format(value));
            display.hideSuggestions();
            fireSuggestionEvent(value);
        }
    }

    @Override
    public String getText() {
        if (watermark != null && watermark.isShown()) {
            return "";
        } else {
            return box.getText();
        }
    }

    @Override
    public void setText(String text) {
        box.setText(text);
        if (!box.hasFocus() && watermark != null) {
            watermark.show();
        }
    }

    @Override
    public boolean isEnabled() {
        return box.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        box.setEnabled(enabled);
        if (!enabled) {
            display.hideSuggestions();
        }
        getElement().setPropertyBoolean("disabled", !enabled);
    }

    @Override
    public void setAccessKey(char key) {
        box.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        box.setFocus(focused);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void setTabIndex(int index) {
        box.setTabIndex(index);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        display.onEnsureDebugId(baseID);
    }

    class InputTextBox extends TextBox {

        private boolean focused = false;

        private String text;

        public InputTextBox() {

            addKeyDownHandler(new KeyDownHandler() {

                @Override
                public void onKeyDown(KeyDownEvent event) {
                    switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        display.moveSelectionDown();
                        break;
                    case KeyCodes.KEY_UP:
                        display.moveSelectionUp();
                        break;
                    case KeyCodes.KEY_ENTER:
                    case KeyCodes.KEY_TAB:
                        SelectorTextBox.this.setValue(display.getCurrentSelection());
                        break;
                    }
                }
            });

            addKeyUpHandler(new KeyUpHandler() {

                @Override
                public void onKeyUp(KeyUpEvent event) {
                    if (syncInput()) {
                        refreshSuggestions();
                    }
                }
            });

            addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    delegateEvent(SelectorTextBox.this, event);
                }
            });

            addFocusHandler(new FocusHandler() {

                @Override
                public void onFocus(FocusEvent event) {
                    focused = true;
                    refreshSuggestions();
                }
            });

            addBlurHandler(new BlurHandler() {

                @Override
                public void onBlur(BlurEvent event) {
                    focused = false;
                    SelectorTextBox.this.setValue(display.getCurrentSelection());
                }
            });

            setStyleName(WidgetTheme.StyleName.TextBox.name());
            addStyleName(WidgetTheme.StyleName.SuggestBox.name());
            addStyleDependentName(WidgetTheme.StyleDependent.singleLine.name());

        }

        public boolean hasFocus() {
            return focused;
        }

        private boolean syncInput() {
            String newText = getText();
            // check if new input has been received
            boolean result = (!display.isSuggestionListShowing() && CommonsStringUtils.isEmpty(newText))
                    || (text == null ? newText != null : !text.equals(newText));
            text = newText;
            return result;
        }
    }

    private void fireSuggestionEvent(E selectedSuggestion) {
        SelectionEvent.fire(this, selectedSuggestion);
    }

    private void refreshSuggestions() {

        OptionsGrabber.Callback<E> callback = new OptionsGrabber.Callback<E>() {
            @Override
            public void onOptionsReady(OptionsGrabber.Request request, OptionsGrabber.Response<E> response) {
                // If disabled while request was in-flight, drop it
                if (!isEnabled()) {
                    return;
                }
                display.showSuggestions(response.getOptions());
            }
        };

        optionsGrabber.grabOptions(new OptionsGrabber.Request(getText().length() == 0 ? "" : getText(), limit), callback);

    }

    @Override
    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = new TextWatermark(this) {

                @Override
                public String getText() {
                    return box.getText();
                }

                @Override
                public void setText(String text) {
                    box.setText(text);
                }
            };
        }
        watermark.setWatermark(text);
    }

    @Override
    public String getWatermark() {
        return watermark.getWatermark();
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        addSelectionHandler(new SelectionHandler<E>() {
            @Override
            public void onSelection(SelectionEvent<E> event) {
                NativeEvent nativeEvent = Document.get().createChangeEvent();
                ChangeEvent.fireNativeEvent(nativeEvent, null);
            }
        });
        return addDomHandler(handler, ChangeEvent.getType());
    }

    /**
     * The callback used when a user selects a {@link Suggestion}.
     */
    public static interface SuggestionCallback<E> {
        void onSuggestionSelected(E suggestion);
    }

    class SuggestionDisplay implements HasAnimation {

        private final SuggestionMenu suggestionMenu;

        private final PopupPanel suggestionPopup;

        /**
         * We need to keep track of the last {@link SelectorTextBox} because it acts as
         * an autoHide partner for the {@link PopupPanel}. If we use the same
         * display for multiple {@link SelectorTextBox}, we need to switch the autoHide
         * partner.
         */
        private SelectorTextBox<E> lastSuggestBox = null;

        /**
         * Object to position the suggestion display next to, instead of the
         * associated suggest box.
         */
        private UIObject positionRelativeTo;

        /**
         * Construct a new {@link SuggestionDisplay}.
         */
        public SuggestionDisplay() {
            suggestionMenu = new SuggestionMenu(true);
            suggestionPopup = createPopup();
            suggestionPopup.setWidget(suggestionMenu);
        }

        public void hideSuggestions() {
            suggestionPopup.hide();
        }

        @Override
        public boolean isAnimationEnabled() {
            return suggestionPopup.isAnimationEnabled();
        }

        /**
         * Check whether or not the list of suggestions is being shown.
         * 
         * @return true if the suggestions are visible, false if not
         */
        public boolean isSuggestionListShowing() {
            return suggestionPopup.isShowing();
        }

        @Override
        public void setAnimationEnabled(boolean enable) {
            suggestionPopup.setAnimationEnabled(enable);
        }

        /**
         * Sets the style name of the suggestion popup.
         * 
         * @param style
         *            the new primary style name
         * @see UIObject#setStyleName(String)
         */
        public void setPopupStyleName(String style) {
            suggestionPopup.setStyleName(style);
        }

        /**
         * Sets the UI object where the suggestion display should appear next to.
         * 
         * @param uiObject
         *            the uiObject used for positioning, or null to position
         *            relative to the suggest box
         */
        public void setPositionRelativeTo(UIObject uiObject) {
            positionRelativeTo = uiObject;
        }

        /**
         * Create the PopupPanel that will hold the list of suggestions.
         * 
         * @return the popup panel
         */
        protected PopupPanel createPopup() {
            PopupPanel p = new PopupPanel(true, false);
            p.setStyleName(WidgetTheme.StyleName.SuggestBoxPopup.name());

            p.setPreviewingAllNativeEvents(true);
            return p;
        }

        protected E getCurrentSelection() {
            SuggestionMenuItem item = suggestionMenu.getSelectedItem();
            return item == null ? null : item.getSuggestion();
        }

        /**
         * Get the {@link PopupPanel} used to display suggestions.
         * 
         * @return the popup panel
         */
        protected PopupPanel getPopupPanel() {
            return suggestionPopup;
        }

        protected void moveSelectionDown() {
            // Make sure that the menu is actually showing. These keystrokes
            // are only relevant when choosing a suggestion.
            if (isSuggestionListShowing()) {
                // If nothing is selected, getSelectedItemIndex will return -1 and we
                // will select index 0 (the first item) by default.
                suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() + 1);
            }
        }

        protected void moveSelectionUp() {
            // Make sure that the menu is actually showing. These keystrokes
            // are only relevant when choosing a suggestion.
            if (isSuggestionListShowing()) {
                // if nothing is selected, then we should select the last suggestion by
                // default. This is because, in some cases, the suggestions menu will
                // appear above the text box rather than below it (for example, if the
                // text box is at the bottom of the window and the suggestions will not
                // fit below the text box). In this case, users would expect to be able
                // to use the up arrow to navigate to the suggestions.
                if (suggestionMenu.getSelectedItemIndex() == -1) {
                    suggestionMenu.selectItem(suggestionMenu.getNumItems() - 1);
                } else {
                    suggestionMenu.selectItem(suggestionMenu.getSelectedItemIndex() - 1);
                }
            }
        }

        protected void showSuggestions(Collection<E> suggestions) {
            // Hide the popup if there are no suggestions to display.
            boolean anySuggestions = (suggestions != null && suggestions.size() > 0);

            // Hide the popup before we manipulate the menu within it. If we do not
            // do this, some browsers will redraw the popup as items are removed
            // and added to the menu.
            if (suggestionPopup.isAttached()) {
                suggestionPopup.hide();
            }

            suggestionMenu.clearItems();
            for (final E curSuggestion : suggestions) {
                suggestionMenu.addItem(new SuggestionMenuItem(curSuggestion));
            }

            if (anySuggestions) {
                // Select the first item in the suggestion menu.
                suggestionMenu.selectItem(0);
            }

            // Link the popup autoHide to the TextBox.
            if (lastSuggestBox != SelectorTextBox.this) {
                // If the suggest box has changed, free the old one first.
                if (lastSuggestBox != null) {
                    suggestionPopup.removeAutoHidePartner(lastSuggestBox.getElement());
                }
                lastSuggestBox = SelectorTextBox.this;
                suggestionPopup.addAutoHidePartner(SelectorTextBox.this.getElement());
            }

            if (anySuggestions) {
                // Show the popup under the TextBox.
                suggestionPopup.showRelativeTo(positionRelativeTo != null ? positionRelativeTo : SelectorTextBox.this);

            } else {
                suggestionPopup.hide();
            }
        }

        /**
         * Set the debug id of widgets used in the SuggestionDisplay.
         * 
         * @param suggestBoxBaseID
         *            the baseID of the {@link SelectorTextBox}
         * @see UIObject#onEnsureDebugId(String)
         */
        protected void onEnsureDebugId(String suggestBoxBaseID) {
        }

    }

    /**
     * The SuggestionMenu class is used for the display and selection of
     * suggestions in the SuggestBox widget. SuggestionMenu differs from MenuBar
     * in that it always has a vertical orientation, and it has no submenus. It
     * also allows for programmatic selection of items in the menu, and
     * programmatically performing the action associated with the selected item.
     * In the MenuBar class, items cannot be selected programatically - they can
     * only be selected when the user places the mouse over a particlar item.
     * Additional methods in SuggestionMenu provide information about the number
     * of items in the menu, and the index of the currently selected item.
     */
    private class SuggestionMenu extends MenuBar {

        public SuggestionMenu(boolean vertical) {
            super(vertical);
            // Make sure that CSS styles specified for the default Menu classes
            // do not affect this menu
            setStyleName("");
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
    }

    private class SuggestionMenuItem extends MenuItem {

        private static final String STYLENAME_DEFAULT = "item";

        private final E suggestion;

        public SuggestionMenuItem(final E suggestion) {
            super(valueFormatter.format(suggestion), true, (MenuBar) null);
            this.suggestion = suggestion;
            getElement().getStyle().setProperty("whiteSpace", "nowrap");
            setStyleName(STYLENAME_DEFAULT);

            setScheduledCommand(new ScheduledCommand() {
                @Override
                public void execute() {
                    box.setFocus(true);
                    setValue(suggestion);
                }
            });
        }

        public E getSuggestion() {
            return suggestion;
        }

    }

}