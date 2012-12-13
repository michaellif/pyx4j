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
package com.propertyvista.admin.client.ui.crud.pmc;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.admin.rpc.PmcDTO;

public class PmcForm extends AdminEntityForm<PmcDTO> {

    private static final I18n i18n = I18n.get(PmcForm.class);

    private OnboardingMerchantAccountsLister onboardingMerchantAccountsLister;

    private ListerDataSource<OnboardingMerchantAccountDTO> onboardingMerchantAccountsSource;

    public PmcForm() {
        this(false);
    }

    public PmcForm(boolean viewMode) {
        super(PmcDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        selectTab(addTab(createGeneralTab()));
        Tab tab = addTab(createOnboardingMerchantAccountsTab());
        setTabEnabled(tab, !isEditable());

        selectTab(addTab(createEquifaxlTab()));
    }

    public void setOnboardingMerchantAccountsSource(ListerDataSource<OnboardingMerchantAccountDTO> onboardingMerchantAccountsSource) {
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
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().features().genericProductCatalog()), 5).build());

        content.setH1(++row, 0, 2, proto().dnsNameAliases().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().dnsNameAliases(), new PmcDnsNameFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        return content;
    }

    private FormFlexPanel createOnboardingMerchantAccountsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Merchant Accounts"));
        panel.setWidget(0, 0, onboardingMerchantAccountsLister = new OnboardingMerchantAccountsLister());
        return panel;
    }

    private FormFlexPanel createEquifaxlTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Equifax"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Equifax"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().approved()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().reportType()), 15).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerNumber()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().securityCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxInfo().customerReferenceNumber()), 15).build());

        return content;
    }
}