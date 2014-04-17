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
package com.propertyvista.portal.resident.ui.financial.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayInfoDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPaySummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class AutoPayAgreementsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(AutoPayAgreementsGadget.class);

    private final AutoPaysView view;

    private final Button autoPayButton = new Button("Add Auto Pay Agreement");

    AutoPayAgreementsGadget(FinancialDashboardViewImpl dashboardView) {
        super(dashboardView, PortalImages.INSTANCE.billingIcon(), i18n.tr("Auto Pay Agreements"), ThemeColor.contrast4, 1);
        setActionsToolbar(new AutoPayAgreementsToolbar());

        view = new AutoPaysView();
        view.setViewable(true);
        view.init();

        asWidget().setWidth("100%");
        setContent(view);
    }

    protected void populate(AutoPaySummaryDTO value) {
        view.populate(value);

        autoPayButton.setVisible(!value.leaseStatus().getValue().isNoAutoPay()
                && SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.forAutoPay()));
    }

    class AutoPayAgreementsToolbar extends GadgetToolbar {
        public AutoPayAgreementsToolbar() {
            autoPayButton.setCommand(new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().addAutoPay();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(autoPayButton);
        }
    }

    class AutoPaysView extends CEntityForm<AutoPaySummaryDTO> {

        public AutoPaysView() {
            super(AutoPaySummaryDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
            int row = -1;

            mainPanel.setWidget(++row, 0, inject(proto().nextAutoPayDate(), new CDateLabel(), new FieldDecoratorBuilder(100).build()));
            mainPanel.setBR(++row, 0, 1);
            mainPanel.setWidget(++row, 0, inject(proto().currentAutoPayments(), new AutoPayFolder(this)));

            return mainPanel;
        }
    }

    private class AutoPayFolder extends PortalBoxFolder<AutoPayInfoDTO> {

        private final AutoPaysView parentView;

        public AutoPayFolder(AutoPaysView parentView) {
            super(AutoPayInfoDTO.class, true);
            this.parentView = parentView;

            setEditable(true);
            inheritViewable(false);

            setOrderable(false);
            setAddable(false);
            setRemovable(true);
        }

        @Override
        public IFolderItemDecorator<AutoPayInfoDTO> createItemDecorator() {
            BoxFolderItemDecorator<AutoPayInfoDTO> decor = (BoxFolderItemDecorator<AutoPayInfoDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public <T extends CComponent<T, ?>> T create(IObject<?> member) {
            if (member instanceof AutoPayInfoDTO) {
                return (T) new AutoPayViewer();
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

        private class AutoPayViewer extends CEntityForm<AutoPayInfoDTO> {

            private final Anchor detailsViewAnchor = new Anchor("View Details", new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().viewPreauthorizedPayment(getValue());
                }
            });

            public AutoPayViewer() {
                super(AutoPayInfoDTO.class);

                setViewable(true);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().payer(), new CEntityLabel<Tenant>(), new FieldDecoratorBuilder(250).build()));
                content.setWidget(++row, 0, inject(proto().paymentMethod(), new CEntityLabel<PaymentMethod>(), new FieldDecoratorBuilder(250).build()));
                content.setWidget(++row, 0, inject(proto().amount(), new FieldDecoratorBuilder(100).build()));

                content.setWidget(++row, 0, detailsViewAnchor);

                return content;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().paymentMethod()).setVisible(!getValue().paymentMethod().isNull());
                detailsViewAnchor.setVisible(!getValue().paymentMethod().isNull());

                ((CEntityFolderItem<AutoPayInfoDTO>) getParent()).setRemovable(!getValue().paymentMethod().isNull()
                        && parentView.getValue().allowCancelationByResident().getValue(false));
            }
        }
    }
}
