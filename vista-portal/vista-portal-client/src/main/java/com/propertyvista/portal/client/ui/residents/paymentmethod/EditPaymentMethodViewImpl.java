/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.paymentmethod;

import java.util.Collection;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.EditImpl;
import com.propertyvista.portal.client.ui.residents.payment.PortalPaymentTypesUtil;

public class EditPaymentMethodViewImpl extends EditImpl<LeasePaymentMethod> implements EditPaymentMethodView {

    public EditPaymentMethodViewImpl() {
        setForm(new PaymentMethodForm<LeasePaymentMethod>(LeasePaymentMethod.class) {
            @Override
            public Collection<PaymentType> defaultPaymentTypes() {
                return PortalPaymentTypesUtil.getAllowedPaymentTypes();
            }

            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured> comp) {
                if (set) {
                    assert (getPresenter() != null);
                    ((EditPaymentMethodView.Presenter) getPresenter()).getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                        @Override
                        public void onSuccess(AddressStructured result) {
                            comp.setValue(result, false);
                        }
                    });
                } else {
                    comp.setValue(EntityFactory.create(AddressStructured.class), false);
                }
            }
        });
    }
}
