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

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setH1(++row, 0, 2, i18n.tr("QUICK SUMMARY"));
        main.setWidget(++row, 0, 2, createQuickSummary());

        main.setH1(++row, 0, 2, i18n.tr("IDENTITY"));
        main.setWidget(++row, 0, 2, createIdentity());

        main.setH1(++row, 0, 2, i18n.tr("ACCOUNTS"));
        main.setWidget(++row, 0, 2, inject(proto().accounts(), new AccountFolder()));

        main.setH1(++row, 0, 2, i18n.tr("COURT JUDGEMENTS"));
        main.setWidget(++row, 0, 2, inject(proto().judgements(), new JudgementFolder()));

        main.setH1(++row, 0, 2, i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        main.setWidget(++row, 0, 2, inject(proto().proposals(), new ProposalFolder()));

// Not implemented in Equifax:
//        
//        main.setH1(++row, 0, 2, i18n.tr("EVICTIONS"));
//        main.setWidget(++row, 0, 2, inject(proto().evictions(), new EvictionFolder()));
//
//        main.setH1(++row, 0, 2, i18n.tr("RENT HISTORY"));
//        main.setWidget(++row, 0, 2, inject(proto().rents(), new RentFolder()));

        main.setH1(++row, 0, 2, i18n.tr("COLLECTIONS"));
        main.setWidget(++row, 0, 2, inject(proto().collections(), new CollectionFolder()));

        main.setH1(++row, 0, 2, i18n.tr("INQUIRIES"));
        main.setWidget(++row, 0, 2, inject(proto().inquiries(), new InquiryFolder()));

        selectTab(addTab(main));
        setTabBarVisible(false);

        // Add do not print marker:
        main.getElement().addClassName(StyleManager.DO_NOT_PRINT_CLASS_NAME);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().identity().deathDate()).setVisible(!getValue().identity().deathDate().isNull());
    }

    private Widget createQuickSummary() {
        TwoColumnFlexFormPanel summary = new TwoColumnFlexFormPanel();

        int row = -1;
        summary.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().percentOfRentCovered()), 20, 10, 20).build());
        summary.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().totalAccounts()), 20, 10, 20).build());
        summary.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().totalOutstandingBalance()), 20, 10, 20).build());
        summary.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().outstandingRevolvingDebt()), 20, 10, 20).build());
        summary.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().outstandingCollectionsBalance()), 20, 10, 20).build());

        row = 0;
        summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().accountsWithNoLatePayments()), 20, 10, 20).build());
        summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().numberOfLegalItems()), 20, 10, 20).build());
        summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().numberOfBancruptciesOrActs()), 20, 10, 20).build());
        summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().landlordCollectionsFiled()), 20, 10, 20).build());
// Not implemented in Equifax:
//      summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().numberOfEvictions()), 20, 10, 20).build());

        BasicFlexFormPanel accounts = new BasicFlexFormPanel();
        int col = -1;
        accounts.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Accounts paid late:") + "</i>"));
        accounts.getWidget(0, col).setWidth("15em");
        accounts.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        accounts.setWidget(0, ++col, alignToCenter(inject(proto().latePayments1_30days()), 20));
        accounts.setWidget(0, ++col, alignToCenter(inject(proto().latePayments31_60days()), 20));
        accounts.setWidget(0, ++col, alignToCenter(inject(proto().latePayments61_90days()), 20));

        BasicFlexFormPanel equiifax = new BasicFlexFormPanel();
        col = -1;
        equiifax.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Equifax:") + "</i>"));
        equiifax.getWidget(0, col).setWidth("15em");
        equiifax.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        equiifax.setWidget(0, ++col, alignToCenter(inject(proto().equifaxCheckScore()), 20));
        equiifax.setWidget(0, ++col, alignToCenter(inject(proto().equifaxRatingLevel()), 20));
        equiifax.setWidget(0, ++col, alignToCenter(inject(proto().equifaxRiskLevel()), 20));

        BasicFlexFormPanel raitings = new BasicFlexFormPanel();
        col = -1;
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating1()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating2()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating3()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating4()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating5()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating6()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating7()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating8()), 8));
        raitings.setWidget(0, ++col, alignToCenter(inject(proto().rating9()), 8));

        // put all together:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        row = -1;

        main.setWidget(++row, 0, 2, summary);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, accounts);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, equiifax);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, raitings);

        return main;
    }

    private Widget createIdentity() {
        BasicFlexFormPanel name = new BasicFlexFormPanel();

        name.setWidget(0, 0, 2, inject(proto().identity().name(), new NameEditor(i18n.tr("Name"))));

        name.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().identity().SIN()), 20).build());
        name.setWidget(2, 0, new FormDecoratorBuilder(inject(proto().identity().maritalStatus()), 10).build());

        name.setWidget(1, 1, new FormDecoratorBuilder(inject(proto().identity().birthDate()), 10).build());
        name.setWidget(2, 1, new FormDecoratorBuilder(inject(proto().identity().deathDate()), 10).build());

        BasicFlexFormPanel address = new BasicFlexFormPanel();
        address.setWidget(0, 0, new HTML("<i>" + i18n.tr("Current:") + "</i>"));
        address.getWidget(0, 0).getElement().getStyle().setPaddingRight(4, Unit.EM);
        address.setWidget(1, 0, inject(proto().identity().currentAddress(), new AddressSimpleEditor()));

        address.setWidget(0, 1, new HTML("<i>" + i18n.tr("Former:") + "</i>"));
        address.getWidget(0, 1).getElement().getStyle().setPaddingRight(4, Unit.EM);
        address.setWidget(1, 1, inject(proto().identity().formerAddress(), new AddressSimpleEditor()));

        BasicFlexFormPanel employement = new BasicFlexFormPanel();
        employement.setWidget(0, 0, new HTML("<i>" + i18n.tr("Current:") + "</i>"));
        employement.getWidget(0, 0).getElement().getStyle().setPaddingRight(4, Unit.EM);
        employement.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().identity().currentEmployer()), 20).build());
        employement.setWidget(4, 0, new FormDecoratorBuilder(inject(proto().identity().currentOccupation()), 20).build());

        employement.setWidget(0, 1, new HTML("<i>" + i18n.tr("Former:") + "</i>"));
        employement.getWidget(0, 1).getElement().getStyle().setPaddingRight(4, Unit.EM);
        employement.setWidget(1, 1, new FormDecoratorBuilder(inject(proto().identity().formerEmployer()), 20).build());
        employement.setWidget(2, 1, new FormDecoratorBuilder(inject(proto().identity().formerOccupation()), 20).build());

        // put all together:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        main.setWidget(0, 0, name);

        main.setH3(1, 0, 0, i18n.tr("Addresses"));
        main.setWidget(2, 0, address);

        main.setH3(3, 0, 0, i18n.tr("Employement"));
        main.setWidget(4, 0, employement);

        return main;
    }

    private WidgetDecorator alignToCenter(CComponent<?> comp, double width) {
        return new FormDecoratorBuilder(comp, width, width, width).labelPosition(LabelPosition.top).labelAlignment(Alignment.center)
                .componentAlignment(Alignment.center).build();
    }

    private class AccountFolder extends VistaBoxFolder<AccountDTO> {

        public AccountFolder() {
            super(AccountDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof AccountDTO) {
                return new AccountViewer();
            }
            return super.create(member);
        }

        private class AccountViewer extends CEntityForm<AccountDTO> {

            public AccountViewer() {
                super(AccountDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, alignToCenter(inject(proto().name()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().number()), 15));
                table.setWidget(0, ++col, alignToCenter(inject(proto().creditAmount()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().balanceAmount()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().lastPayment()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().code()), 5));
                table.setWidget(0, ++col, alignToCenter(inject(proto().type()), 10));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, new FormDecoratorBuilder(inject(proto().paymentRate()), 25).build());
                main.setWidget(2, 1, new FormDecoratorBuilder(inject(proto().paymentType()), 25).build());

                return main;
            }
        }
    }

    private class JudgementFolder extends VistaBoxFolder<JudgementDTO> {

        public JudgementFolder() {
            super(JudgementDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof JudgementDTO) {
                return new JudgementViewer();
            }
            return super.create(member);
        }

        private class JudgementViewer extends CEntityForm<JudgementDTO> {

            public JudgementViewer() {
                super(JudgementDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, alignToCenter(inject(proto().caseNumber()), 15));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerNumber()), 15));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerName()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().status()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().dateFiled()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().dateSatisfied()), 10));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, new FormDecoratorBuilder(inject(proto().plaintiff()), 50, true).build());
                main.setWidget(3, 0, 2, new FormDecoratorBuilder(inject(proto().defendants()), 50, true).build());

                return main;
            }
        }
    }

    private class ProposalFolder extends VistaBoxFolder<ProposalDTO> {

        public ProposalFolder() {
            super(ProposalDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ProposalDTO) {
                return new ProposalViewer();
            }
            return super.create(member);
        }

        private class ProposalViewer extends CEntityForm<ProposalDTO> {

            public ProposalViewer() {
                super(ProposalDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, alignToCenter(inject(proto().caseNumber()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerNumber()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerName()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().dispositionDate()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().liabilityAmount()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().assetAmount()), 10));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, new FormDecoratorBuilder(inject(proto().caseNumberAndTrustee()), 50, true).build());
                main.setWidget(3, 0, 2, new FormDecoratorBuilder(inject(proto().intentOrDisposition()), 50, true).build());

                return main;
            }
        }
    }

    private class EvictionFolder extends VistaBoxFolder<EvictionDTO> {

        public EvictionFolder() {
            super(EvictionDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof EvictionDTO) {
                return new EvictionViewer();
            }
            return super.create(member);
        }

        private class EvictionViewer extends CEntityForm<EvictionDTO> {

            public EvictionViewer() {
                super(EvictionDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, alignToCenter(inject(proto().caseNumber()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerNumber()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().customerName()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().dateFiled()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().judgementDate()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().judgment()), 10));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, new FormDecoratorBuilder(inject(proto().plaintiff()), 50, true).build());
                main.setWidget(3, 0, 2, new FormDecoratorBuilder(inject(proto().defendants()), 50, true).build());
                main.setWidget(4, 0, 2, inject(proto().address(), new AddressSimpleEditor()));

                return main;
            }
        }
    }

    private class RentFolder extends VistaBoxFolder<RentDTO> {

        public RentFolder() {
            super(RentDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof RentDTO) {
                return new RentViewer();
            }
            return super.create(member);
        }

        private class RentViewer extends CEntityForm<RentDTO> {

            public RentViewer() {
                super(RentDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, alignToCenter(inject(proto().landlord()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().rent()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().writeOffs()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().noticeGiven()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().latePayments()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().NSFChecks()), 10));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, new FormDecoratorBuilder(inject(proto().lastUpdated()), 25, true).build());
                main.setWidget(3, 0, new FormDecoratorBuilder(inject(proto().from()), 10).build());
                main.setWidget(3, 1, new FormDecoratorBuilder(inject(proto().to()), 10).build());
                main.setWidget(4, 0, 2, inject(proto().address(), new AddressSimpleEditor()));

                return main;
            }
        }
    }

    private class CollectionFolder extends VistaBoxFolder<CollectionDTO> {

        public CollectionFolder() {
            super(CollectionDTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CollectionDTO) {
                return new CollectionViewer();
            }
            return super.create(member);
        }

        private class CollectionViewer extends CEntityForm<CollectionDTO> {

            public CollectionViewer() {
                super(CollectionDTO.class);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel table = new BasicFlexFormPanel();

                int col = -1;
                table.setWidget(0, ++col, alignToCenter(inject(proto().onBehalf()), 20));
                table.setWidget(0, ++col, alignToCenter(inject(proto().date()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().lastActive()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().originalAmount()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().balance()), 10));
                table.setWidget(0, ++col, alignToCenter(inject(proto().status()), 15));

                return table;
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
