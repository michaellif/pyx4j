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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
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

        autoPayButton.setVisible(!value.leaseStatus().getValue().isNoAutoPay() && SecurityController.check(VistaCustomerPaymentTypeBehavior.forAutoPay()));
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

    class AutoPaysView extends CForm<AutoPaySummaryDTO> {

        public AutoPaysView() {
            super(AutoPaySummaryDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().nextAutoPayDate(), new CDateLabel()).decorate().componentWidth(100);
            formPanel.br();
            formPanel.append(Location.Left, proto().currentAutoPayments(), new AutoPayFolder(this));

            return formPanel;
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
        public BoxFolderItemDecorator<AutoPayInfoDTO> createItemDecorator() {
            BoxFolderItemDecorator<AutoPayInfoDTO> decor = super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        protected CForm<AutoPayInfoDTO> createItemForm(IObject<?> member) {
            return new AutoPayViewer();
        }

        @Override
        protected void removeItem(final CFolderItem<AutoPayInfoDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    AutoPayFolder.super.removeItem(item);
                    getGadgetView().getPresenter().deletePreauthorizedPayment(item.getValue());
                }
            });
        }

        private class AutoPayViewer extends CForm<AutoPayInfoDTO> {

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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().payer(), new CEntityLabel<Tenant>()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<PaymentMethod>()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(100);

                formPanel.append(Location.Left, detailsViewAnchor);

                return formPanel;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().paymentMethod()).setVisible(!getValue().paymentMethod().isNull());
                detailsViewAnchor.setVisible(!getValue().paymentMethod().isNull());

                ((CFolderItem<AutoPayInfoDTO>) getParent()).setRemovable(!getValue().paymentMethod().isNull()
                        && parentView.getValue().allowCancelationByResident().getValue(false));

                if (getValue().paymentMethodRestricted().getValue(false)) {
                    get(proto().paymentMethod()).setNote(i18n.tr("This Payment Method Type is currently restricted and cannot be used in Portal!"),
                            NoteStyle.Warn);
                }
            }
        }
    }
}
