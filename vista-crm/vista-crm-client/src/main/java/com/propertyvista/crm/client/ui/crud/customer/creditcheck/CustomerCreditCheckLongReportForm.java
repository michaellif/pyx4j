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
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
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
    public CustomerCreditCheckLongReportForm(IFormView<CustomerCreditCheckLongReportDTO> view) {
        super(CustomerCreditCheckLongReportDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("QUICK SUMMARY"));
        formPanel.append(Location.Dual, createQuickSummary());

        formPanel.h1(i18n.tr("IDENTITY"));
        formPanel.append(Location.Dual, createIdentity());

        formPanel.h1(i18n.tr("ACCOUNTS"));
        formPanel.append(Location.Dual, proto().accounts(), new AccountFolder());

        formPanel.h1(i18n.tr("COURT JUDGEMENTS"));
        formPanel.append(Location.Dual, proto().judgements(), new JudgementFolder());

        formPanel.h1(i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        formPanel.append(Location.Dual, proto().proposals(), new ProposalFolder());

        // Not implemented in Equifax:
        if (false) {
            formPanel.h1(i18n.tr("EVICTIONS"));
            formPanel.append(Location.Dual, proto().evictions(), new EvictionFolder());

            formPanel.h1(i18n.tr("RENT HISTORY"));
            formPanel.append(Location.Dual, proto().rents(), new RentFolder());
        }

        formPanel.h1(i18n.tr("COLLECTIONS"));
        formPanel.append(Location.Dual, proto().collections(), new CollectionFolder());

        formPanel.h1(i18n.tr("INQUIRIES"));
        formPanel.append(Location.Dual, proto().inquiries(), new InquiryFolder());

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

        formPanel.append(Location.Left, proto().percentOfRentCovered()).decorate();
        formPanel.append(Location.Left, proto().totalAccounts()).decorate();
        formPanel.append(Location.Left, proto().totalOutstandingBalance()).decorate();
        formPanel.append(Location.Left, proto().outstandingRevolvingDebt()).decorate();
        formPanel.append(Location.Left, proto().outstandingCollectionsBalance()).decorate();

        formPanel.append(Location.Right, proto().accountsWithNoLatePayments()).decorate();
        formPanel.append(Location.Right, proto().numberOfLegalItems()).decorate();
        formPanel.append(Location.Right, proto().numberOfBancruptciesOrActs()).decorate();
        formPanel.append(Location.Right, proto().landlordCollectionsFiled()).decorate();

        // Not implemented in Equifax
        if (false) {
            formPanel.append(Location.Right, proto().numberOfEvictions()).decorate();
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

        formPanel.append(Location.Dual, proto().identity().name(), new NameEditor(i18n.tr("Name")));

        formPanel.append(Location.Left, proto().identity().SIN()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().identity().maritalStatus()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().identity().birthDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().identity().deathDate()).decorate().componentWidth(120);

        formPanel.h2(i18n.tr("Addresses"));

        formPanel.h3(i18n.tr("Current Addresses"));

        formPanel.append(Location.Dual, proto().identity().currentAddress(), new InternationalAddressEditor());

        formPanel.h3(i18n.tr("Former Addresses"));

        formPanel.append(Location.Dual, proto().identity().formerAddress(), new InternationalAddressEditor());

        formPanel.h2(i18n.tr("Employement"));

        formPanel.h3(i18n.tr("Current Employement"));
        formPanel.append(Location.Left, proto().identity().currentEmployer()).decorate();
        formPanel.append(Location.Right, proto().identity().currentOccupation()).decorate();

        formPanel.h3(i18n.tr("Former Employement"));
        formPanel.append(Location.Left, proto().identity().formerEmployer()).decorate();
        formPanel.append(Location.Right, proto().identity().formerOccupation()).decorate();

        return formPanel;
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().caseNumber()).decorate();
                formPanel.append(Location.Left, proto().customerNumber()).decorate();
                formPanel.append(Location.Left, proto().customerName()).decorate();
                formPanel.append(Location.Right, proto().status()).decorate();
                formPanel.append(Location.Right, proto().dateFiled()).decorate();
                formPanel.append(Location.Right, proto().dateSatisfied()).decorate();

                formPanel.hr();

                formPanel.append(Location.Dual, proto().plaintiff()).decorate();
                formPanel.append(Location.Dual, proto().defendants()).decorate();

                return formPanel;
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().caseNumber()).decorate();
                formPanel.append(Location.Left, proto().customerNumber()).decorate();
                formPanel.append(Location.Left, proto().customerName()).decorate();
                formPanel.append(Location.Right, proto().dispositionDate()).decorate();
                formPanel.append(Location.Right, proto().liabilityAmount()).decorate();
                formPanel.append(Location.Right, proto().assetAmount()).decorate();

                formPanel.hr();

                formPanel.append(Location.Dual, proto().caseNumberAndTrustee()).decorate();
                formPanel.append(Location.Dual, proto().intentOrDisposition()).decorate();

                return formPanel;
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().caseNumber()).decorate();
                formPanel.append(Location.Left, proto().customerNumber()).decorate();
                formPanel.append(Location.Left, proto().customerName()).decorate();
                formPanel.append(Location.Right, proto().dateFiled()).decorate();
                formPanel.append(Location.Right, proto().judgementDate()).decorate();
                formPanel.append(Location.Right, proto().judgment()).decorate();

                formPanel.hr();

                formPanel.append(Location.Dual, proto().plaintiff()).decorate();
                formPanel.append(Location.Dual, proto().defendants()).decorate();
                formPanel.append(Location.Dual, proto().address()).decorate();

                return formPanel;
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().landlord()).decorate();
                formPanel.append(Location.Left, proto().rent()).decorate();
                formPanel.append(Location.Left, proto().writeOffs()).decorate();
                formPanel.append(Location.Right, proto().noticeGiven()).decorate();
                formPanel.append(Location.Right, proto().latePayments()).decorate();
                formPanel.append(Location.Right, proto().NSFChecks()).decorate();

                formPanel.hr();

                formPanel.append(Location.Dual, proto().lastUpdated()).decorate();
                formPanel.append(Location.Dual, proto().from()).decorate();
                formPanel.append(Location.Dual, proto().to()).decorate();
                formPanel.append(Location.Dual, proto().address()).decorate();

                return formPanel;
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
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().onBehalf()).decorate();
                formPanel.append(Location.Left, proto().date()).decorate();
                formPanel.append(Location.Left, proto().lastActive()).decorate();
                formPanel.append(Location.Right, proto().originalAmount()).decorate();
                formPanel.append(Location.Right, proto().balance()).decorate();
                formPanel.append(Location.Right, proto().status()).decorate();

                return formPanel;
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
