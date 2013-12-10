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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.view.client.AbstractDataProvider;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.IFormat;

public abstract class SuperSuggestiveSelector<DataType> extends SuperSelector<DataType> {

    public static abstract class SuggestionsProvider<DataType> extends AbstractDataProvider<DataType> {

        public abstract void onSuggestionCriteriaChange(String newSuggestion);

    }

    public enum Styles implements IStyleName {

        SuggestionsPopup

    }

    final static int SUGGESTIONS_PER_PAGE = 5;

    private final static int POPUP_HIDE_DELAY = 400;

    final static int DEFAULT_SUGGE_DELAY = 100;

    final SuggestionsProvider<DataType> suggestionsProvider;

    final Cell<DataType> cell;

    SuggestionsPopup<DataType> popup;

    private final boolean alwaysSuggest;

    private Timer popupTimer;

    int popupDelay;

    boolean suggestionsInUse;

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
    protected void onAddItemRequest() {
        if (this.popup != null) {
            addItem(this.popup.getSelectedItem());
        } else {
            super.onAddItemRequest();
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
        hideSuggestionsWithADelay();
    }

    void hideSuggestionsWithADelay() {
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
            popup = new SuggestionsPopup<DataType>(this);
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
