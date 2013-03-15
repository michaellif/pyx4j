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
package com.propertyvista.crm.client.visor.paps;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.AmountType;

public class PreauthorizedPaymentsFolder extends VistaTableFolder<PreauthorizedPayment> {

    public PreauthorizedPaymentsFolder() {
        super(PreauthorizedPayment.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().amountType(), "10em"),
                new EntityFolderColumnDescriptor(proto().amount(), "10em"),
                new EntityFolderColumnDescriptor(proto().paymentMethod(), "30em"));
          //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPayment) {
            return new PreauthorizedPaymentEditor();
        }
        return super.create(member);
    }

    private class PreauthorizedPaymentEditor extends CEntityFolderRowEditor<PreauthorizedPayment> {

        public PreauthorizedPaymentEditor() {
            super(PreauthorizedPayment.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().paymentMethod() == column.getObject()) {
                return inject(proto().paymentMethod(), new CEntitySelectorHyperlink() {

                    @Override
                    protected AppPlace getTargetPlace() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    protected AbstractEntitySelectorDialog getSelectorDialog() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
            } else if (proto().amountType() == column.getObject()) {
                CComponent<AmountType, ?> comp = (CComponent<AmountType, ?>) super.createCell(column);
                comp.addValueChangeHandler(new ValueChangeHandler<AmountType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<AmountType> event) {
                        bindAmountEditor(event.getValue(), false);
                    }
                });
                return comp;
            } else if (proto().amount() == column.getObject()) {
                return inject(proto().amount(), new CLabel<BigDecimal>());
            }

            return super.createCell(column);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            bindAmountEditor(getValue().amountType().getValue(), populate);
        }

        private void bindAmountEditor(AmountType valueType, boolean repopulate) {
            CComponent<?, ?> comp = null;
            if (valueType != null) {
                switch (valueType) {
                case Value:
                    comp = new CMoneyField();
                    break;
                case Percent:
                    comp = new CPercentageField();
                    break;
                }
            }

            if (comp != null) {
                IDecorator<CComponent<?, ?>> decor = get((proto().amount())).getDecorator();
                unbind(proto().amount());

                inject(proto().amount(), comp);
                comp.setDecorator(decor);

                if (repopulate) {
                    get(proto().amount()).populate(getValue().amount().getValue(BigDecimal.ZERO));
                }
            }
        }
    }
}
