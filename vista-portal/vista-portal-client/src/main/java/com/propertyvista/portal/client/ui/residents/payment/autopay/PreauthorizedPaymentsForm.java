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

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView.Presenter;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentItemDTO;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsForm extends CEntityForm<PreauthorizedPaymentListDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private PreauthorizedPaymentsView.Presenter presenter;

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentListDTO.class, new VistaViewersComponentFactory());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel container = new FlowPanel();
        container.add(inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));
        return container;
    }

    public void populate(List<PreauthorizedPaymentItemDTO> preauthorizedPayment) {
        PreauthorizedPaymentListDTO dto = EntityFactory.create(PreauthorizedPaymentListDTO.class);
        dto.preauthorizedPayments().addAll(preauthorizedPayment);
        super.populate(dto);
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentItemDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentItemDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentItemDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentItemDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
                @Override
                public void execute() {
                    presenter.deletePreauthorizedPayment(item.getValue());
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentItemDTO> {

            private final SimplePanel amountPlaceholder = new SimplePanel();

            private final Widget percent;

            private final Widget value;

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentItemDTO.class);

                setViewable(true);
                inheritViewable(false);

                amountPlaceholder.setWidth("21em");
                percent = new DecoratorBuilder(inject(proto().percent()), 10, 10).build();
                value = new DecoratorBuilder(inject(proto().value()), 10, 10).build();
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>()));
                content.setHR(++row, 0, 2);
                content.setWidget(++row, 0, amountPlaceholder);
                content.setWidget(row, 1, inject(proto().paymentMethod(), new CEntityHyperlink<LeasePaymentMethod>(null, new Command() {
                    @Override
                    public void execute() {
                        presenter.viewPaymentMethod(getValue());
                    }
                })));
                content.getCellFormatter().setWidth(row, 0, "25em");

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                setAmountEditor(getValue().amountType().getValue());

                setEditable(getValue().getPrimaryKey() == null);
                setRemovable(!getValue().isCoTenant().isBooleanTrue());
            }

            private void setAmountEditor(AmountType amountType) {
                amountPlaceholder.clear();
                get(proto().percent()).setVisible(false);
                get(proto().value()).setVisible(false);

                if (amountType != null) {
                    switch (amountType) {
                    case Percent:
                        amountPlaceholder.setWidget(percent);
                        get(proto().percent()).setVisible(true);
                        break;

                    case Value:
                        amountPlaceholder.setWidget(value);
                        get(proto().value()).setVisible(true);
                        break;

                    default:
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }
}
