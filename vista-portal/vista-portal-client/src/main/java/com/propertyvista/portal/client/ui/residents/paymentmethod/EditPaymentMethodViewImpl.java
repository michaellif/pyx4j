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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.decorations.UserMessagePanel;

public class EditPaymentMethodViewImpl extends FlowPanel implements EditPaymentMethodView {

    private static final I18n i18n = I18n.get(EditPaymentMethodViewImpl.class);

    private final PaymentMethodForm form;

    private Presenter presenter;

    public EditPaymentMethodViewImpl() {
        add(new UserMessagePanel());

        form = new PaymentMethodForm() {
            @Override
            public Collection<PaymentType> getPaymentOptions() {
                return PaymentType.avalableInProfile();
            }

            @Override
            public void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
                if (set) {
                    assert (presenter != null);
                    presenter.getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                        @Override
                        public void onSuccess(AddressStructured result) {
                            comp.setValue(result, false);
                        }
                    });
                } else {
                    comp.setValue(EntityFactory.create(AddressStructured.class), false);
                }
            }
        };
        form.initContent();
        add(form);

        Button submitButton = new Button(i18n.tr("Save"));
        submitButton.getElement().getStyle().setMargin(20, Unit.PX);
        submitButton.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

        submitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!form.isValid()) {
                    Window.scrollTo(0, 0);
                    throw new UserRuntimeException(form.getValidationResults().getMessagesText(true));
                } else {
                    presenter.save(form.getValue());
                }
            }
        });
        add(DecorationUtils.inline(submitButton));

        CHyperlink cancel = new CHyperlink(new Command() {
            @Override
            public void execute() {
                presenter.cancel();
            }
        });
        cancel.setValue(i18n.tr("Cancel"));
        cancel.asWidget().getElement().getStyle().setMarginTop(20, Unit.PX);
        cancel.asWidget().getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        add(cancel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public void populate(PaymentMethod paymentMethod) {
        form.reset();
        form.populate(paymentMethod);
    }

}
