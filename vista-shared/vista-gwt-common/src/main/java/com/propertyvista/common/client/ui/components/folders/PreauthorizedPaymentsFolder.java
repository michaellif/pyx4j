/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;

public abstract class PreauthorizedPaymentsFolder extends VistaBoxFolder<PreauthorizedPayment> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsFolder.class);

    public PreauthorizedPaymentsFolder() {
        super(PreauthorizedPayment.class);
        setOrderable(false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPayment) {
            return new PreauthorizedPaymentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
    }

    @Override
    protected CEntityFolderItem<PreauthorizedPayment> createItem(boolean first) {
        // TODO Auto-generated method stub
        return super.createItem(first);
    }

    @Override
    protected void createNewEntity(AsyncCallback<PreauthorizedPayment> callback) {
        PreauthorizedPayment newEntity = EntityFactory.create(PreauthorizedPayment.class);

        newEntity.amountType().setValue(AmountType.Value);
        newEntity.percent().setValue(BigDecimal.ZERO);

        callback.onSuccess(newEntity);
    }

    @Override
    protected void removeItem(final CEntityFolderItem<PreauthorizedPayment> item) {
        MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
            @Override
            public void execute() {
                PreauthorizedPaymentsFolder.super.removeItem(item);
            }
        });
    }

    public abstract List<LeasePaymentMethod> getAvailablePaymentMethods();

    private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPayment> {

        private final SimplePanel amountPlaceholder = new SimplePanel();

        private final Widget percent;

        private final Widget value;

        public PreauthorizedPaymentEditor() {
            super(PreauthorizedPayment.class);

            amountPlaceholder.setWidth("15em");
            percent = new DecoratorBuilder(inject(proto().percent()), 10, 10).build();
            value = new DecoratorBuilder(inject(proto().value()), 10, 10).build();
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amountType()), 10, 10).build());
            content.getFlexCellFormatter().setWidth(row, 0, "21em");
            content.setWidget(row, 1, amountPlaceholder);

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentMethod(), new CEntitySelectorHyperlink<LeasePaymentMethod>() {
                @Override
                protected AppPlace getTargetPlace() {
                    return null; // not intended to navigate - just edit mode!
                }

                @Override
                protected AbstractEntitySelectorDialog<LeasePaymentMethod> getSelectorDialog() {
                    return new EntitySelectorListDialog<LeasePaymentMethod>(i18n.tr("Select Payment Method"), false, getAvailablePaymentMethods()) {
                        @Override
                        public boolean onClickOk() {
                            get(proto().paymentMethod()).setValue(getSelectedItems().iterator().next());
                            return true;
                        }
                    };
                }
            }), 33, 10).build());
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            get(proto().amountType()).addValueChangeHandler(new ValueChangeHandler<AmountType>() {
                @Override
                public void onValueChange(ValueChangeEvent<AmountType> event) {
                    setAmountEditor(event.getValue());
                }
            });

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            setAmountEditor(getValue().amountType().getValue());

            setEditable(getValue().getPrimaryKey() == null);
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
