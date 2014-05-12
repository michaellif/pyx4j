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
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSurePageView.TenantSurePagePresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

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
    protected IsWidget createContent() {
        PortalFormPanel formPanel = new PortalFormPanel(this);
        formPanel.append(Location.Left, new TenantSureLogo());
        formPanel.append(Location.Left, makeGreetingPanel());

        formPanel.h3(i18n.tr("Coverage"));
        formPanel.append(Location.Left, proto().certificate().insuranceCertificateNumber()).decorate();
        formPanel.append(Location.Left, proto().certificate().inceptionDate()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().certificate().expiryDate()).decorate().componentWidth(150);

        formPanel.append(Location.Left, proto().certificate().liabilityCoverage()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().contentsCoverage()).decorate().componentWidth(150);

        IFormatter<BigDecimal, String> currencyFormat = new MoneyComboBox.MoneyComboBoxFormat();
        ((CTextFieldBase<BigDecimal, ?>) get(proto().certificate().liabilityCoverage())).setFormatter(currencyFormat);
        ((CTextFieldBase<BigDecimal, ?>) get(proto().contentsCoverage())).setFormatter(currencyFormat);

        formPanel.h3(i18n.tr("Annual Payment"));
        formPanel.append(Location.Left, proto().annualPaymentDetails(), new TenantSurePaymentViewer());

        formPanel.h3(i18n.tr("Next Monthly Payment"));
        formPanel.append(Location.Left, proto().nextPaymentDetails(), new TenantSurePaymentViewer());

        formPanel.append(Location.Left, proto().messages(), new TenantSureMessagesViewer());

        formPanel.br();
        formPanel.append(Location.Left, createTermLink(i18n.tr("Privacy Policy"), TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF));
        formPanel.append(Location.Left,
                createTermLink(i18n.tr("Compensation Disclosure Statement"), TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF));

        return formPanel;
    }

    public void setPresenter(TenantSurePageView.TenantSurePagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateCC.setVisible(!getValue().isCancelled().getValue(false) && !getValue().isPaymentFailed().getValue(false));
        updateCCAndPay.setVisible(getValue().isPaymentFailed().getValue(false));

        cancelTenantSure.setVisible(!getValue().isCancelled().getValue(false));
        reinstateTenantSure.setVisible(getValue().isCancelled().getValue(false));
    }

    @Override
    protected FormDecorator<TenantSureInsurancePolicyDTO> createDecorator() {

        FormDecorator<TenantSureInsurancePolicyDTO> decorator = super.createDecorator();
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
        decorator.addHeaderToolbarWidget(btnActions);

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

        public TenantSureMessagesViewer() {
            setFormatter(new IFormatter<IList<TenantSureMessageDTO>, IsWidget>() {
                @Override
                public IsWidget format(IList<TenantSureMessageDTO> value) {
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
            });
        }

    }

    private Anchor createTermLink(String text, String href) {
        Anchor anchor = new Anchor(text);
        anchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        anchor.setTarget("_blank");
        anchor.setHref(href);
        return anchor;
    }
}