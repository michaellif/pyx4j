/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Dec 1, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.TargetLabel;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class PageNavigBar extends Toolbar {

    private static I18n i18n = I18n.get(PageNavigBar.class);

    private final Label countLabel;

    private final Anchor prevAnchor;

    private final Anchor nextAnchor;

    private final HorizontalPanel pageSizeContentPanel;

    protected final ListBox pageSizeSelector;

    protected List<Integer> pageSizeOptions;

    private HandlerRegistration prevActionHandlerRegistration;

    private HandlerRegistration nextActionHandlerRegistration;

    private ClickHandler pageSizeClickHandler;

    private final DataTableActionsBar actionsBar;

    public PageNavigBar(final DataTableActionsBar actionsBar) {
        this.actionsBar = actionsBar;
        pageSizeContentPanel = new HorizontalPanel();
        pageSizeContentPanel.getElement().getStyle().setMarginRight(12, Unit.PX);
        pageSizeContentPanel.setVisible(false);
        pageSizeSelector = new ListBox();

        pageSizeContentPanel.add(new TargetLabel(i18n.tr("Page Size") + ":", pageSizeSelector));
        pageSizeContentPanel.add(pageSizeSelector);
        pageSizeSelector.getElement().getStyle().setMarginLeft(3, Unit.PX);
        addItem(pageSizeContentPanel);

        prevAnchor = new Anchor("&lt;&nbsp;" + i18n.tr("Prev"), true, Anchor.DEFAULT_HREF);
        prevAnchor.setEnabled(false);
        prevAnchor.getElement().getStyle().setMarginRight(10, Unit.PX);
        addItem(prevAnchor);

        countLabel = new Label(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8), true);
        countLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
        countLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        addItem(countLabel);

        nextAnchor = new Anchor(i18n.tr("Next") + "&nbsp;&gt;", true, Anchor.DEFAULT_HREF);
        nextAnchor.setEnabled(false);
        addItem(nextAnchor);

        getElement().getStyle().setProperty("textAlign", "right");

        pageSizeSelector.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (actionsBar.getDataTableModel() != null) {
                    actionsBar.getDataTableModel().setPageSize(Integer.valueOf(pageSizeSelector.getValue(pageSizeSelector.getSelectedIndex())));
                    // Actually fire event
                    if (pageSizeClickHandler != null) {
                        pageSizeClickHandler.onClick(null);
                    }
                }
            }
        });
    }

    public void setPrevActionHandler(ClickHandler prevActionHandler) {
        if (prevActionHandlerRegistration != null) {
            prevActionHandlerRegistration.removeHandler();
        }
        if (prevActionHandler != null) {
            prevActionHandlerRegistration = prevAnchor.addClickHandler(prevActionHandler);
        } else {
            prevActionHandlerRegistration = null;
        }

    }

    public void setNextActionHandler(ClickHandler nextActionHandler) {
        if (nextActionHandlerRegistration != null) {
            nextActionHandlerRegistration.removeHandler();
        }
        if (nextActionHandler != null) {
            nextActionHandlerRegistration = nextAnchor.addClickHandler(nextActionHandler);
        } else {
            nextActionHandlerRegistration = null;
        }
    }

    public void setPageSizeActionHandler(ClickHandler clickHandler) {
        pageSizeClickHandler = clickHandler;
    }

    public void onTableModelChanged(DataTableModelEvent e) {
        prevAnchor.setEnabled(prevActionHandlerRegistration != null && actionsBar.getDataTableModel().getPageNumber() > 0);
        int from = actionsBar.getDataTableModel().getPageNumber() * actionsBar.getDataTableModel().getPageSize() + 1;
        int to = from + actionsBar.getDataTableModel().getData().size() - 1;
        if (from > to) {
            countLabel.setText(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8));
        } else if (from == to) {
            countLabel.setText(String.valueOf(from));
        } else {
            countLabel.setText(i18n.tr("{0}-{1} of {2}", from, to, actionsBar.getDataTableModel().getTotalRows()));
        }

        nextAnchor.setEnabled(nextActionHandlerRegistration != null && actionsBar.getDataTableModel().hasMoreData());

        if (pageSizeOptions != null) {
            pageSizeSelector.setSelectedIndex(pageSizeOptions.indexOf(actionsBar.getDataTableModel().getPageSize()));
        }
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        this.pageSizeOptions = pageSizeOptions;
        pageSizeContentPanel.setVisible(this.pageSizeOptions != null);
        if (this.pageSizeOptions != null) {
            for (Integer size : pageSizeOptions) {
                pageSizeSelector.addItem(String.valueOf(size));
            }
        }
    }
}
