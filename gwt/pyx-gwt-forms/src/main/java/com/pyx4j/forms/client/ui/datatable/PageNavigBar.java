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
package com.pyx4j.forms.client.ui.datatable;

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.images.DataTableImages;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.TargetLabel;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class PageNavigBar extends Toolbar {

    private static final I18n i18n = I18n.get(PageNavigBar.class);

    private final Label countLabel;

    private final Button firstButton;

    private final Button prevButton;

    private final Button nextButton;

    private final Button lastButton;

    private final HorizontalPanel pageSizeContentPanel;

    protected final ListBox pageSizeSelector;

    protected List<Integer> pageSizeOptions;

    private HandlerRegistration firstActionHandlerRegistration;

    private HandlerRegistration prevActionHandlerRegistration;

    private HandlerRegistration nextActionHandlerRegistration;

    private HandlerRegistration lastActionHandlerRegistration;

    private Command pageSizeActionCommand;

    private final DataTableActionsBar actionsBar;

    public PageNavigBar(final DataTableActionsBar actionsBar) {
        this.actionsBar = actionsBar;

        getElement().getStyle().setProperty("textAlign", "right");

        firstButton = new Button(DataTableImages.INSTANCE.first());
        firstButton.setVisible(false);
        firstButton.getElement().getStyle().setMarginRight(0, Unit.PX);
        addItem(firstButton);

        prevButton = new Button(DataTableImages.INSTANCE.prev());
        prevButton.setVisible(false);
        prevButton.getElement().getStyle().setMarginRight(5, Unit.PX);
        addItem(prevButton);

        countLabel = new Label(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8), true);
        countLabel.getElement().getStyle().setMarginRight(5, Unit.PX);
        countLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        addItem(countLabel);

        nextButton = new Button(DataTableImages.INSTANCE.next());
        nextButton.setVisible(false);
        nextButton.getElement().getStyle().setMarginRight(0, Unit.PX);
        addItem(nextButton);

        lastButton = new Button(DataTableImages.INSTANCE.last());
        lastButton.setVisible(false);
        addItem(lastButton);

        pageSizeContentPanel = new HorizontalPanel();
        pageSizeContentPanel.getElement().getStyle().setMarginRight(12, Unit.PX);
        pageSizeContentPanel.setVisible(false);
        pageSizeSelector = new ListBox();

        TargetLabel pageSizeLabel = new TargetLabel(i18n.tr("Page Size") + ":", pageSizeSelector);
        pageSizeContentPanel.add(pageSizeLabel);
        pageSizeContentPanel.setCellVerticalAlignment(pageSizeLabel, HorizontalPanel.ALIGN_MIDDLE);

        pageSizeContentPanel.add(pageSizeSelector);
        pageSizeSelector.getElement().getStyle().setMarginLeft(3, Unit.PX);
        addItem(pageSizeContentPanel);

        pageSizeSelector.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                if (actionsBar.getDataTableModel() != null) {
                    actionsBar.getDataTableModel().setPageSize(Integer.valueOf(pageSizeSelector.getValue(pageSizeSelector.getSelectedIndex())));
                    // Actually fire event
                    if (pageSizeActionCommand != null) {
                        pageSizeActionCommand.execute();
                    }
                }
            }
        });
    }

    public void setFirstActionCommand(final Command firstActionCommand) {
        if (firstActionHandlerRegistration != null) {
            firstActionHandlerRegistration.removeHandler();
        }
        if (firstActionCommand != null) {
            firstActionHandlerRegistration = firstButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    firstActionCommand.execute();
                }
            });
        } else {
            firstActionHandlerRegistration = null;
        }

    }

    public void setPrevActionCommand(final Command prevActionCommand) {
        if (prevActionHandlerRegistration != null) {
            prevActionHandlerRegistration.removeHandler();
        }
        if (prevActionCommand != null) {
            prevActionHandlerRegistration = prevButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    prevActionCommand.execute();
                }
            });
        } else {
            prevActionHandlerRegistration = null;
        }

    }

    public void setNextActionCommand(final Command nextActionCommand) {
        if (nextActionHandlerRegistration != null) {
            nextActionHandlerRegistration.removeHandler();
        }
        if (nextActionCommand != null) {
            nextActionHandlerRegistration = nextButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    nextActionCommand.execute();
                }
            });
        } else {
            nextActionHandlerRegistration = null;
        }
    }

    public void setLastActionCommand(final Command lastActionCommand) {
        if (lastActionHandlerRegistration != null) {
            lastActionHandlerRegistration.removeHandler();
        }
        if (lastActionCommand != null) {
            lastActionHandlerRegistration = lastButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    lastActionCommand.execute();
                }
            });
        } else {
            lastActionHandlerRegistration = null;
        }

    }

    public void setPageSizeActionCommand(Command pageSizeActionCommand) {
        this.pageSizeActionCommand = pageSizeActionCommand;
    }

    public void onTableModelChanged(DataTableModelEvent e) {
        int from = actionsBar.getDataTableModel().getPageNumber() * actionsBar.getDataTableModel().getPageSize() + 1;
        int to = from + actionsBar.getDataTableModel().getData().size() - 1;
        int of = actionsBar.getDataTableModel().getTotalRows();
        if (from > to) {
            countLabel.setText(String.valueOf(CommonsStringUtils.NO_BREAK_SPACE_UTF8));
        } else {
            countLabel.setText(i18n.tr("{0}-{1} of {2}", from, to, of));
        }

        boolean fitsOnOnePage = actionsBar.getDataTableModel().getPageSize() >= of;
        prevButton.setVisible(!fitsOnOnePage);
        firstButton.setVisible(!fitsOnOnePage);
        nextButton.setVisible(!fitsOnOnePage);
        lastButton.setVisible(!fitsOnOnePage);

        prevButton.setEnabled(prevActionHandlerRegistration != null && actionsBar.getDataTableModel().getPageNumber() > 0);
        firstButton.setEnabled(prevActionHandlerRegistration != null && actionsBar.getDataTableModel().getPageNumber() > 0);
        nextButton.setEnabled(nextActionHandlerRegistration != null && actionsBar.getDataTableModel().hasMoreData());
        lastButton.setEnabled(nextActionHandlerRegistration != null && actionsBar.getDataTableModel().hasMoreData());

        if (pageSizeOptions != null) {
            pageSizeSelector.setSelectedIndex(pageSizeOptions.indexOf(actionsBar.getDataTableModel().getPageSize()));
            pageSizeContentPanel.setVisible(pageSizeOptions.get(0) < of);
        }
    }

    public void setPageSizeOptions(List<Integer> pageSizeOptions) {
        this.pageSizeOptions = pageSizeOptions;
        pageSizeContentPanel.setVisible(this.pageSizeOptions != null);

        pageSizeSelector.clear();
        if (this.pageSizeOptions != null) {
            for (Integer size : pageSizeOptions) {
                pageSizeSelector.addItem(String.valueOf(size));
            }
        }
    }
}
