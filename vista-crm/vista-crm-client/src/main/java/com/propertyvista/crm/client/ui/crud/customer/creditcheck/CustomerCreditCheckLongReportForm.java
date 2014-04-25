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
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

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

        selectTab(addTab(main, i18n.tr("Customer Credit Check")));
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
        summary.setWidget(++row, 0, inject(proto().percentOfRentCovered(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 0, inject(proto().totalAccounts(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 0, inject(proto().totalOutstandingBalance(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 0, inject(proto().outstandingRevolvingDebt(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 0, inject(proto().outstandingCollectionsBalance(), new FieldDecoratorBuilder(20, 10, 20).build()));

        row = 0;
        summary.setWidget(++row, 1, inject(proto().accountsWithNoLatePayments(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 1, inject(proto().numberOfLegalItems(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 1, inject(proto().numberOfBancruptciesOrActs(), new FieldDecoratorBuilder(20, 10, 20).build()));
        summary.setWidget(++row, 1, inject(proto().landlordCollectionsFiled(), new FieldDecoratorBuilder(20, 10, 20).build()));
// Not implemented in Equifax:
//      summary.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().numberOfEvictions()), 20, 10, 20).build());

        BasicFlexFormPanel accounts = new BasicFlexFormPanel();
        int col = -1;
        accounts.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Accounts paid late:") + "</i>"));
        accounts.getWidget(0, col).setWidth("15em");
        accounts.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        accounts.setWidget(0, ++col, inject(proto().latePayments1_30days(), decorator(20)));
        accounts.setWidget(0, ++col, inject(proto().latePayments31_60days(), decorator(20)));
        accounts.setWidget(0, ++col, inject(proto().latePayments61_90days(), decorator(20)));

        BasicFlexFormPanel equiifax = new BasicFlexFormPanel();
        col = -1;
        equiifax.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Equifax:") + "</i>"));
        equiifax.getWidget(0, col).setWidth("15em");
        equiifax.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        equiifax.setWidget(0, ++col, inject(proto().equifaxCheckScore(), decorator(20)));
        equiifax.setWidget(0, ++col, inject(proto().equifaxRatingLevel(), decorator(20)));
        equiifax.setWidget(0, ++col, inject(proto().equifaxRiskLevel(), decorator(20)));

        BasicFlexFormPanel raitings = new BasicFlexFormPanel();
        col = -1;
        raitings.setWidget(0, ++col, inject(proto().rating1(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating2(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating3(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating4(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating5(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating6(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating7(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating8(), decorator(8)));
        raitings.setWidget(0, ++col, inject(proto().rating9(), decorator(8)));

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

        name.setWidget(1, 0, inject(proto().identity().SIN(), new FieldDecoratorBuilder(20).build()));
        name.setWidget(2, 0, inject(proto().identity().maritalStatus(), new FieldDecoratorBuilder(10).build()));

        name.setWidget(1, 1, inject(proto().identity().birthDate(), new FieldDecoratorBuilder(10).build()));
        name.setWidget(2, 1, inject(proto().identity().deathDate(), new FieldDecoratorBuilder(10).build()));

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
        employement.setWidget(1, 0, inject(proto().identity().currentEmployer(), new FieldDecoratorBuilder(20).build()));
        employement.setWidget(4, 0, inject(proto().identity().currentOccupation(), new FieldDecoratorBuilder(20).build()));

        employement.setWidget(0, 1, new HTML("<i>" + i18n.tr("Former:") + "</i>"));
        employement.getWidget(0, 1).getElement().getStyle().setPaddingRight(4, Unit.EM);
        employement.setWidget(1, 1, inject(proto().identity().formerEmployer(), new FieldDecoratorBuilder(20).build()));
        employement.setWidget(2, 1, inject(proto().identity().formerOccupation(), new FieldDecoratorBuilder(20).build()));

        // put all together:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        main.setWidget(0, 0, name);

        main.setH3(1, 0, 0, i18n.tr("Addresses"));
        main.setWidget(2, 0, address);

        main.setH3(3, 0, 0, i18n.tr("Employement"));
        main.setWidget(4, 0, employement);

        return main;
    }

    private FieldDecorator decorator(double width) {
        return new FieldDecoratorBuilder(width, width, width).labelPosition(LabelPosition.top).labelAlignment(Alignment.center).useLabelSemicolon(false)
                .componentAlignment(Alignment.center).build();
    }

    private class AccountFolder extends VistaBoxFolder<AccountDTO> {

        public AccountFolder() {
            super(AccountDTO.class, false);
        }

        @Override
        protected CForm<AccountDTO> createItemForm(IObject<?> member) {
            return new AccountViewer();
        }

        private class AccountViewer extends CForm<AccountDTO> {

            public AccountViewer() {
                super(AccountDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, inject(proto().name(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().number(), decorator(15)));
                table.setWidget(0, ++col, inject(proto().creditAmount(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().balanceAmount(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().lastPayment(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().code(), decorator(5)));
                table.setWidget(0, ++col, inject(proto().type(), decorator(10)));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, inject(proto().paymentRate(), new FieldDecoratorBuilder(25).build()));
                main.setWidget(2, 1, inject(proto().paymentType(), new FieldDecoratorBuilder(25).build()));

                return main;
            }
        }
    }

    private class JudgementFolder extends VistaBoxFolder<JudgementDTO> {

        public JudgementFolder() {
            super(JudgementDTO.class, false);
        }

        @Override
        protected CForm<JudgementDTO> createItemForm(IObject<?> member) {
            return new JudgementViewer();
        }

        private class JudgementViewer extends CForm<JudgementDTO> {

            public JudgementViewer() {
                super(JudgementDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, inject(proto().caseNumber(), decorator(15)));
                table.setWidget(0, ++col, inject(proto().customerNumber(), decorator(15)));
                table.setWidget(0, ++col, inject(proto().customerName(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().status(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().dateFiled(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().dateSatisfied(), decorator(10)));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, inject(proto().plaintiff(), new FieldDecoratorBuilder(50, true).build()));
                main.setWidget(3, 0, 2, inject(proto().defendants(), new FieldDecoratorBuilder(50, true).build()));

                return main;
            }
        }
    }

    private class ProposalFolder extends VistaBoxFolder<ProposalDTO> {

        public ProposalFolder() {
            super(ProposalDTO.class, false);
        }

        @Override
        protected CForm<ProposalDTO> createItemForm(IObject<?> member) {
            return new ProposalViewer();
        }

        private class ProposalViewer extends CForm<ProposalDTO> {

            public ProposalViewer() {
                super(ProposalDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, inject(proto().caseNumber(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().customerNumber(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().customerName(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().dispositionDate(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().liabilityAmount(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().assetAmount(), decorator(10)));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, inject(proto().caseNumberAndTrustee(), new FieldDecoratorBuilder(50, true).build()));
                main.setWidget(3, 0, 2, inject(proto().intentOrDisposition(), new FieldDecoratorBuilder(50, true).build()));

                return main;
            }
        }
    }

    private class EvictionFolder extends VistaBoxFolder<EvictionDTO> {

        public EvictionFolder() {
            super(EvictionDTO.class, false);
        }

        @Override
        protected CForm<EvictionDTO> createItemForm(IObject<?> member) {
            return new EvictionViewer();
        }

        private class EvictionViewer extends CForm<EvictionDTO> {

            public EvictionViewer() {
                super(EvictionDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, inject(proto().caseNumber(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().customerNumber(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().customerName(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().dateFiled(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().judgementDate(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().judgment(), decorator(10)));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, inject(proto().plaintiff(), new FieldDecoratorBuilder(50, true).build()));
                main.setWidget(3, 0, 2, inject(proto().defendants(), new FieldDecoratorBuilder(50, true).build()));
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
        protected CForm<RentDTO> createItemForm(IObject<?> member) {
            return new RentViewer();
        }

        private class RentViewer extends CForm<RentDTO> {

            public RentViewer() {
                super(RentDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int col = -1;
                BasicFlexFormPanel table = new BasicFlexFormPanel();
                table.setWidget(0, ++col, inject(proto().landlord(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().rent(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().writeOffs(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().noticeGiven(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().latePayments(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().NSFChecks(), decorator(10)));

                main.setWidget(0, 0, 2, table);

                main.setHR(1, 0, 2);

                main.setWidget(2, 0, 2, inject(proto().lastUpdated(), new FieldDecoratorBuilder(25, true).build()));
                main.setWidget(3, 0, inject(proto().from(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(3, 1, inject(proto().to(), new FieldDecoratorBuilder(10).build()));
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
        protected CForm<CollectionDTO> createItemForm(IObject<?> member) {
            return new CollectionViewer();
        }

        private class CollectionViewer extends CForm<CollectionDTO> {

            public CollectionViewer() {
                super(CollectionDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel table = new BasicFlexFormPanel();

                int col = -1;
                table.setWidget(0, ++col, inject(proto().onBehalf(), decorator(20)));
                table.setWidget(0, ++col, inject(proto().date(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().lastActive(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().originalAmount(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().balance(), decorator(10)));
                table.setWidget(0, ++col, inject(proto().status(), decorator(15)));

                return table;
            }
        }
    }

    private class InquiryFolder extends VistaTableFolder<InquiryDTO> {

        public InquiryFolder() {
            super(InquiryDTO.class, false);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new FolderColumnDescriptor(proto().onBehalf(), "25em"),
                    new FolderColumnDescriptor(proto().date(), "10em"),
                    new FolderColumnDescriptor(proto().customerNumber(), "15em"),
                    new FolderColumnDescriptor(proto().phone(), "10em"));
              //@formatter:on        
        }
    }
}
