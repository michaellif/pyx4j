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
 * Created on Feb 24, 2010
 * @author Michael
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.TargetLabel;

public class DataTableActionsBar extends HorizontalPanel implements DataTableModelListener {

    private static I18n i18n = I18n.get(DataTableActionsBar.class);

    private DataTableModel<?> model;

    private final Label countLabel;

    private final Anchor prevAnchor;

    private final Anchor nextAnchor;

    private final HorizontalPanel pageSizeContentPanel;

    protected final ListBox pageSizeSelector;

    protected List<Integer> pageSizeOptions;

    private HandlerRegistration prevActionHandlerRegistration;

    private HandlerRegistration nextActionHandlerRegistration;

    private ClickHandler pageSizeClickHandler;

    public DataTableActionsBar() {
        setStyleName(DataTable.BASE_NAME + DataTable.StyleSuffix.ActionsBar);
        setWidth("100%");
        HorizontalPanel contentPanel = new HorizontalPanel();
        add(contentPanel);
        setCellHorizontalAlignment(contentPanel, HorizontalPanel.ALIGN_RIGHT);

        pageSizeContentPanel = new HorizontalPanel();
        pageSizeContentPanel.getElement().getStyle().setMarginRight(10, Unit.PX);
        pageSizeContentPanel.setVisible(false);
        pageSizeSelector = new ListBox();
        pageSizeContentPanel.add(new TargetLabel(i18n.tr("Page Size:"), pageSizeSelector));
        pageSizeContentPanel.add(pageSizeSelector);
        pageSizeSelector.getElement().getStyle().setMarginLeft(3, Unit.PX);
        contentPanel.add(pageSizeContentPanel);

        prevAnchor = new Anchor(i18n.tr("&lt;&nbsp;Prev"), true);
        prevAnchor.setVisible(false);
        prevAnchor.getElement().getStyle().setMarginRight(10, Unit.PX);
        contentPanel.add(prevAnchor);

        countLabel = new Label(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8), true);
        countLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
        countLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        contentPanel.add(countLabel);

        nextAnchor = new Anchor(i18n.tr("Next&nbsp;&gt;"), true);
        nextAnchor.setVisible(false);
        contentPanel.add(nextAnchor);

        getElement().getStyle().setProperty("textAlign", "right");
        getElement().getStyle().setProperty("padding", "6px");

        pageSizeSelector.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (model != null) {
                    model.setPageSize(Integer.valueOf(pageSizeSelector.getValue(pageSizeSelector.getSelectedIndex())));
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

    public Anchor insertActionItem(String name, IDebugId debugId, ClickHandler handler) {
        Anchor anchor = new Anchor(name, false);
        anchor.getElement().getStyle().setMarginRight(10, Unit.PX);
        anchor.addClickHandler(handler);
        if (debugId != null) {
            anchor.ensureDebugId(debugId.debugId());
        } else {
            anchor.ensureDebugId(name);
        }
        this.insert(anchor, 0);
        return anchor;
    }

    public void setDataTableModel(DataTableModel<?> model) {
        if (this.model != null) {
            this.model.removeDataTableModelListener(this);
        }
        this.model = model;
        model.addDataTableModelListener(this);
    }

    @Override
    public void onTableModelChanged(DataTableModelEvent e) {
        prevAnchor.setVisible(prevActionHandlerRegistration != null && model.getPageNumber() > 0);
        int from = model.getPageNumber() * model.getPageSize() + 1;
        int to = from + model.getData().size() - 1;
        if (from > to) {
            countLabel.setText(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8));
        } else if (from == to) {
            countLabel.setText(String.valueOf(from));
        } else {
            countLabel.setText(i18n.tr("{0}-{1} of {2}", from, to, model.getTotalRows()));
        }

        nextAnchor.setVisible(nextActionHandlerRegistration != null && model.hasMoreData());

        if (pageSizeOptions != null) {
            pageSizeSelector.setSelectedIndex(pageSizeOptions.indexOf(model.getPageSize()));
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
