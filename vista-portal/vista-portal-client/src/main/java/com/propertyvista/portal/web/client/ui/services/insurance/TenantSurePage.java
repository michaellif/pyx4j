/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.web.client.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.web.client.themes.TenantSureTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSurePageView.TenantSurePagePresenter;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TenantSurePage extends CPortalEntityForm<TenantSureInsurancePolicyDTO> {

    private final static I18n i18n = I18n.get(TenantSurePage.class);

    private TenantSurePagePresenter presenter;

    private MenuItem updateCC;

    private MenuItem updateCCAndPay;

    private MenuItem cancelTenantSure;

    private MenuItem reinstateTenantSure;

    public TenantSurePage(TenantSurePageView view) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenantSure Insurance"), ThemeColor.contrast3);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;
        mainPanel.setWidget(++row, 0, 2, new TenantSureLogo());
        mainPanel.setWidget(++row, 0, 2, makeGreetingPanel());

        mainPanel.setH3(++row, 0, 1, i18n.tr("Coverage"));
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().insuranceCertificateNumber()), 300).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().inceptionDate()), 150).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().expiryDate()), 150).build());

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().certificate().liabilityCoverage()), 150).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().contentsCoverage()), 150).build());

        IFormat<BigDecimal> currencyFormat = new MoneyComboBox.MoneyComboBoxFormat();
        ((CTextFieldBase<BigDecimal, ?>) get(proto().certificate().liabilityCoverage())).setFormat(currencyFormat);
        ((CTextFieldBase<BigDecimal, ?>) get(proto().contentsCoverage())).setFormat(currencyFormat);

        mainPanel.setH3(++row, 0, 1, i18n.tr("Annual Payment"));
        mainPanel.setWidget(++row, 0, inject(proto().annualPaymentDetails(), new TenantSurePaymentViewer()));

        mainPanel.setH3(++row, 0, 1, i18n.tr("Next Monthly Payment"));
        mainPanel.setWidget(++row, 0, inject(proto().nextPaymentDetails(), new TenantSurePaymentViewer()));

        mainPanel.setWidget(++row, 0, inject(proto().messages(), new TenantSureMessagesViewer()));

        TenantSure2HighCourtReferenceLinks highCourtLinks = new TenantSure2HighCourtReferenceLinks();
        highCourtLinks.setCompensationDisclosureStatementHref(TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF);
        highCourtLinks.setPrivacyPolcyHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        mainPanel.setWidget(++row, 0, highCourtLinks);
        return mainPanel;
    }

    public void setPresenter(TenantSurePageView.TenantSurePagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateCC.setVisible(!getValue().isCancelled().isBooleanTrue() && !getValue().isPaymentFailed().isBooleanTrue());
        updateCCAndPay.setVisible(getValue().isPaymentFailed().isBooleanTrue());

        cancelTenantSure.setVisible(!getValue().isCancelled().isBooleanTrue());
        reinstateTenantSure.setVisible(getValue().isCancelled().isBooleanTrue());
    }

    @Override
    protected FormDecorator<TenantSureInsurancePolicyDTO, CEntityForm<TenantSureInsurancePolicyDTO>> createDecorator() {

        FormDecorator<TenantSureInsurancePolicyDTO, CEntityForm<TenantSureInsurancePolicyDTO>> decorator = super.createDecorator();
        decorator.setCaption(i18n.tr("TenantSure"));

        Button btnActions = new Button(i18n.tr("Actions"));
        ButtonMenuBar actions = new ButtonMenuBar();

        MenuItem sendDocumentationItem = new MenuItem(i18n.tr("Send Insurance Certificate"), new Command() {
            @Override
            public void execute() {
                presenter.sendCertificate(null);
            }
        });

        updateCC = new MenuItem(i18n.tr("Update Credit Card Details"), new Command() {
            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });

        updateCCAndPay = new MenuItem(i18n.tr("Update Credit Card and Pay"), new Command() {
            @Override
            public void execute() {
                presenter.updateCreditCardDetails();
            }
        });

        cancelTenantSure = new MenuItem(i18n.tr("Cancel TenantSure"), new Command() {
            @Override
            public void execute() {
                onCancelTenantSure();
            }
        });

        reinstateTenantSure = new MenuItem(i18n.tr("Reinstate"), new Command() {
            @Override
            public void execute() {
                onReinstateTenantSure();
            }
        });

        MenuItem viewFaq = new MenuItem(i18n.tr("FAQ"), new Command() {
            @Override
            public void execute() {
                presenter.viewFaq();
            }
        });

        MenuItem aboutTenantSure = new MenuItem(i18n.tr("About TenantSure / Contact Us"), new Command() {
            @Override
            public void execute() {
                presenter.viewAboutTenantSure();
            }
        });

        MenuItem makeAClaim = new MenuItem(i18n.tr("Make a Claim"), new Command() {
            @Override
            public void execute() {
                presenter.makeAClaim();
            }
        });

        actions.addItem(sendDocumentationItem);
        actions.addItem(makeAClaim);
        actions.addItem(updateCC);
        actions.addItem(updateCCAndPay);
        actions.addSeparator();

        actions.addItem(cancelTenantSure);
        actions.addItem(reinstateTenantSure);
        actions.addSeparator();

        actions.addItem(viewFaq);
        actions.addItem(aboutTenantSure);

        btnActions.setMenu(actions);
        decorator.addHeaderToolbarButton(btnActions);

        return decorator;
    }

    private Widget makeGreetingPanel() {
        FlowPanel tenantSureGreetingPanel = new FlowPanel();
        tenantSureGreetingPanel.setStyleName(TenantSureTheme.StyleName.TenantSureManagementGreetingPanel.name());

        HTML greeting = new HTML(TenantSureResources.INSTANCE.managementPanelGreeting().getText());
        greeting.setStyleName(TenantSureTheme.StyleName.TenantSureManagementGreeting.name());

        tenantSureGreetingPanel.add(greeting);
        return tenantSureGreetingPanel;
    }

    private void onCancelTenantSure() {
        MessageDialog.confirm(i18n.tr("TenantSure cancellation"), i18n.tr("Are you sure you want to cancel TenantSure?"), new Command() {
            @Override
            public void execute() {
                presenter.cancelTenantSure();
            }
        });
    }

    private void onReinstateTenantSure() {
        MessageDialog.confirm(i18n.tr("TenantSure Reinstatement"), i18n.tr("Are you sure you want to reinstate TenantSure?"), new Command() {
            @Override
            public void execute() {
                presenter.reinstate();
            }
        });
    }

    public static class TenantSureMessagesViewer extends CViewer<IList<TenantSureMessageDTO>> {

        @Override
        public IsWidget createContent(IList<TenantSureMessageDTO> value) {
            FlowPanel panel = new FlowPanel();
            panel.setStyleName(TenantSureTheme.StyleName.TenantSureMessages.name());
            if (value != null && !value.isEmpty()) {
                for (TenantSureMessageDTO message : value) {
                    Label messageLabel = new Label(message.messageText().getValue());
                    messageLabel.setStyleName(TenantSureTheme.StyleName.TenantSureMessage.name());
                    panel.add(messageLabel);
                }
            }

            return panel;
        }

    }

}