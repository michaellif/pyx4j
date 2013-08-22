/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.misc.PapExpirationWarning;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView.Presenter;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentListDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private static String cutOffDateWarning = i18n.tr("All changes will take effect after this date!");

    private PreauthorizedPaymentsView.Presenter presenter;

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentListDTO.class, new VistaViewersComponentFactory());
        setViewable(true);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        VerticalPanel container = new VerticalPanel();

        container.add(new FormDecoratorBuilder(inject(proto().currentPaymentDate(), new CDateLabel()), 10).labelWidth(18).build());
        container.add(new FormDecoratorBuilder(inject(proto().nextPaymentDate(), new CDateLabel()), 10).labelWidth(18).build());
        container.add(new HTML("&nbsp"));
        container.add(inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));

        return container;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().preauthorizedPayments()).setEditable(!getValue().isMoveOutWithinNextBillingCycle().getValue(false));

        if (!getValue().currentPaymentDate().equals(getValue().nextPaymentDate())) {
            get(proto().nextPaymentDate()).setNote(cutOffDateWarning, NoteStyle.Warn);
            get(proto().currentPaymentDate()).setVisible(true);
        } else {
            get(proto().nextPaymentDate()).setNote(null);
            get(proto().currentPaymentDate()).setVisible(false);
        }
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentListDTO.ListItemDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentListDTO.ListItemDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentListDTO.ListItemDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentListDTO.ListItemDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                    presenter.deletePreauthorizedPayment(item.getValue());
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentListDTO.ListItemDTO> {

            private final PapExpirationWarning expirationWarning = new PapExpirationWarning();

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentListDTO.ListItemDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @SuppressWarnings("rawtypes")
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, expirationWarning.getExpirationWarningPanel());
                content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>()));

                content.setHR(++row, 0, 1);

                content.setWidget(row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()));
                ((CField) get(proto().paymentMethod())).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        presenter.viewPaymentMethod(getValue());
                    }
                });

                content.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
                get(proto().coveredItems()).setViewable(true);
                get(proto().coveredItems()).inheritViewable(false);

                return content;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                expirationWarning.prepareView(getValue().expiring());

                boolean isCurrentTenant = getValue().tenant().customer().user().getPrimaryKey().equals(ClientContext.getUserVisit().getPrincipalPrimaryKey());
                ((CEntityFolderItem<PreauthorizedPaymentListDTO.ListItemDTO>) getParent()).setRemovable(isCurrentTenant);
            }
        }
    }
}
