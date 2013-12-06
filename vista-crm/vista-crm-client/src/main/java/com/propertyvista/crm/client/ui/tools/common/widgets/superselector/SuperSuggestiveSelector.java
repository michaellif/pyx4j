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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

public abstract class SuperSuggestiveSelector<DataType> extends SuperSelector<DataType> {

    private static final I18n i18n = I18n.get(SuperSuggestiveSelector.class);

    public static abstract class SuggestionsProvider<DataType> extends AbstractDataProvider<DataType> {

        public abstract void onSuggestionCriteriaChange(String newSuggestion);

    }

    public enum Styles implements IStyleName {

        SuggestionsPopup

    }

    private final static int SUGGESTIONS_PER_PAGE = 5;

    private final static int POPUP_HIDE_DELAY = 400;

    private final static int DEFAULT_SUGGE_DELAY = 100;

    private final SuggestionsProvider<DataType> suggestionsProvider;

    private final Cell<DataType> cell;

    private SuggestionsPopup popup;

    private final boolean alwaysSuggest;

    private Timer popupTimer;

    private int popupDelay;

    private boolean suggestionsInUse;

    public class SuggestionsPopup extends PopupPanel {

        private final CellList<DataType> suggestionList;

        private SingleSelectionModel<DataType> selectionModel;

        private final SimplePager pager;

        int selectedIndex;

        public SuggestionsPopup() {
            super(false);
            selectedIndex = -1;
            popupDelay = DEFAULT_SUGGE_DELAY;

            VerticalPanel panel = new VerticalPanel();
            panel.setStyleName(Styles.SuggestionsPopup.name());
            panel.addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    suggestionsInUse = true;
                }
            }, MouseOverEvent.getType());
            panel.addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    suggestionsInUse = false;
                    if (!SuperSuggestiveSelector.this.isFocused()) {
                        SuperSuggestiveSelector.this.delayedHideSuggestions();
                    }
                }
            }, MouseOutEvent.getType());

            suggestionList = new CellList<DataType>(cell, suggestionsProvider.getKeyProvider());
            suggestionList.setPageSize(SUGGESTIONS_PER_PAGE);
            suggestionList.setSelectionModel(selectionModel = new SingleSelectionModel<DataType>(suggestionsProvider.getKeyProvider()));
            suggestionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
            suggestionList.addCellPreviewHandler(new Handler<DataType>() {
                @Override
                public void onCellPreview(CellPreviewEvent<DataType> event) {
                    NativeEvent nativeEvent = event.getNativeEvent();
                    if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                        addItem(event.getValue());
                    } else if (BrowserEvents.KEYDOWN.equals(nativeEvent.getType()) && (nativeEvent.getKeyCode() == KeyCodes.KEY_ENTER)) {
                        addItem(event.getValue());
                        selectedIndex = event.getIndex();
                        selectionModel.setSelected(event.getValue(), true);
                    }
                }
            });
            suggestionList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);

            Label noSuggestionsMessage = new Label(i18n.tr("No Suggestions"));
            noSuggestionsMessage.getElement().getStyle().setWidth(100, Unit.PCT);
            noSuggestionsMessage.getElement().getStyle().setTextAlign(TextAlign.CENTER);
            noSuggestionsMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);

            suggestionList.setEmptyListWidget(noSuggestionsMessage);
            suggestionsProvider.addDataDisplay(suggestionList);
            panel.add(suggestionList);

            SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
            pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
            pager.setDisplay(suggestionList);

            panel.add(pager);
            panel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_CENTER);

            setWidget(panel);
        }

        public void selectPrevious() {
            if (selectedIndex == -1) {
                pager.previousPage();
                return;
            } else {
                selectionModel.setSelected(suggestionList.getVisibleItem(selectedIndex), false);
                selectedIndex -= 1;
                if (selectedIndex > -1) {
                    selectionModel.setSelected(suggestionList.getVisibleItem(selectedIndex), true);
                } else {
                    pager.previousPage();
                }
            }
        }

        public void selectNext() {
            if (selectedIndex < (suggestionList.getVisibleItemCount() - 1)) {
                selectedIndex += 1;
                selectionModel.setSelected(suggestionList.getVisibleItem(selectedIndex), true);
            } else {
                pager.nextPage();
                selectionModel.setSelected(selectionModel.getSelectedObject(), false);
                selectedIndex = -1;
            }
        }

        public DataType getSelectedItem() {
            return selectionModel.getSelectedObject();
        }

        @Override
        public void hide() {
            SuperSuggestiveSelector.this.popup = null;
            super.hide();
        }
    }

    public SuperSuggestiveSelector(IFormat<DataType> format, Cell<DataType> cell, SuggestionsProvider<DataType> suggestionsProvider, boolean alwaysSuggest) {
        super(format);
        this.alwaysSuggest = alwaysSuggest;
        this.cell = cell;
        this.suggestionsProvider = suggestionsProvider;

        addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (SuperSuggestiveSelector.this.popup != null) {
                    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_UP) {
                        SuperSuggestiveSelector.this.popup.selectPrevious();
                    } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DOWN) {
                        SuperSuggestiveSelector.this.popup.selectNext();
                    }
                }
            }
        }, KeyDownEvent.getType());
    }

    public void setSuggestionDelay(int coundownMillis) {
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

    @Override
    protected void onFocus() {
        this.suggestionsInUse = false;
        if (alwaysSuggest) {
            showSuggestions();
            this.suggestionsProvider.onSuggestionCriteriaChange("");
        }
    }

    @Override
    protected void onBlur() {
        delayedHideSuggestions();
    }

    private void onSuggestionCriteriaChange(String newSuggestionCriteria) {
        if ("".equals(newSuggestionCriteria.trim())) {
            if (!alwaysSuggest) {
                hideSuggestions();
            }
        } else {
            showSuggestions();
            this.suggestionsProvider.onSuggestionCriteriaChange(newSuggestionCriteria);
        }
    }

    private void showSuggestions() {
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

    private void hideSuggestions() {
        if (this.popup != null) {
            this.popup.hide();
        }
    }

    private void delayedHideSuggestions() {
        Timer delayedHide = new Timer() {
            @Override
            public void run() {
                if (!SuperSuggestiveSelector.this.suggestionsInUse) {
                    hideSuggestions();
                }
            }
        };
        delayedHide.schedule(POPUP_HIDE_DELAY);
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
