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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormWidgetDecoratorBuilder;

public class AutoPayAgreementsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(AutoPayAgreementsGadget.class);

    private final AutoPaysView view;

    AutoPayAgreementsGadget(FinancialDashboardViewImpl dashboardView) {
        super(dashboardView, PortalImages.INSTANCE.billingIcon(), i18n.tr("Auto Pay Agreements"), ThemeColor.contrast4, 1);
        setActionsToolbar(new AutoPayAgreementsToolbar());

        view = new AutoPaysView();
        view.setViewable(true);
        view.initContent();

        setContent(view);
    }

    protected void populate(AutoPaySummaryDTO value) {
        view.populate(value);
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

    class AutoPaysView extends CEntityForm<AutoPaySummaryDTO> {

        private final BasicFlexFormPanel mainPanel;

        public AutoPaysView() {
            super(AutoPaySummaryDTO.class);

            mainPanel = new BasicFlexFormPanel();

        }

        @Override
        public IsWidget createContent() {
            int row = -1;

            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().nextAutoPayDate(), new CDateLabel()), 100).build());
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
        public IFolderItemDecorator<AutoPayInfoDTO> createItemDecorator() {
            BoxFolderItemDecorator<AutoPayInfoDTO> decor = (BoxFolderItemDecorator<AutoPayInfoDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
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

            public AutoPayViewer() {
                super(AutoPayInfoDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().payer(), new CEntityLabel<Tenant>()), 250).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<PaymentMethod>()), 250).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().amount()), 100).build());

                content.setWidget(++row, 0, new Anchor("View Details", new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewPreauthorizedPayment(getValue());
                    }
                }));

                return content;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().paymentMethod()).setVisible(!getValue().paymentMethod().isNull());

                ((CEntityFolderItem<AutoPayInfoDTO>) getParent()).setRemovable(!getValue().paymentMethod().isEmpty());
            }
        }
    }
}
