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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.EntityCSSClass;

public class DataTableActionsBar extends HorizontalPanel implements DataTableModelListener {

    private DataTableModel<?> model;

    private final Anchor prevAnchor;

    private final Anchor nextAnchor;

    private final Label countLabel;

    private HandlerRegistration prevActionHandlerRegistration;

    private HandlerRegistration nextActionHandlerRegistration;

    public DataTableActionsBar() {
        setStyleName(EntityCSSClass.pyx4j_Entity_DataTableActionsBar.name());
        setWidth("100%");
        HorizontalPanel contentPanel = new HorizontalPanel();
        add(contentPanel);
        setCellHorizontalAlignment(contentPanel, HorizontalPanel.ALIGN_RIGHT);

        prevAnchor = new Anchor("&lt;&nbsp;Prev", true);
        prevAnchor.setVisible(false);
        prevAnchor.getElement().getStyle().setMarginRight(10, Unit.PX);
        contentPanel.add(prevAnchor);

        countLabel = new Label(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8), true);
        countLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
        countLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        contentPanel.add(countLabel);

        nextAnchor = new Anchor("Next&nbsp;&gt;", true);
        nextAnchor.setVisible(false);
        contentPanel.add(nextAnchor);

        getElement().getStyle().setProperty("textAlign", "right");
        getElement().getStyle().setProperty("padding", "6px");

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
            countLabel.setText(from + "-" + to);
        }

        nextAnchor.setVisible(nextActionHandlerRegistration != null && model.hasMoreData());
    }

}
