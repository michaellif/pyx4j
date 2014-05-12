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
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PaymentMethodsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(PaymentMethodsGadget.class);

    private final PaymentMethodsView view;

    private final Button paymentMethodButton = new Button("Add Payment Method");

    PaymentMethodsGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Payment Methods"), ThemeColor.contrast4, 1);
        setActionsToolbar(new PaymentMethodsToolbar());

        view = new PaymentMethodsView();
        view.setViewable(true);
        view.init();

        asWidget().setWidth("100%");
        setContent(view);
    }

    protected void populate(PaymentMethodSummaryDTO value) {
        view.populate(value);

        paymentMethodButton.setVisible(SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.forPaymentMethodSetup()));
    }

    class PaymentMethodsToolbar extends GadgetToolbar {
        public PaymentMethodsToolbar() {
            paymentMethodButton.setCommand(new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().addPaymentMethod();
                }
            });
            paymentMethodButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            addItem(paymentMethodButton);
        }
    }

    class PaymentMethodsView extends CForm<PaymentMethodSummaryDTO> {

        public PaymentMethodsView() {
            super(PaymentMethodSummaryDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            PortalFormPanel formPanel = new PortalFormPanel(this);
            formPanel.append(Location.Left, proto().paymentMethods(), new PaymentMethodFolder(this));
            return formPanel;
        }
    }

    private class PaymentMethodFolder extends PortalBoxFolder<PaymentMethodInfoDTO> {

        private final PaymentMethodsView parentView;

        public PaymentMethodFolder(PaymentMethodsView parentView) {
            super(PaymentMethodInfoDTO.class, true);
            this.parentView = parentView;

            setEditable(true);
            inheritViewable(false);

            setOrderable(false);
            setAddable(false);
            setRemovable(true);

            setExpended(false);
        }

        @Override
        protected CForm<PaymentMethodInfoDTO> createItemForm(IObject<?> member) {
            return new PaymentMethodViewer();
        }

        @Override
        protected void removeItem(final CFolderItem<PaymentMethodInfoDTO> item) {
            String text = i18n.tr("Do you really want to delete the Payment Method?");
            if (item.getValue().usedByAutoPay().getValue(false)) {
                text = i18n.tr("This Payment Method is used in AutoPay(s). Do you really want to delete it with corresponding AutoPay(s)?");
            }

            MessageDialog.confirm(i18n.tr("Please confirm"), text, new Command() {
                @Override
                public void execute() {
                    PaymentMethodFolder.super.removeItem(item);
                    getGadgetView().getPresenter().deletePaymentMethod(item.getValue());
                }
            });
        }

        private class PaymentMethodViewer extends CForm<PaymentMethodInfoDTO> {

            public PaymentMethodViewer() {
                super(PaymentMethodInfoDTO.class);

                setViewable(true);
            }

            @Override
            protected IsWidget createContent() {
                PortalFormPanel formPanel = new PortalFormPanel(this);

                formPanel.append(Location.Left, proto().paymentMethod().creationDate(), new CDateLabel()).decorate().componentWidth(100);
                formPanel.append(Location.Left, proto().paymentMethod().type(), new CEnumLabel()).decorate().componentWidth(150);
                formPanel.append(Location.Left, proto().paymentMethod().details(), new CEntityLabel<PaymentDetails>()).decorate();
                formPanel.append(Location.Left, proto().paymentMethod().billingAddress(), new CEntityLabel<AddressSimple>()).decorate();
                formPanel.append(Location.Left, new Anchor(i18n.tr("View Details"), new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewPaymentMethod(getValue());
                    }
                }));

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (getValue().usedByAutoPay().getValue(false)) {
                    ((CFolderItem<PaymentMethodInfoDTO>) getParent()).setRemovable(parentView.getValue().allowCancelationByResident().getValue(false));
                }

                if (getValue().restricted().getValue(false)) {
                    get(proto().paymentMethod().type()).setNote(i18n.tr("This Payment Method Type is currently restricted and cannot be used in Portal!"),
                            NoteStyle.Warn);
                }
            }
        }
    }
}
