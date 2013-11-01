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
package com.propertyvista.crm.client.ui.tools.n4generation.base;

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

public abstract class BulkOperationToolViewImpl<Settings extends IEntity, Item extends BulkEditableEntity, Holder extends BulkItemsHolder<Item>> extends
        AbstractPrimePane implements BulkOperationToolView<Settings, Item>, IsView {

    public enum Styles implements IStyleName {

        BulkOperationSettingsFormPanel, BulkOperationButtonsPanel, BulkOperationItemsHolderForm
    }

    private final static I18n i18n = I18n.get(BulkOperationToolViewImpl.class);

    private BulkOperationToolView.Presenter presenter;

    private final Class<Holder> holderClass;

    private final ItemsHolderForm<Item, Holder> itemsHolderForm;

    private final CEntityForm<Settings> settingsForm;

    private Range visibleRange;

    private Button acceptButton;

    private int pageIncrement;

    private Button searchButton;

    public BulkOperationToolViewImpl(String caption, CEntityForm<Settings> settingsForm, Class<Holder> holderClass,
            ItemsHolderForm<Item, Holder> itemsHolderForm) {

        this.pageIncrement = 10;

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
        buttonsPanel.add(searchButton = new Button(i18n.tr("Search"), new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.search();
            }
        }));
        viewPanel.add(buttonsPanel);

        this.holderClass = holderClass;
        this.itemsHolderForm = itemsHolderForm;
        itemsHolderForm.setOnMoreClicked(new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.showMore();
            }
        });
        itemsHolderForm.initContent();

        viewPanel.add(itemsHolderForm);

        addHeaderToolbarItem(acceptButton = new Button(i18n.tr("Accept Selected"), new Command() {
            @Override
            public void execute() {
                BulkOperationToolViewImpl.this.acceptMarked();
            }
        }));

        setCaption(caption);
        setContentPane(viewPanel);
        setSize("100%", "100%");

        visibleRange = new Range(0, pageIncrement);
    }

    @Override
    public void setBulkOperationEnabled(boolean isEnabled) {
        acceptButton.setEnabled(isEnabled);
    }

    @Override
    public void setSearchEnabled(boolean isEnabled) {
        searchButton.setEnabled(isEnabled);
        itemsHolderForm.setVisible(isEnabled);
    }

    @Override
    public void setRowData(int start, int total, List<Item> items) {
        Holder holder = createHolderEntity();
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
        visibleRange = new Range(0, pageIncrement);
    }

    @Override
    public Range getVisibleRange() {
        return visibleRange;
    }

    @Override
    public void setPresenter(BulkOperationToolView.Presenter presenter) {
        this.presenter = presenter;

        this.settingsForm.setVisited(false);

        this.itemsHolderForm.setVisited(false);
        this.itemsHolderForm.populateNew();
    }

    @Override
    public com.propertyvista.crm.client.ui.tools.n4generation.base.BulkOperationToolView.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public List<Item> getSelectedItems() {
        List<Item> selected = new LinkedList<Item>();
        for (Item item : itemsHolderForm.getValue().items()) {
            if (item.isSelected().isBooleanTrue()) {
                selected.add(item);
            }
        }
        return selected;
    }

    @Override
    public Settings getSettings() {
        return settingsForm.getValue();
    }

    @Override
    public void setLoading(boolean isLoading) {
        itemsHolderForm.setLoading(isLoading);
    }

    @Override
    public void showMessage(String message) {
        MessageDialog.info(message);
    }

    @Override
    public void setSettings(Settings settings) {
        settingsForm.populate(settings);
        settingsForm.setUnconditionalValidationErrorRendering(false);
        settingsForm.setVisited(false);
    }

    protected CEntityForm<Settings> getSettingsForm() {
        return settingsForm;
    }

    protected void setAcceptButtonCaption(String caption) {
        acceptButton.setCaption(caption);
    }

    protected void setPageIncrement(int pageIncrement) {
        this.pageIncrement = pageIncrement;
    }

    private Holder createHolderEntity() {
        return EntityFactory.create(holderClass);
    }

    private void acceptMarked() {
        visibleRange = new Range(0, pageIncrement);

        itemsHolderForm.setUnconditionalValidationErrorRendering(true);
        boolean isEditable = itemsHolderForm.isEditable(); // validations can fail only when form is editable so we force it to be editable
        itemsHolderForm.setEditable(true);
        boolean isValid = itemsHolderForm.isValid();
        itemsHolderForm.setEditable(isEditable);

        if (isValid) {
            presenter.acceptSelected();
        } else {
            MessageDialog.info(i18n.tr("Please fix the validation errors"));
        }

    }

    private void search() {
        settingsForm.setUnconditionalValidationErrorRendering(true);
        if (settingsForm.isValid()) {
            presenter.search();
        }
    }

    private void showMore() {
        this.visibleRange = new Range(0, itemsHolderForm.getValue() == null || itemsHolderForm.getValue().isNull() ? pageIncrement : itemsHolderForm.getValue()
                .items().size()
                + pageIncrement);
        this.presenter.updateVisibleItems();
    }

}
