/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-12-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.superselector;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public abstract class SuperSuggestiveSelector<DataType> extends SuperSelector<DataType> {

    public static abstract class SuggestionsProvider<DataType> extends AbstractDataProvider<DataType> {

        public abstract void onSuggestionCriteriaChange(String newSuggestion);

    }

    private final static int DEFAULT_POPUP_DELAY = 100;

    private final SuggestionsProvider<DataType> suggestionsProvider;

    private boolean isSuggestionsActive;

    private final Cell<DataType> cell;

    private Timer popupTimer;

    private SuggestionsPopup popup;

    private int popupDelay;

    public class SuggestionsPopup extends PopupPanel {

        private final CellList<DataType> suggestionList;

        private SingleSelectionModel<DataType> selectionModel;

        int selectedIndex;

        public SuggestionsPopup() {
            super(true);
            suggestionList = new CellList<DataType>(cell, suggestionsProvider.getKeyProvider());
            suggestionList.setSelectionModel(selectionModel = new SingleSelectionModel<DataType>(suggestionsProvider.getKeyProvider()));
            suggestionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
            suggestionList.addCellPreviewHandler(new Handler<DataType>() {
                @Override
                public void onCellPreview(CellPreviewEvent<DataType> event) {
                    NativeEvent nativeEvent = event.getNativeEvent();
                    if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                        addItem(event.getValue());
                    } else if (BrowserEvents.KEYUP.equals(nativeEvent.getType()) && (nativeEvent.getKeyCode() == KeyCodes.KEY_ENTER)) {
                        addItem(event.getValue());
                        selectedIndex = event.getIndex();
                        selectionModel.setSelected(event.getValue(), true);
                    }
                }
            });

            suggestionsProvider.addDataDisplay(suggestionList);
            selectedIndex = -1;
            popupDelay = DEFAULT_POPUP_DELAY;

            setWidget(suggestionList);
        }

        public void suggestPrevious() {
            if (selectedIndex == 0) {
                selectedIndex = -1;
                selectionModel.clear();
            } else {
                selectedIndex -= 1;
                selectionModel.setSelected(suggestionList.getVisibleItem(selectedIndex), true);
            }
        }

        public void suggestNext() {
            if (selectedIndex < suggestionList.getRowCount() - 1) {
                selectedIndex += 1;
                selectionModel.setSelected(suggestionList.getVisibleItem(selectedIndex), true);
            }
        }

        public DataType getSelectedItem() {
            return selectionModel.getSelectedObject();
        }

        @Override
        public void hide() {
            SuperSuggestiveSelector.this.isSuggestionsActive = false;
            SuperSuggestiveSelector.this.popup = null;
            super.hide();

        }
    }

    public SuperSuggestiveSelector(Cell<DataType> cell, SuggestionsProvider<DataType> suggestionsProvider) {
        this.cell = cell;
        this.suggestionsProvider = suggestionsProvider;
        this.isSuggestionsActive = false;

        addDomHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (SuperSuggestiveSelector.this.popup != null) {
                    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_UP) {
                        SuperSuggestiveSelector.this.popup.suggestPrevious();
                    } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DOWN) {
                        SuperSuggestiveSelector.this.popup.suggestNext();
                    }
                }
            }
        }, KeyPressEvent.getType());
    }

    public void setSuggestionPopupDelay(int coundownMillis) {
        this.popupDelay = coundownMillis;
    }

    @Override
    public void addItem(DataType item) {
        if (this.popup != null && this.popup.getSelectedItem() != null) {
            super.addItem(this.popup.getSelectedItem());
        } else {
            super.addItem(item);
        }
    }

    @Override
    protected void onInputChanged(final String newInput) {
        if (this.popupTimer != null) {
            this.popupTimer.cancel();
        }
        this.popupTimer = new Timer() {
            @Override
            public void run() {
                onSuggestionCriteriaChange(newInput);
            }
        };
        this.popupTimer.schedule(this.popupDelay);
    }

    @Override
    protected void onRedraw() {
        if (this.popup != null) {
            repostitionPopup();
        }
    }

    private void onSuggestionCriteriaChange(String newSuggestionCriteria) {
        if ("".equals(newSuggestionCriteria.trim())) {
            hideSuggestionsPopup();
        } else {
            showSuggestionsPopup();
        }
        if (isSuggestionsActive) {
            this.suggestionsProvider.onSuggestionCriteriaChange(newSuggestionCriteria);
        }
    }

    private void showSuggestionsPopup() {
        this.isSuggestionsActive = true;

        if (popup == null) {
            popup = new SuggestionsPopup();

            popup.setPopupPositionAndShow(new PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    repostitionPopup();
                }
            });
        }
    }

    private void hideSuggestionsPopup() {
        if (this.popup != null) {
            this.popup.hide();
        }
    }

    private void repostitionPopup() {
        Point p = getPopupPostion();
        popup.setPopupPosition((int) p.getX(), (int) p.getY());
    }

    private Point getPopupPostion() {
        Element parent = this.getElement();
        popup.setWidth("" + (parent.getAbsoluteRight() - parent.getAbsoluteLeft()) + "px");
        final int left = parent.getAbsoluteLeft();
        final int top = parent.getAbsoluteBottom() + 1;
        return new Point(left, top);
    }

}
