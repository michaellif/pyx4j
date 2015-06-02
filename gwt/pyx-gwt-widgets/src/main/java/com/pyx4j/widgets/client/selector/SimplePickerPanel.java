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
 */
package com.pyx4j.widgets.client.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class SimplePickerPanel<E> extends ScrollPanel implements IPickerPanel<E> {

    private static final I18n i18n = I18n.get(SimplePickerPanel.class);

    private HandlerRegistration handlerRegistration;

    private final IOptionsGrabber<E> optionsGrabber;

    private final IFormatter<E, SafeHtml> optionFormatter;

    private final HTML noMatchesLabel;

    private final int limit = 20;

    private final static int SUGGESTIONS_PER_PAGE = 14;

    private final CellTable<E> table;

    private final SingleSelectionModel<E> selectionModel;

    private List<E> suggestions;

    private String query;

    public SimplePickerPanel(IOptionsGrabber<E> optionsGrabber, IFormatter<E, SafeHtml> optionFormatter) {
        this.optionsGrabber = optionsGrabber;
        this.optionFormatter = optionFormatter;

        FlowPanel optionsHolder = new FlowPanel();

        setStyleName(WidgetsTheme.StyleName.SelectionPickerPanel.name());

        table = new CellTable<E>();
        table.addColumn(new PickerColumn());

        selectionModel = new SingleSelectionModel<>();
        table.setSelectionModel(selectionModel);

        table.getElement().getStyle().setWidth(100, Unit.PCT);

        table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        optionsHolder.add(table);

        noMatchesLabel = new HTML(i18n.tr("No Matches"));
        noMatchesLabel.setStyleName(WidgetsTheme.StyleName.SelectionPickerPanelNoMatchesLabel.name());

        optionsHolder.add(noMatchesLabel);

        setWidget(optionsHolder);

        getElement().getStyle().setProperty("maxHeight", "200px");

    }

    @Override
    public void setPickerPopup(final PickerPopup<E> pickerPopup) {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
        if (pickerPopup != null) {

            handlerRegistration = selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {
                    if (selectionModel.getSelectedObject() != null) {
                        pickerPopup.pickSelection();
                    }
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
        this.query = query;
        noMatchesLabel.setVisible(false);

        if (ignoreOptions != null && ignoreOptions.size() != 0 && options != null) {
            options.removeAll(ignoreOptions);
        }

        selectionModel.clear();

        if (options == null) {
            suggestions = new ArrayList<E>();
        } else {
            suggestions = new ArrayList<E>(options);
        }
        table.setRowData(suggestions.subList(0, suggestions.size() < SUGGESTIONS_PER_PAGE ? suggestions.size() : SUGGESTIONS_PER_PAGE));

        if (options != null && options.size() > 0) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    table.setKeyboardSelectedRow(0, false);
                }
            });
            noMatchesLabel.setVisible(false);
        } else {
            noMatchesLabel.setVisible(true);
        }

    }

    @Override
    public void moveSelectionDown() {
        int index = table.getKeyboardSelectedRow();
        if (index < table.getRowCount() - 1) {
            table.setKeyboardSelectedRow(index + 1, false);
            table.getRowElement(index + 1).scrollIntoView();
        }
    }

    @Override
    public void moveSelectionUp() {
        int index = table.getKeyboardSelectedRow();
        if (index > 0) {
            table.setKeyboardSelectedRow(index - 1, false);
            table.getRowElement(index - 1).scrollIntoView();
        }
    }

    @Override
    public E getSelection() {
        if (selectionModel.getSelectedObject() != null) {
            return selectionModel.getSelectedObject();
        } else if (table.getKeyboardSelectedRow() >= 0) {
            return suggestions == null ? null : suggestions.get(table.getKeyboardSelectedRow());
        } else {
            return null;
        }
    }

    class PickerColumn extends Column<E, SafeHtml> {

        public PickerColumn() {
            super(new SafeHtmlCell());
        }

        @Override
        public SafeHtml getValue(E value) {
            SafeHtml formattedValue = optionFormatter.format(value);
            return query.equals("") ? formattedValue : OptionQueryHighlighter.highlight(formattedValue, query,
                    SimplePickerPanel.this.optionsGrabber.getSelectType());
        }
    }
}
