/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.PasswordIdentityFormat;
import com.propertyvista.common.client.ui.components.PasswordIdentityParser;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.pmc.PmcPaymentTypeInfo;
import com.propertyvista.domain.security.PasswordIdentity;
import com.propertyvista.operations.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.operations.client.ui.components.PaymentFeesForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords.CardTransactionRecordLister;
import com.propertyvista.operations.client.ui.crud.fundstransfer.directdebitrecords.DirectDebitRecordLister;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.AuditRecordOperationsDTO;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public class PmcForm extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcForm.class);

    private MerchantAccountsLister onboardingMerchantAccountsLister;

    private CardTransactionRecordLister cardTransactionRecordLister;

    private DirectDebitRecordLister directDebitRecordLister;

    private Anchor approvalLink;

    private Anchor auditRecordsLink;

    public PmcForm(IPrimeFormView<PmcDTO, ?> view) {
        super(PmcDTO.class, view);

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        setTabEnabled(addTab(createOnboardingMerchantAccountsTab(), i18n.tr("Merchant Accounts")), !isEditable());
        addTab(createEquifaxlTab(), i18n.tr("Equifax"));
        addTab(createYardiTab(), i18n.tr("Yardi"));
        addTab(createFundsTransferTab(), i18n.tr("Funds Transfer"));
        setTabEnabled(addTab(createCardTransactionTab(), i18n.tr("Card Transaction Records")), !isEditable());
        setTabEnabled(addTab(createDirectDebitTab(), i18n.tr("Direct Debit Records")), !isEditable());
    }

    public void setCardTransactionRecordsSource(ListerDataSource<CardTransactionRecord> cardTransactionRecordsDataSource) {
        this.cardTransactionRecordLister.setDataSource(cardTransactionRecordsDataSource);
    }

    public void setDirectDebitRecordsSource(ListerDataSource<DirectDebitRecord> directDebitRecordsDataSource) {
        this.directDebitRecordLister.setDataSource(directDebitRecordsDataSource);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        boolean isVisible = get(proto().status()).getValue() != PmcStatus.Created;
        get(proto().vistaCrmUrl()).setVisible(isVisible);
        get(proto().residentPortalUrl()).setVisible(isVisible);
        get(proto().prospectPortalUrl()).setVisible(isVisible);

        if (!isEditable() & getValue().getPrimaryKey() != null) {
            Pmc listerPmcContext = EntityFactory.createIdentityStub(Pmc.class, getValue().getPrimaryKey());

            onboardingMerchantAccountsLister.setParentPmc(listerPmcContext);
            onboardingMerchantAccountsLister.populate(0);

            cardTransactionRecordLister.setParentPmc(listerPmcContext);
            cardTransactionRecordLister.populate(0);

            directDebitRecordLister.setParentPmc(listerPmcContext);
            directDebitRecordLister.populate(0);
        }

        approvalLink.setVisible(true || !isEditable() & getValue().equifaxInfo().status().getValue() == PmcEquifaxStatus.PendingVistaApproval);

        get(proto().features().yardiMaintenance()).setEnabled(getValue() != null && getValue().features().yardiIntegration().getValue(false));
    }

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("General"));

        formPanel.append(Location.Left, proto().updated()).decorate();
        formPanel.append(Location.Right, proto().created()).decorate();

        formPanel.append(Location.Left, proto().status(), new CLabel<String>()).decorate();
        formPanel.append(Location.Right, proto().namespace()).decorate();

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Right, proto().dnsName()).decorate();

        formPanel.append(Location.Dual, proto().vistaCrmUrl(), new CLabel<String>()).decorate();
        ((CField<?, ?>) get(proto().vistaCrmUrl())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().vistaCrmUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, getValue().dnsName().getStringView() + "_Crm", null);
            }

        });

        formPanel.append(Location.Dual, proto().residentPortalUrl(), new CLabel<String>()).decorate();
        ((CField<?, ?>) get(proto().residentPortalUrl())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().residentPortalUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, getValue().dnsName().getStringView() + "_Portal", null);
            }

        });

        formPanel.append(Location.Dual, proto().prospectPortalUrl(), new CLabel<String>()).decorate();
        ((CField<?, ?>) get(proto().prospectPortalUrl())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().prospectPortalUrl().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, getValue().dnsName().getStringView() + "_Ptapp", null);
            }

        });

        auditRecordsLink = new Anchor(i18n.tr("Audit Records for this PMC"));

        auditRecordsLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AuditRecordOperationsDTO.class);

                place.formListerPlace().queryArg(EntityFactory.getEntityPrototype(AuditRecordOperationsDTO.class).pmc().getPath().toString(),
                        getValue().id().getValue().toString());

                AppSite.getPlaceController().goTo(place);
            }
        });

        formPanel.br();
        formPanel.append(Location.Dual, auditRecordsLink);
        formPanel.br();

        formPanel.h1(proto().features().getMeta().getCaption());
        formPanel.append(Location.Left, proto().features().countryOfOperation()).decorate();

        formPanel.append(Location.Left, proto().features().onlineApplication()).decorate();
        formPanel.append(Location.Left, proto().features().whiteLabelPortal()).decorate();

        formPanel.append(Location.Left, proto().features().yardiIntegration()).decorate();
        formPanel.append(Location.Right, proto().features().yardiMaintenance()).decorate();

        formPanel.append(Location.Left, proto().features().tenantSureIntegration()).decorate();
        formPanel.append(Location.Right, proto().features().tenantEmailEnabled()).decorate();

        formPanel.h1(proto().dnsNameAliases().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable()));

        final CComponent<?, Boolean, ?, ?> yardiIntegrationSwitch = get(proto().features().yardiIntegration());
        final CComponent<?, Boolean, ?, ?> yardiMaintenanceSwitch = get(proto().features().yardiMaintenance());
        yardiIntegrationSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                yardiMaintenanceSwitch.setEnabled(Boolean.TRUE.equals(event.getValue()));
            }
        });

        formPanel.append(Location.Left, proto().schemaVersion()).decorate();
        formPanel.append(Location.Right, proto().schemaDataUpgradeSteps()).decorate();

        return formPanel;
    }

    private IsWidget createOnboardingMerchantAccountsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, onboardingMerchantAccountsLister = new MerchantAccountsLister());
        return formPanel;
    }

    private IsWidget createEquifaxlTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Equifax Fee Quote"));
        formPanel.append(Location.Dual, proto().equifaxFee(), new EquifaxFeeQuoteForm(false, false));

        formPanel.h1(i18n.tr("Equifax"));
        formPanel.append(Location.Left, proto().equifaxInfo().status()).decorate().componentWidth(200);

        formPanel.append(Location.Right, proto().equifaxInfo().equifaxSignUpFee(), new CLabel<>()).decorate();

        formPanel.append(Location.Left, proto().equifaxInfo().reportType()).decorate().componentWidth(200);

        formPanel.append(Location.Right, proto().equifaxInfo().equifaxPerApplicantCreditCheckFee(), new CLabel<>()).decorate();

        CPersonalIdentityField<PasswordIdentity> memberNumber = new CPersonalIdentityField<PasswordIdentity>(PasswordIdentity.class);
        memberNumber.setFormatter(new PasswordIdentityFormat());
        memberNumber.setParser(new PasswordIdentityParser(memberNumber));
        formPanel.append(Location.Left, proto().equifaxInfo().memberNumber(), memberNumber).decorate().componentWidth(200);

        CPersonalIdentityField<PasswordIdentity> securityCode = new CPersonalIdentityField<PasswordIdentity>(PasswordIdentity.class);
        securityCode.setFormatter(new PasswordIdentityFormat());
        securityCode.setParser(new PasswordIdentityParser(securityCode));
        formPanel.append(Location.Left, proto().equifaxInfo().securityCode(), securityCode).decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().equifaxInfo().customerCode()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().equifaxInfo().customerReferenceNumber()).decorate().componentWidth(200);

        approvalLink = new Anchor(i18n.tr("Go to Approval Screen"));
        approvalLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.EquifaxApproval().formViewerPlace(getValue().getPrimaryKey()));
            }
        });
        formPanel.append(Location.Dual, approvalLink);

        formPanel.h1(i18n.tr("Equifax Usage Limits"));
        formPanel.append(Location.Left, proto().equifaxInfo().limit().dailyReports()).decorate();
        formPanel.append(Location.Left, proto().equifaxInfo().limit().dailyRequests()).decorate();

        return formPanel;
    }

    private IsWidget createYardiTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().yardiCredentials(), new YardiCredentialFolder());
        return formPanel;
    }

    private IsWidget createFundsTransferTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("Default"));
        formPanel.append(Location.Dual, proto().defaultPaymentFees(), new PaymentFeesForm<DefaultPaymentFees>(DefaultPaymentFees.class));
        get(proto().defaultPaymentFees()).setViewable(true);

        formPanel.h2(i18n.tr("Override"));
        formPanel.append(Location.Dual, proto().paymentTypeInfo(), new PaymentFeesForm<PmcPaymentTypeInfo>(PmcPaymentTypeInfo.class));

        return formPanel;
    }

    private IsWidget createDirectDebitTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, directDebitRecordLister = new DirectDebitRecordLister(false));
        return formPanel;
    }

    private IsWidget createCardTransactionTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, cardTransactionRecordLister = new CardTransactionRecordLister(false));
        return formPanel;
    }
}