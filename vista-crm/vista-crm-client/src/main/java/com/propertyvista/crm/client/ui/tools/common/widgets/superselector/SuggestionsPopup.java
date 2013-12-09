/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.superselector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSuggestiveSelector.Styles;

public class SuggestionsPopup<DataType> extends PopupPanel {

    private static final I18n i18n = I18n.get(SuggestionsPopup.class);

    private final SuperSuggestiveSelector<DataType> parentSelector;

    private final CellList<DataType> suggestionList;

    private SingleSelectionModel<DataType> selectionModel;

    private final SimplePager pager;

    int selectedIndex;

    public SuggestionsPopup(SuperSuggestiveSelector<DataType> superSuggestiveSelector) {
        super(false);
        parentSelector = superSuggestiveSelector;
        selectedIndex = -1;
        parentSelector.popupDelay = SuperSuggestiveSelector.DEFAULT_SUGGE_DELAY;

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName(Styles.SuggestionsPopup.name());
        panel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                SuggestionsPopup.this.parentSelector.suggestionsInUse = true;
            }
        }, MouseOverEvent.getType());
        panel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                SuggestionsPopup.this.parentSelector.suggestionsInUse = false;
                if (!parentSelector.isFocused()) {
                    parentSelector.hideSuggestionsWithADelay();
                }
            }
        }, MouseOutEvent.getType());

        suggestionList = new CellList<DataType>(parentSelector.cell, parentSelector.suggestionsProvider.getKeyProvider());
        suggestionList.setEmptyListWidget(makeNoSuggestionsWidget());
        suggestionList.setPageSize(SuperSuggestiveSelector.SUGGESTIONS_PER_PAGE);

        suggestionList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        suggestionList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
        suggestionList.setKeyboardSelectedRow(0, false);

        suggestionList.setSelectionModel(selectionModel = new SingleSelectionModel<DataType>(parentSelector.suggestionsProvider.getKeyProvider()));
        suggestionList.addCellPreviewHandler(new Handler<DataType>() {
            @Override
            public void onCellPreview(CellPreviewEvent<DataType> event) {
                NativeEvent nativeEvent = event.getNativeEvent();
                if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                    SuggestionsPopup.this.parentSelector.addItem(event.getValue());
                } else if (BrowserEvents.KEYDOWN.equals(nativeEvent.getType()) && (nativeEvent.getKeyCode() == KeyCodes.KEY_ENTER)) {
                    SuggestionsPopup.this.parentSelector.addItem(event.getValue());
                    selectedIndex = event.getIndex();
                    selectionModel.setSelected(event.getValue(), true);
                }
            }
        });
        parentSelector.suggestionsProvider.addDataDisplay(suggestionList);
        panel.add(suggestionList);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(suggestionList);

        panel.add(pager);
        panel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_CENTER);

        setWidget(panel);
    }

    public void selectPrevious() {
        suggestionList.setKeyboardSelectedRow(suggestionList.getKeyboardSelectedRow() - 1, false);
    }

    public void selectNext() {
        suggestionList.setKeyboardSelectedRow(suggestionList.getKeyboardSelectedRow() + 1, false);
    }

    public DataType getSelectedItem() {
        return selectionModel.getSelectedObject();
    }

    @Override
    public void hide() {
        parentSelector.popup = null;
        super.hide();
    }

    private Widget makeNoSuggestionsWidget() {
        Label noSuggestionsMessage = new Label(i18n.tr("No Suggestions"));
        noSuggestionsMessage.getElement().getStyle().setWidth(100, Unit.PCT);
        noSuggestionsMessage.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        noSuggestionsMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        return noSuggestionsMessage;
    }
}