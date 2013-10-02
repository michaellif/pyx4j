/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.n4generation;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;

public abstract class BulkOperationToolViewImpl<Settings extends IEntity, Item extends BulkEditableEntity> extends AbstractPrimePane implements
        BulkOperationToolView<Settings, Item>, IsView {

    public enum Styles implements IStyleName {

        BulkOperationSettingsFormPanel, BulkOperationButtonsPanel, BulkOperationItemsHolderForm
    }

    private final static I18n i18n = I18n.get(BulkOperationToolViewImpl.class);

    private static final int PAGE_INCREMENT = 10;

    private BulkOperationToolView.Presenter presenter;

    private final ItemsHolderForm<Item> itemsHolderForm;

    private Range visibleRange;

    private final CEntityForm<Settings> settingsForm;

    public BulkOperationToolViewImpl(CEntityForm<Settings> settingsForm, ItemsHolderForm<Item> itemsHolderForm) {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        viewPanel.setSize("100%", "100%");

        FlowPanel settingsFormPanel = new FlowPanel();
        settingsFormPanel.setStyleName(Styles.BulkOperationSettingsFormPanel.name());

        this.settingsForm = settingsForm;
        this.settingsForm.initContent();
        this.settingsForm.populateNew();
        settingsFormPanel.add(settingsForm);
        viewPanel.add(settingsFormPanel);

        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.setStyleName(Styles.BulkOperationButtonsPanel.name());
        buttonsPanel.add(new Button(i18n.tr("Search"), new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.search();
            }
        }));
        viewPanel.add(buttonsPanel);

        this.itemsHolderForm = itemsHolderForm;
        itemsHolderForm.setOnMoreClicked(new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.showMore();
            }
        });
        itemsHolderForm.initContent();

        viewPanel.add(itemsHolderForm);

        addHeaderToolbarItem(new Button(i18n.tr("Accept Selected"), new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.acceptMarked();
            }
        }));

        setCaption(i18n.tr("Suspended AutoPays Review"));
        setContentPane(viewPanel);
        setSize("100%", "100%");

        visibleRange = new Range(0, PAGE_INCREMENT);
    }

    @Override
    public void setRowData(int start, int total, List<Item> items) {
        BulkItemsHolder holder = EntityFactory.create(BulkItemsHolder.class);
        holder.totalItemCount().setValue(total);
        holder.items().addAll(items);

        this.itemsHolderForm.setValue(holder, false);
        this.visibleRange = new Range(0, items.size());
    }

    @Override
    public boolean isEverythingSelected() {
        return this.itemsHolderForm.isSelectAllSet();
    }

    @Override
    public void resetVisibleRange() {
        visibleRange = new Range(0, PAGE_INCREMENT);
    }

    @Override
    public Range getVisibleRange() {
        return visibleRange;
    }

    @Override
    public void setPresenter(BulkOperationToolView.Presenter presenter) {
        this.settingsForm.setVisited(false);
        this.settingsForm.populateNew(); // TODO this is not supposed to be here: settings must be populated by presenter too
        this.itemsHolderForm.setVisited(false);
        this.itemsHolderForm.populateNew();
        this.presenter = presenter;
    }

    @Override
    public List<Item> getMarkedItems() {
        List<Item> selected = new LinkedList<Item>();
        for (Item item : itemsHolderForm.getValue().items()) {
            if (item.isSelected().isBooleanTrue()) {
                selected.add(item);
            }
        }
        return selected;
    }

    @Override
    public Settings getFilterSettings() {
        return settingsForm.getValue();
    }

    private void acceptMarked() {
        visibleRange = new Range(0, PAGE_INCREMENT);

        itemsHolderForm.setUnconditionalValidationErrorRendering(true);
        boolean isEditable = itemsHolderForm.isEditable(); // validations can fail only when form is editable so we force it to be editable
        itemsHolderForm.setEditable(true);
        boolean isValid = itemsHolderForm.isValid();
        itemsHolderForm.setEditable(isEditable);

        if (isValid) {
            presenter.acceptMarked();
        } else {
            MessageDialog.info(i18n.tr("Please fix the validation errors"));
        }

    }

    private void search() {
        settingsForm.setUnconditionalValidationErrorRendering(true);
        if (settingsForm.isValid()) {
            presenter.populate();
        }
    }

    private void showMore() {
        this.visibleRange = new Range(0, itemsHolderForm.getValue() == null || itemsHolderForm.getValue().isNull() ? PAGE_INCREMENT : itemsHolderForm
                .getValue().items().size()
                + PAGE_INCREMENT);
        this.presenter.onRangeChanged();
    }

    @Override
    public void setLoading(boolean isLoading) {
        itemsHolderForm.setLoading(isLoading);
    }

    @Override
    public void showMessage(String message) {
        MessageDialog.info(message);
    }

}
