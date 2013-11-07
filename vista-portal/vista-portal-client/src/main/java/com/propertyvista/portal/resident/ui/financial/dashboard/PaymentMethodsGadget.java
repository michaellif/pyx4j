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
import com.pyx4j.entity.shared.IObject;
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
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.PaymentDetails;
import com.propertyvista.portal.resident.ui.AbstractGadget;
import com.propertyvista.portal.resident.ui.util.decorators.FormWidgetDecoratorBuilder;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.financial.PaymentMethodSummaryDTO;
import com.propertyvista.portal.shared.resources.PortalImages;

public class PaymentMethodsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(PaymentMethodsGadget.class);

    private final PaymentMethodsView view;

    PaymentMethodsGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Payment Methods"), ThemeColor.contrast4, 1);
        setActionsToolbar(new PaymentMethodsToolbar());

        view = new PaymentMethodsView();
        view.setViewable(true);
        view.initContent();

        setContent(view);
    }

    protected void populate(PaymentMethodSummaryDTO value) {
        view.populate(value);
    }

    class PaymentMethodsToolbar extends Toolbar {
        public PaymentMethodsToolbar() {
            Button autoPayButton = new Button("Add Payment Method", new Command() {
                @Override
                public void execute() {
                    getGadgetView().getPresenter().addPaymentMethod();
                }
            });
            autoPayButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));
            add(autoPayButton);
        }
    }

    class PaymentMethodsView extends CEntityForm<PaymentMethodSummaryDTO> {

        public PaymentMethodsView() {
            super(PaymentMethodSummaryDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, inject(proto().paymentMethods(), new PaymentMethodFolder()));

            return content;
        }
    }

    private class PaymentMethodFolder extends VistaBoxFolder<PaymentMethodInfoDTO> {

        public PaymentMethodFolder() {
            super(PaymentMethodInfoDTO.class, true);

            setOrderable(false);
            setAddable(false);
        }

        @Override
        public IFolderItemDecorator<PaymentMethodInfoDTO> createItemDecorator() {
            BoxFolderItemDecorator<PaymentMethodInfoDTO> decor = (BoxFolderItemDecorator<PaymentMethodInfoDTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PaymentMethodInfoDTO) {
                return new PaymentMethodViewer();
            }
            return super.create(member);
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PaymentMethodInfoDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Payment Method?"), new Command() {
                @Override
                public void execute() {
                    PaymentMethodFolder.super.removeItem(item);
                    getGadgetView().getPresenter().deletePaymentMethod(item.getValue());
                }
            });
        }

        private class PaymentMethodViewer extends CEntityDecoratableForm<PaymentMethodInfoDTO> {

            public PaymentMethodViewer() {
                super(PaymentMethodInfoDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().paymentMethod().creationDate(), new CDateLabel()), 100).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().paymentMethod().type(), new CEnumLabel()), 150).build());
                content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().paymentMethod().details(), new CEntityLabel<PaymentDetails>())).build());
                content.setWidget(++row, 0,
                        new FormWidgetDecoratorBuilder(inject(proto().paymentMethod().billingAddress(), new CEntityLabel<AddressSimple>())).build());

                content.setWidget(++row, 0, new Anchor("View Details", new Command() {
                    @Override
                    public void execute() {
                        getGadgetView().getPresenter().viewPaymentMethod(getValue());
                    }
                }));

                return content;
            }
        }
    }
}
