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
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
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
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

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

    class PaymentMethodsView extends CEntityForm<PaymentMethodSummaryDTO> {

        public PaymentMethodsView() {
            super(PaymentMethodSummaryDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, inject(proto().paymentMethods(), new PaymentMethodFolder(this)));

            return content;
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
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PaymentMethodInfoDTO) {
                return new PaymentMethodViewer();
            }
            return super.create(member);
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PaymentMethodInfoDTO> item) {
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

        private class PaymentMethodViewer extends CEntityForm<PaymentMethodInfoDTO> {

            public PaymentMethodViewer() {
                super(PaymentMethodInfoDTO.class);

                setViewable(true);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().paymentMethod().creationDate(), new CDateLabel(), new FieldDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().paymentMethod().type(), new CEnumLabel(), new FieldDecoratorBuilder(150).build()));
                content.setWidget(++row, 0,
                        inject(proto().paymentMethod().details(), new CEntityLabel<PaymentDetails>(), new FieldDecoratorBuilder().build()));
                content.setWidget(++row, 0,
                        inject(proto().paymentMethod().billingAddress(), new CEntityLabel<AddressSimple>(), new FieldDecoratorBuilder().build()));

                content.setWidget(++row, 0, new Anchor(i18n.tr("View Details"), new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewPaymentMethod(getValue());
                    }
                }));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                if (getValue().usedByAutoPay().getValue(false)) {
                    ((CEntityFolderItem<PaymentMethodInfoDTO>) getParent()).setRemovable(parentView.getValue().allowCancelationByResident().getValue(false));
                }

                if (getValue().restricted().getValue(false)) {
                    get(proto().paymentMethod().type()).setNote(i18n.tr("This Payment Method Type is currently restricted and cannot be used in Portal!"),
                            NoteStyle.Warn);
                }
            }
        }
    }
}
