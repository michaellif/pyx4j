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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.operations.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.PmcMerchantAccountDTO;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.PmcDTO;

public class PmcForm extends OperationsEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcForm.class);

    private MerchantAccountsLister onboardingMerchantAccountsLister;

    private ListerDataSource<PmcMerchantAccountDTO> onboardingMerchantAccountsSource;

    private Anchor approvalLink;

    public PmcForm(IFormView<PmcDTO> view) {
        super(PmcDTO.class, view);

        selectTab(addTab(createGeneralTab()));
        setTabEnabled(addTab(createOnboardingMerchantAccountsTab()), !isEditable());
        addTab(createEquifaxlTab());
        addTab(createYardiTab());
    }

    public void setOnboardingMerchantAccountsSource(ListerDataSource<PmcMerchantAccountDTO> onboardingMerchantAccountsSource) {
        this.onboardingMerchantAccountsSource = onboardingMerchantAccountsSource;
        this.onboardingMerchantAccountsLister.setDataSource(onboardingMerchantAccountsSource);
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
            onboardingMerchantAccountsLister.setParentPmc(EntityFactory.createIdentityStub(Pmc.class, getValue().getPrimaryKey()));
            onboardingMerchantAccountsLister.obtain(0);
        }

        approvalLink.setVisible(true || !isEditable() & getValue().equifaxInfo().status().getValue() == PmcEquifaxStatus.PendingVistaApproval);
    }

    private FormFlexPanel createGeneralTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("General"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingAccountId()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().namespace()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dnsName()), 15).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().vistaCrmUrl(), new CHyperlink(new Command() {
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

        })), 50).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().residentPortalUrl(), new CHyperlink(new Command() {
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

        })), 50).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().prospectPortalUrl(), new CHyperlink(new Command() {
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

        })), 50).build());

        content.setH1(++row, 0, 2, proto().features().getMeta().getCaption());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().countryOfOperation()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().occupancyModel()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().productCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().leases()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().onlineApplication()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().xmlSiteExport()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().defaultProductCatalog()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().yardiIntegration()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().tenantSureIntegration()), 5).build());

        content.setH1(++row, 0, 2, proto().dnsNameAliases().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        return content;
    }

    private FormFlexPanel createOnboardingMerchantAccountsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Merchant Accounts"));
        panel.setWidget(0, 0, onboardingMerchantAccountsLister = new MerchantAccountsLister());
        return panel;
    }

    private FormFlexPanel createEquifaxlTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Equifax"));

        FormFlexPanel equifaxFeeQuotePanel = new FormFlexPanel();
        int rowFeeQuote = -1;
        equifaxFeeQuotePanel.setH1(++rowFeeQuote, 0, 2, i18n.tr("Equifax Fee Quote"));
        equifaxFeeQuotePanel.setWidget(++rowFeeQuote, 0, inject(proto().equifaxFee(), new EquifaxFeeQuoteForm(false)));

        int row = -1;

        content.setWidget(++row, 0, equifaxFeeQuotePanel);
        equifaxFeeQuotePanel.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setH1(++row, 0, 2, i18n.tr("Equifax"));
        int row2 = row; // save this row for other column
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().status()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().reportType()), 15).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerNumber()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().securityCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerReferenceNumber()), 15).build());

        content.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().equifaxInfo().equifaxSignUpFee())).labelWidth(25).componentWidth(6).build());
        get(proto().equifaxInfo().equifaxSignUpFee()).setViewable(true);
        content.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().equifaxInfo().equifaxPerApplicantCreditCheckFee())).labelWidth(25).componentWidth(6)
                .build());
        get(proto().equifaxInfo().equifaxPerApplicantCreditCheckFee()).setViewable(true);

        approvalLink = new Anchor(i18n.tr("Go to Approval Screen"));
        approvalLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new OperationsSiteMap.Management.EquifaxApproval().formViewerPlace(getValue().getPrimaryKey()));
            }
        });
        content.setWidget(++row, 0, approvalLink);
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        content.setH1(++row, 0, 2, i18n.tr("Equifax Usage Limits"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().limit().dailyReports()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().limit().dailyRequests()), 15).build());

        return content;
    }

    private FormFlexPanel createYardiTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Yardi"));

        int row = -1;

        content.setH1(++row, 0, 2, i18n.tr("Yardi Credentials"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().residentTransactionsServiceURL()), 35).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().sysBatchServiceURL()), 35).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().propertyCode()), 35).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().username()), 15).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().credential()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().serverName()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().database()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().yardiCredential().platform()), 15).build());

        return content;
    }
}