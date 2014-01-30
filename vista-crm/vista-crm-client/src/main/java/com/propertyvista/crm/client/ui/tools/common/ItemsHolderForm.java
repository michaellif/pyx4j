/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.CheckBox;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;

public abstract class ItemsHolderForm<Item extends BulkEditableEntity, Holder extends BulkItemsHolder<Item>> extends CEntityForm<Holder> {

    private final static I18n i18n = I18n.get(ItemsHolderForm.class);

    public enum Styles implements IStyleName {

        BulkStatsPanel, BulkActionsPanel, BulkSelectAllBox, BulkEverythingIsSelected, BulkSuperCaptionsPanel, BulkFolderHolder, AutoPayLoadMore

    }

    private FlowPanel statsPanel;

    private HTML counterPanel;

    private Widget headerPanel;

    private Anchor toggleSelectEverythingAnchor;

    private HTML moreButton;

    private CheckBox checkAllVisibleItems;

    private boolean isSelectAllSet;

    private FlowPanel actionsPanel;

    private Command onMoreClicked;

    private FlowPanel folderHolder;

    public ItemsHolderForm(Class<Holder> klass) {
        super(klass);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.add(createStatsPanel());
        panel.add(headerPanel = createHeaderPanel());
        panel.add(createActionsPanel());
        panel.add(createItemsFolderPanel());
        return panel;
    }

    public boolean isSelectAllSet() {
        return isSelectAllSet;
    }

    public void setLoading(boolean isLoading) {
        moreButton.setHTML(isLoading ? i18n.tr("Loading...") : i18n.tr("More..."));
    }

    public void setOnMoreClicked(Command command) {
        this.onMoreClicked = command;
    }

    public void toggleSelectAll(boolean selectAll) {
        isSelectAllSet = selectAll;

        checkAllVisibleItems.setValue(isSelectAllSet);
        checkAllVisibleItems.setEditable(!isSelectAllSet);
        setEditable(!isSelectAllSet);
        checkAll(isSelectAllSet);

        renderStatsPanel();
    }

    protected abstract Widget createHeaderPanel();

    protected abstract CEntityFolder<Item> createItemsFolder();

    @Override
    protected Holder preprocessValue(Holder value, boolean fireEvent, boolean populate) {
        if (isSelectAllSet) {
            for (Item item : value.items()) {
                item.isSelected().setValue(true);
            }
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        checkAllVisibleItems.setValue(false);
        renderStatsPanel();
        moreButton.setVisible(!getValue().isNull() && (getValue().totalItemCount().getValue() != getValue().items().size()));
        actionsPanel.setVisible(!getValue().isNull() && getValue().totalItemCount().getValue() != 0);
        headerPanel.setVisible(!getValue().isNull() && (!getValue().totalItemCount().isNull() && getValue().totalItemCount().getValue() != 0));
        folderHolder.setVisible(!getValue().isNull() && (!getValue().totalItemCount().isNull() && getValue().totalItemCount().getValue() != 0));
    }

    private FlowPanel createStatsPanel() {
        statsPanel = new FlowPanel();
        statsPanel.addStyleName(Styles.BulkStatsPanel.name());

        counterPanel = new HTML();
        statsPanel.add(counterPanel);

        // the caption of this anchor is defined dynamically when the form is populated and based on user's actions 
        toggleSelectEverythingAnchor = new Anchor("", new Command() {
            @Override
            public void execute() {
                toggleSelectAll(!isSelectAllSet);
            }
        });
        statsPanel.add(toggleSelectEverythingAnchor);
        return statsPanel;
    }

    private FlowPanel createActionsPanel() {
        actionsPanel = new FlowPanel();
        actionsPanel.setStyleName(Styles.BulkActionsPanel.name());

        checkAllVisibleItems = new CheckBox();
        checkAllVisibleItems.addStyleName(Styles.BulkSelectAllBox.name());
        checkAllVisibleItems.setTitle(i18n.tr(i18n.tr("Check/Uncheck all visible items")));
        checkAllVisibleItems.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                checkAll();
            }
        });
        actionsPanel.add(checkAllVisibleItems);

        return actionsPanel;
    }

    private FlowPanel createItemsFolderPanel() {
        folderHolder = new FlowPanel();
        folderHolder.setStyleName(Styles.BulkFolderHolder.name());
        folderHolder.add(inject(proto().items(), createItemsFolder()));

        moreButton = new HTML(i18n.tr("More..."));
        moreButton.setStyleName(Styles.AutoPayLoadMore.name());
        moreButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ItemsHolderForm.this.onMoreClicked();
            }
        });
        folderHolder.add(moreButton);

        return folderHolder;
    }

    private void checkAll() {
        checkAll(checkAllVisibleItems.getValue());
        renderStatsPanel();
    }

    private void checkAll(boolean isChecked) {
        CComponent<?> c = get(proto().items());
        BulkItemsFolder<Item> folder = (BulkItemsFolder<Item>) c;
        folder.checkAll(isChecked);
    }

    private void renderStatsPanel() {
        if (getValue().isNull()) {
            statsPanel.setVisible(false);
        } else {
            statsPanel.setVisible(true);
            statsPanel.setStyleName(Styles.BulkEverythingIsSelected.name(), isSelectAllSet);
            if (getValue().totalItemCount().getValue() > 0) {
                if (!isSelectAllSet) {
                    if (checkAllVisibleItems.getValue() == true) {
                        counterPanel.setText(i18n.tr("All {0,number,#,##0} items on this page are selected.", getValue().items().size(), getValue()
                                .totalItemCount().getValue()));
                        if (getValue().totalItemCount().getValue() != getValue().items().size()) {
                            toggleSelectEverythingAnchor.setVisible(true);
                            toggleSelectEverythingAnchor.setText(i18n.tr("Select all {0,number,#,##0} items", getValue().totalItemCount().getValue()));
                        }
                    } else {
                        counterPanel.setText(i18n.tr("Displaying {0,number,#,##0} of {1,number,#,##0} items", getValue().items().size(), getValue()
                                .totalItemCount().getValue()));
                        toggleSelectEverythingAnchor.setVisible(false);
                        toggleSelectEverythingAnchor.setText("");
                    }
                } else {
                    toggleSelectEverythingAnchor.setVisible(true);
                    toggleSelectEverythingAnchor.setText(i18n.tr("Clear selection"));
                    counterPanel.setText(i18n.tr("All {0,number,#,##0} items are selected.", getValue().totalItemCount().getValue()));
                }
            } else {
                counterPanel.setText(i18n.tr("No results were found"));
            }
        }

    }

    private void onMoreClicked() {
        if (onMoreClicked != null) {
            onMoreClicked.execute();
        }
    }
}