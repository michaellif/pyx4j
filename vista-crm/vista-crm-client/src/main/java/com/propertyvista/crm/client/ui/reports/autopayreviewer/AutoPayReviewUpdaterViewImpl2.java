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

import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.Range;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.IsView;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.reports.autopay.AutoPayChangesReportSettingsForm;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewsHolderDTO;

public class AutoPayReviewUpdaterViewImpl2 extends AbstractPrimePane implements AutoPayReviewUpdaterView, IsView {

    private final static I18n i18n = I18n.get(AutoPayReviewUpdaterViewImpl2.class);

    private AutoPayReviewUpdaterView.Presenter presenter;

    private final LeasePapsReviewsHolderForm leasePapsReviewsHolderForm;

    /**
     * 
     */
    public AutoPayReviewUpdaterViewImpl2() {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.getElement().getStyle().setPosition(Position.RELATIVE);
        viewPanel.setSize("100%", "100%");
        setContentPane(viewPanel);
        setSize("100%", "100%");

        FlowPanel filtersPanel = new FlowPanel();
        filtersPanel.setHeight("150px");
        filtersPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

        AutoPayChangesReportSettingsForm filtersForm = new AutoPayChangesReportSettingsForm();
        filtersForm.initContent();
        filtersForm.populateNew();
        filtersPanel.add(filtersForm);

        FlowPanel filterButtonsPanel = new FlowPanel();
        filterButtonsPanel.setWidth("100%");
        filterButtonsPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        filterButtonsPanel.add(new Button(i18n.tr("Apply")));
        filtersPanel.add(filterButtonsPanel);

        viewPanel.add(filtersPanel);

        leasePapsReviewsHolderForm = new LeasePapsReviewsHolderForm();
        leasePapsReviewsHolderForm.initContent();
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setPosition(Position.ABSOLUTE);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setTop(150, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setLeft(0, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setRight(0, Unit.PX);
        leasePapsReviewsHolderForm.asWidget().getElement().getStyle().setBottom(0, Unit.PX);

        viewPanel.add(leasePapsReviewsHolderForm);

        addHeaderToolbarItem(new Button(i18n.tr("Accept All")));
        addHeaderToolbarItem(new Separator(3));
        addHeaderToolbarItem(new Button(i18n.tr("Accept Marked")));
        addHeaderToolbarItem(new Button(i18n.tr("Accept Not Marked")));
        addHeaderToolbarItem(new Separator(6));
        addHeaderToolbarItem(new Button(i18n.tr("Export")));
    }

    @Override
    public void setRowData(int start, List<LeasePapsReviewDTO> values) {
        LeasePapsReviewsHolderDTO holder = EntityFactory.create(LeasePapsReviewsHolderDTO.class);
        holder.leaseAutoPayReviewsTotalCount().setValue(123512);
        holder.leaseAutoPayReviews().addAll(values);
        leasePapsReviewsHolderForm.populate(holder);
    }

    @Override
    public Range getVisibleRange() {
        // TODO
        return new Range(0, 3);
    }

    @Override
    public void setPresenter(AutoPayReviewUpdaterView.Presenter presenter) {
        this.presenter = presenter;
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
