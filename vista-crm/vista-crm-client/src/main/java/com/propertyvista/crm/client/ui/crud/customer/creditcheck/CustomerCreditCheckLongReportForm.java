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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
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

    @SuppressWarnings("unused")
    public CustomerCreditCheckLongReportForm(IForm<CustomerCreditCheckLongReportDTO> view) {
        super(CustomerCreditCheckLongReportDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("QUICK SUMMARY"));
        formPanel.append(Location.Full, createQuickSummary());

        formPanel.h1(i18n.tr("IDENTITY"));
        formPanel.append(Location.Full, createIdentity());

        formPanel.h1(i18n.tr("ACCOUNTS"));
        formPanel.append(Location.Full, proto().accounts(), new AccountFolder());

        formPanel.h1(i18n.tr("COURT JUDGEMENTS"));
        formPanel.append(Location.Full, proto().judgements(), new JudgementFolder());

        formPanel.h1(i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        formPanel.append(Location.Full, proto().proposals(), new ProposalFolder());

        // Not implemented in Equifax:
        if (false) {
            formPanel.h1(i18n.tr("EVICTIONS"));
            formPanel.append(Location.Full, inject(proto().evictions(), new EvictionFolder()));

            formPanel.h1(i18n.tr("RENT HISTORY"));
            formPanel.append(Location.Full, inject(proto().rents(), new RentFolder()));
        }

        formPanel.h1(i18n.tr("COLLECTIONS"));
        formPanel.append(Location.Full, proto().collections(), new CollectionFolder());

        formPanel.h1(i18n.tr("INQUIRIES"));
        formPanel.append(Location.Full, proto().inquiries(), new InquiryFolder());

        selectTab(addTab(formPanel, i18n.tr("Customer Credit Check")));
        setTabBarVisible(false);

        // Add do not print marker:
        formPanel.asWidget().addStyleName(StyleManager.DO_NOT_PRINT_CLASS_NAME);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().identity().deathDate()).setVisible(!getValue().identity().deathDate().isNull());
    }

    @SuppressWarnings("unused")
    private IsWidget createQuickSummary() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, inject(proto().percentOfRentCovered(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Left, inject(proto().totalAccounts(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Left, inject(proto().totalOutstandingBalance(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Left, inject(proto().outstandingRevolvingDebt(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Left, inject(proto().outstandingCollectionsBalance(), new FieldDecoratorBuilder(20, 10, 20).build()));

        formPanel.append(Location.Right, inject(proto().accountsWithNoLatePayments(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Right, inject(proto().numberOfLegalItems(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Right, inject(proto().numberOfBancruptciesOrActs(), new FieldDecoratorBuilder(20, 10, 20).build()));
        formPanel.append(Location.Right, inject(proto().landlordCollectionsFiled(), new FieldDecoratorBuilder(20, 10, 20).build()));

        // Not implemented in Equifax
        if (false) {
            formPanel.append(Location.Right, inject(proto().numberOfEvictions(), new FieldDecoratorBuilder(20, 10, 20).build()));
        }

        formPanel.h1(i18n.tr("Accounts paid late"));

        formPanel.append(Location.Left, proto().latePayments1_30days()).decorate();
        formPanel.append(Location.Left, proto().latePayments31_60days()).decorate();
        formPanel.append(Location.Left, proto().latePayments61_90days()).decorate();

        formPanel.h1(i18n.tr("Equifax"));

        formPanel.append(Location.Left, proto().equifaxCheckScore()).decorate();
        formPanel.append(Location.Left, proto().equifaxRatingLevel()).decorate();
        formPanel.append(Location.Left, proto().equifaxRiskLevel()).decorate();

        formPanel.h1(i18n.tr("Ratings"));

        formPanel.append(Location.Left, proto().rating1()).decorate();
        formPanel.append(Location.Left, proto().rating2()).decorate();
        formPanel.append(Location.Left, proto().rating3()).decorate();
        formPanel.append(Location.Left, proto().rating4()).decorate();
        formPanel.append(Location.Left, proto().rating5()).decorate();
        formPanel.append(Location.Right, proto().rating6()).decorate();
        formPanel.append(Location.Right, proto().rating7()).decorate();
        formPanel.append(Location.Right, proto().rating8()).decorate();
        formPanel.append(Location.Right, proto().rating9()).decorate();

        return formPanel;
    }

    private IsWidget createIdentity() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Full, proto().identity().name(), new NameEditor(i18n.tr("Name")));

        formPanel.append(Location.Left, proto().identity().SIN()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().identity().maritalStatus()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().identity().birthDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().identity().deathDate()).decorate().componentWidth(120);

        formPanel.h2(i18n.tr("Addresses"));

        formPanel.h3(i18n.tr("Current Addresses"));

        formPanel.append(Location.Full, proto().identity().currentAddress(), new AddressSimpleEditor());

        formPanel.h3(i18n.tr("Former Addresses"));

        formPanel.append(Location.Full, proto().identity().formerAddress(), new AddressSimpleEditor());

        formPanel.h2(i18n.tr("Employement"));

        formPanel.h3(i18n.tr("Current Employement"));
        formPanel.append(Location.Left, proto().identity().currentEmployer()).decorate();
        formPanel.append(Location.Right, proto().identity().currentOccupation()).decorate();

        formPanel.h3(i18n.tr("Former Employement"));
        formPanel.append(Location.Left, proto().identity().formerEmployer()).decorate();
        formPanel.append(Location.Right, proto().identity().formerOccupation()).decorate();

        return formPanel;
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().name()).decorate();
                formPanel.append(Location.Left, proto().number()).decorate().componentWidth(180);
                formPanel.append(Location.Left, proto().creditAmount()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().balanceAmount()).decorate().componentWidth(120);
                formPanel.append(Location.Right, proto().lastPayment()).decorate().componentWidth(120);
                formPanel.append(Location.Right, proto().code()).decorate().componentWidth(90);
                formPanel.append(Location.Right, proto().type()).decorate().componentWidth(120);

                formPanel.hr();

                formPanel.append(Location.Left, proto().paymentRate()).decorate();
                formPanel.append(Location.Right, proto().paymentType()).decorate();

                return formPanel;
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
