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

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.reports.autopay.AutoPayChangesReportSettingsForm;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapReviewsHolderDTO;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayReviewUpdaterViewImpl extends AbstractPrimePane implements AutoPayReviewUpdaterView, IsView {

    private final static I18n i18n = I18n.get(AutoPayReviewUpdaterViewImpl.class);

    private static final int PAGE_INCREMENT = 10;

    private AutoPayReviewUpdaterView.Presenter presenter;

    private final PapReviewsHolderForm leasePapsReviewsHolderForm;

    private Range visibleRange;

    private final AutoPayChangesReportSettingsForm filtersForm;

    /**
     * 
     */
    public AutoPayReviewUpdaterViewImpl() {
        this.visibleRange = new Range(0, PAGE_INCREMENT);

        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        viewPanel.setSize("100%", "100%");
        setContentPane(viewPanel);
        setSize("100%", "100%");

        FlowPanel filtersPanel = new FlowPanel();
        filtersPanel.setHeight("150px");
        filtersPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

        filtersForm = new AutoPayChangesReportSettingsForm();
        filtersForm.initContent();
        filtersForm.populateNew();
        filtersPanel.add(filtersForm);

        FlowPanel filterButtonsPanel = new FlowPanel();
        filterButtonsPanel.setWidth("100%");
        filterButtonsPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        filterButtonsPanel.add(new Button(i18n.tr("Apply")));
        filtersPanel.add(filterButtonsPanel);

        viewPanel.add(filtersPanel);

        leasePapsReviewsHolderForm = new PapReviewsHolderForm() {
            @Override
            public void onMoreClicked() {
                AutoPayReviewUpdaterViewImpl.this.showMore();
            }
        };
        leasePapsReviewsHolderForm.initContent();
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setPosition(Position.ABSOLUTE);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setTop(150, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setLeft(0, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setRight(0, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setBottom(0, Unit.PX);

        viewPanel.add(leasePapsReviewsHolderForm);

        addHeaderToolbarItem(new Button(i18n.tr("Accept Marked"), new Command() {
            @Override
            public void execute() {
                presenter.acceptSelected();
            }
        }));
        addHeaderToolbarItem(new Separator(6));
        addHeaderToolbarItem(new Button(i18n.tr("Export")));
    }

    @Override
    public void setRowData(int start, int total, List<PapReviewDTO> values) {
        PapReviewsHolderDTO holder = EntityFactory.create(PapReviewsHolderDTO.class);
        holder.papReviewsTotalCount().setValue(total);
        holder.papReviews().addAll(values);
        leasePapsReviewsHolderForm.populate(holder);
    }

    @Override
    public Range getVisibleRange() {
        return visibleRange;
    }

    @Override
    public void setPresenter(AutoPayReviewUpdaterView.Presenter presenter) {
        this.presenter = presenter;
        this.presenter.onRangeChanged();
    }

    @Override
    public List<PapReviewDTO> selectedRows() {
        List<PapReviewDTO> selected = new LinkedList<PapReviewDTO>();
        for (PapReviewDTO review : leasePapsReviewsHolderForm.getValue().papReviews()) {
            if (review.isSelected().isBooleanTrue()) {
                selected.add(review);
            }
        }
        return selected;
    }

    @Override
    public AutoPayChangesReportMetadata getAutoPayFilterSettings() {
        return filtersForm.getValue();
    }

    private void showMore() {
        this.visibleRange = new Range(0, leasePapsReviewsHolderForm.getValue() == null || leasePapsReviewsHolderForm.getValue().isNull() ? PAGE_INCREMENT
                : leasePapsReviewsHolderForm.getValue().papReviews().size() + PAGE_INCREMENT);
        this.presenter.onRangeChanged();
    }

    private class Separator extends HTML {

        public Separator(int x) {
            String sep = "&nbsp;";
            for (int i = 0; i < x - 1; ++i) {
                sep += "&nbsp;";
            }
            setHTML(sep);
            getElement().getStyle().setCursor(Cursor.DEFAULT);
        }
    }

}
