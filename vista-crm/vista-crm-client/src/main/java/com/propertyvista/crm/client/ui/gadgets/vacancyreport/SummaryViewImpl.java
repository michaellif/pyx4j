/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-23
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;

public class SummaryViewImpl implements SummaryView {
    public static final String SUMMARY_CAPTION = "Summary";

    public final CrmEntityForm<UnitVacancyReportSummaryDTO> form;

    private SummaryFilteringCriteria summaryFilteringCriteria;

    private Presenter presenter;

    public SummaryViewImpl() {
        form = new CrmEntityForm<UnitVacancyReportSummaryDTO>(UnitVacancyReportSummaryDTO.class, new CrmViewersComponentFactory()) {

            @Override
            public IsWidget createContent() {
//                FormFlexPanel main = new FormFlexPanel();
//
//                VerticalPanel p = new VerticalPanel();
//                p.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
//                p.setWidth("100%");
//
//                Label caption = new Label(i18n.tr(SUMMARY_CAPTION));
//                caption.setWidth("100%");
//                p.add(caption);
//
//                main.setWidget(1, 0, inject(proto().total()));
//                main.setWidget(2, 0, inject(proto().netExposure()));
//                main.setWidget(3, 0, new VistaLineSeparator());
//
//                main.setWidget(4, 0, inject(proto().vacancyAbsolute()));
//                main.setWidget(5, 0, inject(proto().vacancyRelative()));
//                main.setWidget(6, 0, inject(proto().vacantRented()));
//                main.setWidget(4, 1, inject(proto().noticeAbsolute()));
//                main.setWidget(5, 1, inject(proto().noticeRelative()));
//                main.setWidget(6, 1, inject(proto().noticeRented()));
//
//                main.setWidget(7, 0, new VistaLineSeparator());
//                main.getFlexCellFormatter().setColSpan(7, 0, 2);
//
//                main.setWidget(8, 0, inject(proto().occupancyAbsolute()));
//                main.setWidget(8, 1, inject(proto().occupancyRelative()));
//                main.setWidth("100%");
//                p.add(main);
//                return p;
                final int WIDTH = 10;
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);
                main.setWidth("100%");

                VerticalPanel p = new VerticalPanel();
                p.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
                p.setWidth("100%");

                Label caption = new Label(i18n.tr(SUMMARY_CAPTION));
                caption.setWidth("100%");
                p.add(caption);
                main.add(p);
                main.add(inject(proto().total()), WIDTH);
                main.add(inject(proto().netExposure()), WIDTH);
                main.add(new VistaLineSeparator());

                VistaDecoratorsSplitFlowPanel sp = new VistaDecoratorsSplitFlowPanel(true);

                sp.getLeftPanel().add(inject(proto().vacancyAbsolute()), WIDTH);
                sp.getLeftPanel().add(inject(proto().vacancyRelative()), WIDTH);
                sp.getLeftPanel().add(inject(proto().vacantRented()), WIDTH);
                sp.getLeftPanel().add(new VistaLineSeparator());
                sp.getLeftPanel().add(inject(proto().occupancyAbsolute()), WIDTH);
                sp.getLeftPanel().add(inject(proto().occupancyRelative()), WIDTH);

                sp.getRightPanel().add(inject(proto().noticeAbsolute()), WIDTH);
                sp.getRightPanel().add(inject(proto().noticeRelative()), WIDTH);
                sp.getRightPanel().add(inject(proto().noticeRented()), WIDTH);

                main.add(sp);

                return new CrmScrollPanel(main);
            }
        };
        form.initContent();
    }

    @Override
    public void populateSummary(UnitVacancyReportSummaryDTO summary) {
        form.populate(summary);
    }

    @Override
    public SummaryFilteringCriteria getSummaryFilteringCriteria() {
        return summaryFilteringCriteria;
    }

    public void setSummaryFilteringCriteria(SummaryFilteringCriteria criteria) {
        summaryFilteringCriteria = criteria;
        if (presenter != null) {
            presenter.populateSummary();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        if (this.presenter != null) {
            this.presenter.populateSummary();
        }
    }

    @Override
    public boolean isEnabled() {
        return form.isVisible();
    }

    @Override
    public void reportError(Throwable error) {
        // TODO Auto-generated method stub
    }

    @Override
    public Widget asWidget() {
        return form.asWidget();
    }
}