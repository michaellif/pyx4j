/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.profile.paymentmethods;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.PmcPaymentMethod;

public class PmcPaymentMethodFolder extends VistaBoxFolder<PmcPaymentMethod> {

    public class PmcPaymentMethodEditor extends PaymentMethodEditor<PmcPaymentMethod> {

        public PmcPaymentMethodEditor() {
            super(PmcPaymentMethod.class);
        }

        @Override
        public List<PaymentType> getPaymentOptions() {
            return Arrays.asList(PaymentType.CreditCard);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            setTypeSelectionVisible(false);
            setIsPreauthorizedVisible(true);
            setBillingAddressVisible(false);
            setBillingAddressAsCurrentVisible(false);
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().setAsActive()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().booleanValue()) {
                        for (int i = 0; i < PmcPaymentMethodFolder.this.getItemCount(); ++i) {
                            for (CComponent<?, ?> comp : PmcPaymentMethodFolder.this.getItem(i).getComponents()) {
                                if (comp instanceof PmcPaymentMethodEditor && !comp.equals(PmcPaymentMethodEditor.this)) {
                                    ((PmcPaymentMethodEditor) comp).get(proto().setAsActive()).setValue(false, false);
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    public PmcPaymentMethodFolder() {
        super(PmcPaymentMethod.class);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PmcPaymentMethod) {
            return new PmcPaymentMethodEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        PmcPaymentMethod pmcPaymentMethod = EntityFactory.create(PmcPaymentMethod.class);
        pmcPaymentMethod.type().setValue(PaymentType.CreditCard);
        pmcPaymentMethod.details().set(EntityFactory.create(CreditCardInfo.class));
        addItem(pmcPaymentMethod);
    }
}
