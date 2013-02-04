/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-1-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.creditcheck;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;

public class CustomerCreditCheckLongReportForm extends CrmEntityForm<CustomerCreditCheckLongReportDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckLongReportForm.class);

    public CustomerCreditCheckLongReportForm(IFormView<CustomerCreditCheckLongReportDTO> view) {
        super(CustomerCreditCheckLongReportDTO.class, view);

        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("QUICK SUMMARY"));
        main.setWidget(++row, 0, createQuickSummary());

        main.setH1(++row, 0, 1, i18n.tr("IDENTITY"));
        main.setWidget(++row, 0, createQuickSummary());

        main.setH1(++row, 0, 1, i18n.tr("ACCOUNTS"));
        main.setWidget(++row, 0, createAccounts());

        main.setH1(++row, 0, 1, i18n.tr("COURT JUDGEMENTS"));
        main.setWidget(++row, 0, createCourtJudgements());

        main.setH1(++row, 0, 1, i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        main.setWidget(++row, 0, createProposals());

        main.setH1(++row, 0, 1, i18n.tr("EVICTIONS"));
        main.setWidget(++row, 0, createEvictions());

        main.setH1(++row, 0, 1, i18n.tr("RENT HISTORY"));
        main.setWidget(++row, 0, createRentHistory());

        main.setH1(++row, 0, 1, i18n.tr("COLLECTIONS"));
        main.setWidget(++row, 0, createCollections());

        main.setH1(++row, 0, 1, i18n.tr("INQUIRIES"));
        main.setWidget(++row, 0, createInquiries());

        selectTab(addTab(main));
    }

    private Widget createQuickSummary() {
        FormFlexPanel summary = new FormFlexPanel();

        int row = -1;
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().percentOfRentCovered()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().grossMonthlyIncome()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalAccounts()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalOutstandingBalance()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfBancruptciesOrActs()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfLegalItems()), 20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingCollectionsBalance()), 20).build());

        row = -1;
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().monthlyIncomeToRentRatio()), 20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().estimatedDebtandRentPayments()), 20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().accountsWithNoLatePayments()), 20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().outstandingRevolvingDebt()), 20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().numberOfEvictions()), 20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().landlordCollectionsFiled()), 20).build());

        summary.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);
        summary.setWidget(++row, 1, inject(proto().landlordCollectionsFiled()));

        FormFlexPanel accounts = new FormFlexPanel();
        int col = -1;
        accounts.setWidget(0, ++col, new HTML(i18n.tr("Accounts paid late")));
        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments1_30days()), 20).layout(Layout.vertical).build());
        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments31_60days()), 20).layout(Layout.vertical).build());
        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments61_90days()), 20).layout(Layout.vertical).build());

        FormFlexPanel equiifax = new FormFlexPanel();
        col = -1;
        equiifax.setWidget(0, ++col, new HTML(i18n.tr("Equifax")));
        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxCheckScore()), 20).layout(Layout.vertical).build());
        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxRatingLevel()), 20).layout(Layout.vertical).build());
        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxRiskLevel()), 20).layout(Layout.vertical).build());

        FormFlexPanel raitings = new FormFlexPanel();
        col = -1;
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating1()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating2()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating3()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating4()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating5()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating6()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating7()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating8()), 10).labelWidth(5).layout(Layout.vertical).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating9()), 10).labelWidth(5).layout(Layout.vertical).build());

        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, summary);
        main.setWidget(1, 0, accounts);
        main.setWidget(2, 0, equiifax);
        main.setWidget(3, 0, raitings);

        return main;
    }

    private Widget createAccounts() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createCourtJudgements() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createProposals() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createEvictions() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createRentHistory() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    private Widget createInquiries() {
        // TODO Auto-generated method stub
        return null;
    }
}
