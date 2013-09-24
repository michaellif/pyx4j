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
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.reports.autopay.AutoPayChangesReportSettingsForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewsHolderDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewViewImpl extends AbstractPrimePane implements AutoPayReviewView, IsView {

    public enum Styles implements IStyleName {

        AutoPayReviewsFiltersFormPanel, AutoPayReviewsFiltersButtonsPanel, AutoPayReviewsHolderForm

    }

    private final static I18n i18n = I18n.get(AutoPayReviewViewImpl.class);

    private static final int PAGE_INCREMENT = 10;

    private AutoPayReviewView.Presenter presenter;

    private final PapReviewsHolderForm papReviewHolderForm;

    private Range visibleRange;

    private final AutoPayChangesReportSettingsForm settingsForm;

    public AutoPayReviewViewImpl() {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        viewPanel.setSize("100%", "100%");

        FlowPanel filtersPanel = new FlowPanel();
        filtersPanel.setStyleName(Styles.AutoPayReviewsFiltersFormPanel.name());

        settingsForm = new AutoPayChangesReportSettingsForm();
        settingsForm.initContent();
        settingsForm.populateNew();
        filtersPanel.add(settingsForm);
        viewPanel.add(filtersPanel);

        FlowPanel filterButtonsPanel = new FlowPanel();
        filterButtonsPanel.setStyleName(Styles.AutoPayReviewsFiltersButtonsPanel.name());
        filterButtonsPanel.add(new Button(i18n.tr("Search"), new Command() {
            @Override
            public void execute() {
                AutoPayReviewViewImpl.this.search();
            }
        }));
        viewPanel.add(filterButtonsPanel);

        papReviewHolderForm = new PapReviewsHolderForm() {
            @Override
            public void onMoreClicked() {
                AutoPayReviewViewImpl.this.showMore();
            }
        };
        papReviewHolderForm.initContent();
        papReviewHolderForm.asWidget().setStyleName(Styles.AutoPayReviewsHolderForm.name());

        viewPanel.add(papReviewHolderForm);

        addHeaderToolbarItem(new Button(i18n.tr("Accept Selected"), new Command() {
            @Override
            public void execute() {
                AutoPayReviewViewImpl.this.acceptMarked();
            }
        }));

        setCaption(i18n.tr("Suspended AutoPays Review"));
        setContentPane(viewPanel);
        setSize("100%", "100%");

        visibleRange = new Range(0, PAGE_INCREMENT);
    }

    @Override
    public void setRowData(int start, int total, List<PapReviewDTO> values) {
        PapReviewsHolderDTO holder = EntityFactory.create(PapReviewsHolderDTO.class);
        holder.papReviewsTotalCount().setValue(total);
        holder.papReviews().addAll(values);

        this.papReviewHolderForm.setValue(holder, false);
        this.visibleRange = new Range(0, values.size());
    }

    @Override
    public boolean isEverythingSelected() {
        return this.papReviewHolderForm.isSelectAllSet();
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
    public void setPresenter(AutoPayReviewView.Presenter presenter) {
        this.settingsForm.setVisited(false);
        this.settingsForm.populateNew(); // TODO this is not supposed to be here: settings must be populated by presenter too
        this.papReviewHolderForm.setVisited(false);
        this.papReviewHolderForm.setValue(EntityFactory.create(PapReviewsHolderDTO.class));
        this.presenter = presenter;
    }

    @Override
    public List<PapReviewDTO> getMarkedPapReviews() {
        List<PapReviewDTO> selected = new LinkedList<PapReviewDTO>();
        for (PapReviewDTO review : papReviewHolderForm.getValue().papReviews()) {
            if (review.isSelected().isBooleanTrue()) {
                selected.add(review);
            }
        }
        return selected;
    }

    @Override
    public AutoPayChangesReportMetadata getAutoPayFilterSettings() {
        return settingsForm.getValue();
    }

    private void acceptMarked() {
        visibleRange = new Range(0, PAGE_INCREMENT);

        papReviewHolderForm.setUnconditionalValidationErrorRendering(true);
        boolean isEditable = papReviewHolderForm.isEditable(); // validations can fail only when form is editable so we force it to be editable
        papReviewHolderForm.setEditable(true);
        boolean isValid = papReviewHolderForm.isValid();
        papReviewHolderForm.setEditable(isEditable);

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
        this.visibleRange = new Range(0, papReviewHolderForm.getValue() == null || papReviewHolderForm.getValue().isNull() ? PAGE_INCREMENT
                : papReviewHolderForm.getValue().papReviews().size() + PAGE_INCREMENT);
        this.presenter.onRangeChanged();
    }

    @Override
    public void setLoading(boolean isLoading) {
        papReviewHolderForm.setLoading(isLoading);
    }

    @Override
    public void showMessage(String message) {
        MessageDialog.info(message);
    }

}
