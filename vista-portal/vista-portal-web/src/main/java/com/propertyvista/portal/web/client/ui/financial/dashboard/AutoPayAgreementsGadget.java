/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class AutoPayAgreementsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(AutoPayAgreementsGadget.class);

    private final AutoPayListView autoPayListView;

    AutoPayAgreementsGadget(FinancialDashboardViewImpl dashboardView) {
        super(dashboardView, PortalImages.INSTANCE.billingIcon(), i18n.tr("Auto Pay Agreements"), ThemeColor.contrast4);
        setActionsToolbar(new AutoPayAgreementsToolbar());

        autoPayListView = new AutoPayListView();
        autoPayListView.setViewable(true);
        autoPayListView.initContent();

        SimplePanel contentPanel = new SimplePanel(autoPayListView.asWidget());
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        setContent(contentPanel);
    }

    protected void populate(AutoPaySummaryDTO value) {
        autoPayListView.populate(value);
    }

    class AutoPayAgreementsToolbar extends Toolbar {
        public AutoPayAgreementsToolbar() {
            Button autoPayButton = new Button("Add Auto Pay Agreement", new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().addAutoPay();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(autoPayButton);
        }
    }

    class AutoPayListView extends CEntityForm<AutoPaySummaryDTO> {

        private final BasicFlexFormPanel mainPanel;

        public AutoPayListView() {
            super(AutoPaySummaryDTO.class);

            mainPanel = new BasicFlexFormPanel();

            doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

            AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {
                @Override
                public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                    doLayout(event.getLayoutType());
                }
            });
        }

        private void doLayout(LayoutType layoutType) {

        }

        @Override
        public IsWidget createContent() {
            int row = -1;

            mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nextAutoPayDate(), new CDateLabel()), 100).labelWidth(12).build());
            mainPanel.setBR(++row, 0, 1);
            mainPanel.setWidget(++row, 0, inject(proto().currentAutoPayments(), new AutoPayFolder()));

            return mainPanel;
        }
    }

    private class AutoPayFolder extends VistaBoxFolder<AutoPayInfoDTO> {

        public AutoPayFolder() {
            super(AutoPayInfoDTO.class, true);

            setOrderable(false);
            setAddable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof AutoPayInfoDTO) {
                return new AutoPayViewer();
            }
            return super.create(member);
        }

        @Override
        protected void removeItem(final CEntityFolderItem<AutoPayInfoDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    AutoPayFolder.super.removeItem(item);
                    getGadgetView().getPresenter().deletePreauthorizedPayment(item.getValue());
                }
            });
        }

        private class AutoPayViewer extends CEntityDecoratableForm<AutoPayInfoDTO> {

            private final BasicFlexFormPanel expirationWarning = new BasicFlexFormPanel();

            public AutoPayViewer() {
                super(AutoPayInfoDTO.class);

                setViewable(true);
                inheritViewable(false);

                Widget expirationWarningLabel = new HTML(i18n.tr("This Pre-Authorized Payment is expired - needs to be replaced with new one!"));
                expirationWarningLabel.setStyleName(VistaTheme.StyleName.warningMessage.name());
                expirationWarning.setWidget(0, 0, expirationWarningLabel);
                expirationWarning.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
                expirationWarning.setHR(1, 0, 1);
                expirationWarning.setBR(2, 0, 1);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, expirationWarning);
                content.setWidget(++row, 0, inject(proto().payer(), new CEntityLabel<Tenant>()));
                content.setWidget(++row, 0, inject(proto().paymentMethod(), new CEntityLabel<PaymentMethod>()));
                content.setWidget(++row, 0, inject(proto().amount()));

                return content;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                expirationWarning.setVisible(!getValue().expiring().isNull());

                ((CEntityFolderItem<AutoPayInfoDTO>) getParent()).setRemovable(!getValue().paymentMethod().isEmpty());
            }
        }
    }
}
