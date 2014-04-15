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
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.PasswordIdentityFormat;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.pmc.PmcPaymentTypeInfo;
import com.propertyvista.domain.security.PasswordIdentity;
import com.propertyvista.operations.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.operations.client.ui.components.PaymentFeesForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.dto.PmcMerchantAccountDTO;

public class PmcForm extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcForm.class);

    private MerchantAccountsLister onboardingMerchantAccountsLister;

    private DirectDebitRecordLister directDebitRecordLister;

    private Anchor approvalLink;

    public PmcForm(IForm<PmcDTO> view) {
        super(PmcDTO.class, view);

        selectTab(addTab(createGeneralTab()));
        setTabEnabled(addTab(createOnboardingMerchantAccountsTab()), !isEditable());
        addTab(createEquifaxlTab());
        addTab(createYardiTab());
        addTab(createFundsTransferTab());
        setTabEnabled(addTab(createDirectDebitTab()), !isEditable());
    }

    public void setOnboardingMerchantAccountsSource(ListerDataSource<PmcMerchantAccountDTO> onboardingMerchantAccountsSource) {
        this.onboardingMerchantAccountsLister.setDataSource(onboardingMerchantAccountsSource);
    }

    public void setDirectDebitRecordsSource(ListerDataSource<DirectDebitRecord> directDebitRecordsDataSource) {
        this.directDebitRecordLister.setDataSource(directDebitRecordsDataSource);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().status()).setViewable(true);

        boolean isVisible = get(proto().status()).getValue() != PmcStatus.Created;
        get(proto().vistaCrmUrl()).setVisible(isVisible);
        get(proto().residentPortalUrl()).setVisible(isVisible);
        get(proto().prospectPortalUrl()).setVisible(isVisible);

        if (!isEditable() & getValue().getPrimaryKey() != null) {
            Pmc listerPmcContext = EntityFactory.createIdentityStub(Pmc.class, getValue().getPrimaryKey());
            onboardingMerchantAccountsLister.setParentPmc(listerPmcContext);
            onboardingMerchantAccountsLister.obtain(0);

            directDebitRecordLister.setParentPmc(listerPmcContext);
            directDebitRecordLister.obtain(0);
        }

        approvalLink.setVisible(true || !isEditable() & getValue().equifaxInfo().status().getValue() == PmcEquifaxStatus.PendingVistaApproval);

        get(proto().features().yardiMaintenance()).setEnabled(getValue() != null && getValue().features().yardiIntegration().getValue(false));
    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("General"));

        content.setWidget(++row, 0, inject(proto().updated(), new FormDecoratorBuilder(10).build()));
        content.setWidget(row, 1, inject(proto().created(), new FormDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().status(), new FormDecoratorBuilder(15).build()));
        content.setWidget(row, 1, inject(proto().namespace(), new FormDecoratorBuilder(15).build()));

        content.setWidget(++row, 0, inject(proto().name(), new FormDecoratorBuilder(15).build()));
        content.setWidget(row, 1, inject(proto().dnsName(), new FormDecoratorBuilder(15).build()));

        content.setWidget(++row, 0, 2, inject(proto().vistaCrmUrl(), new CLabel<String>(), new FormDecoratorBuilder(true).build()));
        ((CField) get(proto().vistaCrmUrl())).setNavigationCommand(new Command() {
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

        content.setWidget(++row, 0, 2, inject(proto().residentPortalUrl(), new CLabel<String>(), new FormDecoratorBuilder(true).build()));
        ((CField) get(proto().residentPortalUrl())).setNavigationCommand(new Command() {
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

        content.setWidget(++row, 0, 2, inject(proto().prospectPortalUrl(), new CLabel<String>(), new FormDecoratorBuilder(true).build()));
        ((CField) get(proto().prospectPortalUrl())).setNavigationCommand(new Command() {
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

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().features().countryOfOperation(), new FormDecoratorBuilder(25).build()));

        content.setWidget(++row, 0, inject(proto().features().onlineApplication(), new FormDecoratorBuilder(5).build()));

        content.setWidget(++row, 0, inject(proto().features().yardiIntegration(), new FormDecoratorBuilder(5).build()));
        content.setWidget(row, 1, inject(proto().features().yardiMaintenance(), new FormDecoratorBuilder(5).build()));

        content.setWidget(++row, 0, inject(proto().features().tenantSureIntegration(), new FormDecoratorBuilder(5).build()));

        content.setH1(++row, 0, 2, proto().dnsNameAliases().getMeta().getCaption());
        content.setWidget(++row, 0, 2, inject(proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        final CComponent<Boolean> yardiIntegrationSwitch = get(proto().features().yardiIntegration());
        final CComponent<Boolean> yardiMaintenanceSwitch = get(proto().features().yardiMaintenance());
        yardiIntegrationSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                yardiMaintenanceSwitch.setEnabled(Boolean.TRUE.equals(event.getValue()));
            }
        });
        return content;
    }

    private TwoColumnFlexFormPanel createOnboardingMerchantAccountsTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Merchant Accounts"));
        panel.setWidget(0, 0, 2, onboardingMerchantAccountsLister = new MerchantAccountsLister());
        return panel;
    }

    private TwoColumnFlexFormPanel createEquifaxlTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Equifax"));

        int row = -1;

        content.setH1(++row, 0, 2, i18n.tr("Equifax Fee Quote"));
        content.setWidget(++row, 0, 2, inject(proto().equifaxFee(), new EquifaxFeeQuoteForm(false)));

        content.setH1(++row, 0, 2, i18n.tr("Equifax"));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().status(), new FormDecoratorBuilder().build()));

        content.setWidget(row, 1, inject(proto().equifaxInfo().equifaxSignUpFee(), new FormDecoratorBuilder().build()));
        get(proto().equifaxInfo().equifaxSignUpFee()).setViewable(true);

        content.setWidget(++row, 0, inject(proto().equifaxInfo().reportType(), new FormDecoratorBuilder().build()));

        content.setWidget(row, 1, inject(proto().equifaxInfo().equifaxPerApplicantCreditCheckFee(), new FormDecoratorBuilder().build()));
        get(proto().equifaxInfo().equifaxPerApplicantCreditCheckFee()).setViewable(true);

        CPersonalIdentityField<PasswordIdentity> memberNumber = new CPersonalIdentityField<PasswordIdentity>(PasswordIdentity.class);
        memberNumber.setFormat(new PasswordIdentityFormat(memberNumber));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().memberNumber(), memberNumber, new FormDecoratorBuilder().build()));

        CPersonalIdentityField<PasswordIdentity> securityCode = new CPersonalIdentityField<PasswordIdentity>(PasswordIdentity.class);
        securityCode.setFormat(new PasswordIdentityFormat(securityCode));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().securityCode(), securityCode, new FormDecoratorBuilder().build()));

        content.setWidget(++row, 0, inject(proto().equifaxInfo().customerCode(), new FormDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().customerReferenceNumber(), new FormDecoratorBuilder().build()));

        approvalLink = new Anchor(i18n.tr("Go to Approval Screen"));
        approvalLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.EquifaxApproval().formViewerPlace(getValue().getPrimaryKey()));
            }
        });
        content.setWidget(++row, 0, 2, approvalLink);
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        content.setH1(++row, 0, 2, i18n.tr("Equifax Usage Limits"));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().limit().dailyReports(), new FormDecoratorBuilder().build()));
        content.setWidget(++row, 0, inject(proto().equifaxInfo().limit().dailyRequests(), new FormDecoratorBuilder().build()));

        return content;
    }

    private TwoColumnFlexFormPanel createYardiTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Yardi"));
        int row = -1;
        content.setWidget(++row, 0, 2, inject(proto().yardiCredentials(), new YardiCredentialFolder()));
        return content;
    }

    private TwoColumnFlexFormPanel createFundsTransferTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Funds Transfer"));
        int row = 0;
        content.setH2(row, 0, 1, i18n.tr("Default"));
        content.setH2(row, 1, 1, i18n.tr("Override"));
        ++row;
        content.setWidget(row, 0, 1, inject(proto().defaultPaymentFees(), new PaymentFeesForm<DefaultPaymentFees>(DefaultPaymentFees.class)));
        get(proto().defaultPaymentFees()).setViewable(true);
        content.setWidget(row, 1, 1, inject(proto().paymentTypeInfo(), new PaymentFeesForm<PmcPaymentTypeInfo>(PmcPaymentTypeInfo.class)));

        return content;
    }

    private TwoColumnFlexFormPanel createDirectDebitTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Direct Debit Records"));
        int row = 0;
        content.setWidget(++row, 0, 2, directDebitRecordLister = new DirectDebitRecordLister(false));

        return content;
    }
}