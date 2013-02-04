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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.AccountDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.JudgementDTO;
import com.propertyvista.domain.person.Name;

public class CustomerCreditCheckLongReportForm extends CrmEntityForm<CustomerCreditCheckLongReportDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckLongReportForm.class);

    public CustomerCreditCheckLongReportForm(IFormView<CustomerCreditCheckLongReportDTO> view) {
        super(CustomerCreditCheckLongReportDTO.class, view);

        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("QUICK SUMMARY"));
        main.setWidget(++row, 0, createQuickSummary());

        main.setH1(++row, 0, 1, i18n.tr("IDENTITY"));
        main.setWidget(++row, 0, createIdentity());

        main.setH1(++row, 0, 1, i18n.tr("ACCOUNTS"));
        main.setWidget(++row, 0, inject(proto().accounts(), new AccountFolder()));

        main.setH1(++row, 0, 1, i18n.tr("COURT JUDGEMENTS"));
        main.setWidget(++row, 0, inject(proto().accounts(), new JudgementFolder()));

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

        // put all together:
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, summary);
        main.setWidget(1, 0, accounts);
        main.setWidget(2, 0, equiifax);
        main.setWidget(3, 0, raitings);

        return main;
    }

    private Widget createIdentity() {
        FormFlexPanel name = new FormFlexPanel();

        int row = -1;
        name.setWidget(++row, 0, new DecoratorBuilder(inject(proto().identityName(), new NameEditor()), 20).build());
        name.setWidget(++row, 0, new DecoratorBuilder(inject(proto().identitySIN()), 20).build());
        name.setWidget(++row, 0, new DecoratorBuilder(inject(proto().identityMarritialStatus()), 20).build());
        row = -1;
        name.setWidget(++row, 1, new DecoratorBuilder(inject(proto().identityBirthDate()), 20).build());
        name.setWidget(++row, 1, new DecoratorBuilder(inject(proto().identityDeathDate()), 20).build());

        name.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        FormFlexPanel address = new FormFlexPanel();
        address.setWidget(0, 0, new DecoratorBuilder(inject(proto().identityBirthDate(), new AddressSimpleEditor()), 20).layout(Layout.vertical).build());
        address.setWidget(0, 1, new DecoratorBuilder(inject(proto().identityDeathDate(), new AddressSimpleEditor()), 20).layout(Layout.vertical).build());

        address.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        FormFlexPanel employement = new FormFlexPanel();
        employement.setWidget(0, 0, new DecoratorBuilder(inject(proto().identityCurrentEmployer()), 20).build());
        employement.setWidget(0, 1, new DecoratorBuilder(inject(proto().identityCurrentOccupation()), 20).build());

        employement.setWidget(1, 0, new DecoratorBuilder(inject(proto().identityFormerEmployer()), 20).build());
        employement.setWidget(1, 1, new DecoratorBuilder(inject(proto().identityFormerOccupation()), 20).build());

        employement.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        // put all together:
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, name);
        main.setWidget(1, 0, address);
        main.setWidget(2, 0, employement);

        return main;
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

    private class AccountFolder extends VistaBoxFolder<AccountDTO> {

        public AccountFolder() {
            super(AccountDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof AccountDTO) {
                return new AccountViewer();
            }
            return super.create(member);
        }

        private class AccountViewer extends CEntityDecoratableForm<AccountDTO> {

            public AccountViewer() {
                super(AccountDTO.class);
                setEditable(false);
                setViewable(true);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().name()), 20).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().number()), 15).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().creditAmount()), 10).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().balanceAmount()), 10).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().lastPaymentDate()), 10).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().radeCode()), 5).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().radeType()), 5).layout(Layout.vertical).build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().paymentRate()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().paymentType()), 25).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col);

                return main;
            }
        }
    }

    private class JudgementFolder extends VistaBoxFolder<JudgementDTO> {

        public JudgementFolder() {
            super(JudgementDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof JudgementDTO) {
                return new JudgementViewer();
            }
            return super.create(member);
        }

        private class JudgementViewer extends CEntityDecoratableForm<JudgementDTO> {

            public JudgementViewer() {
                super(JudgementDTO.class);
                setEditable(false);
                setViewable(true);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().caseNumber()), 15).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().customerNumber()), 15).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().personName(), new NameEditor()), 20).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().status()), 10).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().dateFiled()), 10).layout(Layout.vertical).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().dateSatisfied()), 10).layout(Layout.vertical).build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().plaintiff(), new NameEditor()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().defendants(), new VistaBoxFolder<Name>(Name.class, false) {
                    @Override
                    public CComponent<?, ?> create(IObject<?> member) {
                        if (member instanceof Name) {
                            return new NameEditor();
                        }
                        return super.create(member);
                    }
                }), 25).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col);

                return main;
            }
        }
    }
}
