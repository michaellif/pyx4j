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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
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
                int row = 0;
                FormFlexPanel main = new FormFlexPanel();
                main.setH1(row++, 0, 2, i18n.tr(SUMMARY_CAPTION));

                main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().total())).build());

                main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().netExposure())).build());
                //main.setWidget(row, 0, new VistaLineSeparator());
                //main.getFlexCellFormatter().setColSpan(row++, 0, 2);

//                main.setWidget(row, 0, new DecoratorBuilder(inject(proto().vacancyAbsolute())).build());
//                main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().noticeAbsolute())).build());
//
//                main.setWidget(row, 0, new DecoratorBuilder(inject(proto().vacancyRelative())).build());
//                main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().noticeRelative())).build());
//                main.setWidget(row, 0, new DecoratorBuilder(inject(proto().vacantRented())).build());
//
//                main.setWidget(row++, 1, new DecoratorBuilder(inject(proto().noticeRented())).build());
//
//                //main.setWidget(row, 0, new VistaLineSeparator());
//                //main.getFlexCellFormatter().setColSpan(row++, 0, 2);
//
//                main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().occupancyAbsolute())).build());
//                main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().occupancyRelative())).build());

                main.getColumnFormatter().setWidth(0, "50%");
                main.getColumnFormatter().setWidth(1, "50%");
//                main.setWidth("100%");

                return main;
//                final int WIDTH = 10;
//                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(true);
//                main.setWidth("100%");
//
//                VerticalPanel p = new VerticalPanel();
//                p.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
//                p.setWidth("100%");
//
//                Label caption = new Label(i18n.tr(SUMMARY_CAPTION));
//                caption.setWidth("100%");
//                p.add(caption);
//                main.add(p);
//                main.add(inject(proto().total()), WIDTH);
//                main.add(inject(proto().netExposure()), WIDTH);
//                main.add(new VistaLineSeparator());
//
//                VistaDecoratorsSplitFlowPanel sp = new VistaDecoratorsSplitFlowPanel(true);
//
//                sp.getLeftPanel().add(inject(proto().vacancyAbsolute()), WIDTH);
//                sp.getLeftPanel().add(inject(proto().vacancyRelative()), WIDTH);
//                sp.getLeftPanel().add(inject(proto().vacantRented()), WIDTH);
//                sp.getLeftPanel().add(new VistaLineSeparator());
//                sp.getLeftPanel().add(inject(proto().occupancyAbsolute()), WIDTH);
//                sp.getLeftPanel().add(inject(proto().occupancyRelative()), WIDTH);
//
//                sp.getRightPanel().add(inject(proto().noticeAbsolute()), WIDTH);
//                sp.getRightPanel().add(inject(proto().noticeRelative()), WIDTH);
//                sp.getRightPanel().add(inject(proto().noticeRented()), WIDTH);
//
//                main.add(sp);

//                return new CrmScrollPanel(main);
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