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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.IForm;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.AccountDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.CollectionDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.EvictionDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.InquiryDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.JudgementDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.ProposalDTO;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO.RentDTO;

public class CustomerCreditCheckLongReportForm extends CrmEntityForm<CustomerCreditCheckLongReportDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckLongReportForm.class);

    public CustomerCreditCheckLongReportForm(IForm<CustomerCreditCheckLongReportDTO> view) {
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
        main.setWidget(++row, 0, inject(proto().judgements(), new JudgementFolder()));

        main.setH1(++row, 0, 1, i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        main.setWidget(++row, 0, inject(proto().proposals(), new ProposalFolder()));

// Not implemented in Equifax:
//        
//        main.setH1(++row, 0, 1, i18n.tr("EVICTIONS"));
//        main.setWidget(++row, 0, inject(proto().evictions(), new EvictionFolder()));
//
//        main.setH1(++row, 0, 1, i18n.tr("RENT HISTORY"));
//        main.setWidget(++row, 0, inject(proto().rents(), new RentFolder()));

        main.setH1(++row, 0, 1, i18n.tr("COLLECTIONS"));
        main.setWidget(++row, 0, inject(proto().collections(), new CollectionFolder()));

        main.setH1(++row, 0, 1, i18n.tr("INQUIRIES"));
        main.setWidget(++row, 0, inject(proto().inquiries(), new InquiryFolder()));

        selectTab(addTab(main));

        // Add do not print marker:
        main.getElement().addClassName(StyleManger.DO_NOT_PRINT_CLASS_NAME);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().identity().deathDate()).setVisible(!getValue().identity().deathDate().isNull());
    }

    private Widget createQuickSummary() {
        FormFlexPanel summary = new FormFlexPanel();
        int row = -1;
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().percentOfRentCovered()), 10).labelWidth(20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalAccounts()), 10).labelWidth(20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalOutstandingBalance()), 10).labelWidth(20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingRevolvingDebt()), 10).labelWidth(20).build());
        summary.setWidget(++row, 0, new DecoratorBuilder(inject(proto().outstandingCollectionsBalance()), 10).labelWidth(20).build());

        row = 0;
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().accountsWithNoLatePayments()), 10).labelWidth(20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().numberOfLegalItems()), 10).labelWidth(20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().numberOfBancruptciesOrActs()), 10).labelWidth(20).build());
        summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().landlordCollectionsFiled()), 10).labelWidth(20).build());
// Not implemented in Equifax:
//      summary.setWidget(++row, 1, new DecoratorBuilder(inject(proto().numberOfEvictions()), 10).labelWidth(20).build());

        summary.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        FormFlexPanel accounts = new FormFlexPanel();
        int col = -1;
        accounts.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Accounts paid late:") + "</i>"));
        accounts.getWidget(0, col).setWidth("15em");
        accounts.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments1_30days()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments31_60days()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        accounts.setWidget(0, ++col, new DecoratorBuilder(inject(proto().latePayments61_90days()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());

        FormFlexPanel equiifax = new FormFlexPanel();
        col = -1;
        equiifax.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Equifax:") + "</i>"));
        equiifax.getWidget(0, col).setWidth("15em");
        equiifax.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxCheckScore()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxRatingLevel()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        equiifax.setWidget(0, ++col, new DecoratorBuilder(inject(proto().equifaxRiskLevel()), 15).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());

        FormFlexPanel raitings = new FormFlexPanel();
        col = -1;
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating1()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating2()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating3()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating4()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating5()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating6()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating7()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating8()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());
        raitings.setWidget(0, ++col, new DecoratorBuilder(inject(proto().rating9()), 3).labelWidth(3).layout(Layout.vertical).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build());

        // put all together:
        FormFlexPanel main = new FormFlexPanel();
        row = -1;

        main.setWidget(++row, 0, summary);
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, accounts);
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, equiifax);
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, raitings);

        return main;
    }

    private Widget createIdentity() {
        FormFlexPanel name = new FormFlexPanel();

        name.setWidget(0, 0, inject(proto().identity().name(), new NameEditor(i18n.tr("Name"))));
        name.setWidget(1, 0, new DecoratorBuilder(inject(proto().identity().SIN()), 20).build());

        name.setWidget(0, 1, new DecoratorBuilder(inject(proto().identity().birthDate()), 10).build());
        name.setWidget(1, 1, new DecoratorBuilder(inject(proto().identity().maritalStatus()), 10).build());
        name.setWidget(2, 1, new DecoratorBuilder(inject(proto().identity().deathDate()), 10).build());

        name.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        FormFlexPanel address = new FormFlexPanel();
        address.setWidget(0, 0, new HTML("<i>" + i18n.tr("Current:") + "</i>"));
        address.getWidget(0, 0).getElement().getStyle().setPaddingLeft(16, Unit.EM);
        address.setWidget(1, 0, inject(proto().identity().currentAddress(), new AddressSimpleEditor()));

        address.setWidget(0, 1, new HTML("<i>" + i18n.tr("Former:") + "</i>"));
        address.getWidget(0, 1).getElement().getStyle().setPaddingLeft(16, Unit.EM);
        address.setWidget(1, 1, inject(proto().identity().formerAddress(), new AddressSimpleEditor()));

        address.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        FormFlexPanel employement = new FormFlexPanel();
        employement.setWidget(0, 0, new HTML("<i>" + i18n.tr("Current:") + "</i>"));
        employement.getWidget(0, 0).getElement().getStyle().setPaddingLeft(16, Unit.EM);
        employement.setWidget(1, 0, new DecoratorBuilder(inject(proto().identity().currentEmployer()), 20).build());
        employement.setWidget(2, 0, new DecoratorBuilder(inject(proto().identity().currentOccupation()), 20).build());

        employement.setWidget(0, 1, new HTML("<i>" + i18n.tr("Former:") + "</i>"));
        employement.getWidget(0, 1).getElement().getStyle().setPaddingLeft(16, Unit.EM);
        employement.setWidget(1, 1, new DecoratorBuilder(inject(proto().identity().formerEmployer()), 20).build());
        employement.setWidget(2, 1, new DecoratorBuilder(inject(proto().identity().formerOccupation()), 20).build());

        employement.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        // put all together:
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, name);

        main.setH3(1, 0, 0, i18n.tr("Addresses"));
        main.setWidget(2, 0, address);

        main.setH3(3, 0, 0, i18n.tr("Employement"));
        main.setWidget(4, 0, employement);

        return main;
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
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().name()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().number()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().creditAmount()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().balanceAmount()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().lastPayment()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col, new DecoratorBuilder(inject(proto().code()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().type()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());

                main.setHR(1, 0, col + 1);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().paymentRate()), 20).build());
                main.getFlexCellFormatter().setColSpan(2, 0, 2);

                main.setWidget(2, 1, new DecoratorBuilder(inject(proto().paymentType()), 15).build());
                main.getFlexCellFormatter().setColSpan(2, 1, 3);

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
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().caseNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerName()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().status()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().dateFiled()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().dateSatisfied()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().plaintiff()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().defendants()), 40).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col);

                return main;
            }
        }
    }

    private class ProposalFolder extends VistaBoxFolder<ProposalDTO> {

        public ProposalFolder() {
            super(ProposalDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof ProposalDTO) {
                return new ProposalViewer();
            }
            return super.create(member);
        }

        private class ProposalViewer extends CEntityDecoratableForm<ProposalDTO> {

            public ProposalViewer() {
                super(ProposalDTO.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().caseNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerName()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().dispositionDate()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().liabilityAmount()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().assetAmount()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().caseNumberAndTrustee()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().intentOrDisposition()), 25).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col);

                return main;
            }
        }
    }

    private class EvictionFolder extends VistaBoxFolder<EvictionDTO> {

        public EvictionFolder() {
            super(EvictionDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof EvictionDTO) {
                return new EvictionViewer();
            }
            return super.create(member);
        }

        private class EvictionViewer extends CEntityDecoratableForm<EvictionDTO> {

            public EvictionViewer() {
                super(EvictionDTO.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().caseNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).componentAlignment(Alignment.right).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerNumber()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().customerName()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().dateFiled()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().judgementDate()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().judgment()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().plaintiff()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().defendants()), 40).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col);

                main.setWidget(3, 0, inject(proto().address(), new AddressSimpleEditor()));
                main.getFlexCellFormatter().setColSpan(3, 0, col);

                return main;
            }
        }
    }

    private class RentFolder extends VistaBoxFolder<RentDTO> {

        public RentFolder() {
            super(RentDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof RentDTO) {
                return new RentViewer();
            }
            return super.create(member);
        }

        private class RentViewer extends CEntityDecoratableForm<RentDTO> {

            public RentViewer() {
                super(RentDTO.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().landlord()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().rent()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().writeOffs()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().noticeGiven()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().latePayments()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().NSFChecks()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());

                main.setWidget(1, 0, new DecoratorBuilder(inject(proto().lastUpdated()), 25).build());
                main.getFlexCellFormatter().setColSpan(1, 0, col);

                main.setWidget(2, 0, new DecoratorBuilder(inject(proto().from()), 10).build());
                main.setWidget(2, 1, new DecoratorBuilder(inject(proto().to()), 10).build());
                main.getFlexCellFormatter().setColSpan(2, 0, col / 2);
                main.getFlexCellFormatter().setColSpan(2, 1, col / 2);

                main.setWidget(3, 0, inject(proto().address(), new AddressSimpleEditor()));
                main.getFlexCellFormatter().setColSpan(3, 0, col);

                return main;
            }
        }
    }

    private class CollectionFolder extends VistaBoxFolder<CollectionDTO> {

        public CollectionFolder() {
            super(CollectionDTO.class, false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof CollectionDTO) {
                return new CollectionViewer();
            }
            return super.create(member);
        }

        private class CollectionViewer extends CEntityDecoratableForm<CollectionDTO> {

            public CollectionViewer() {
                super(CollectionDTO.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int col = -1;
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().onBehalf()), 20).labelWidth(20).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().date()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().lastActive()), 10).labelWidth(10).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().originalAmount()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right)
                                .build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().balance()), 10).labelWidth(10).layout(Layout.vertical).componentAlignment(Alignment.right).build());
                main.setWidget(0, ++col,
                        new DecoratorBuilder(inject(proto().status()), 15).labelWidth(15).layout(Layout.vertical).labelAlignment(Alignment.center)
                                .componentAlignment(Alignment.center).build());

                return main;
            }
        }
    }

    private class InquiryFolder extends VistaTableFolder<InquiryDTO> {

        public InquiryFolder() {
            super(InquiryDTO.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().onBehalf(), "25em"),
                    new EntityFolderColumnDescriptor(proto().date(), "10em"),
                    new EntityFolderColumnDescriptor(proto().customerNumber(), "15em"),
                    new EntityFolderColumnDescriptor(proto().phone(), "10em"));
              //@formatter:on        
        }
    }
}
