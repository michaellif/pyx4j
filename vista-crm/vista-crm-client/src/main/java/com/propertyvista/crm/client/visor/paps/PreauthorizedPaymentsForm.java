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

import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.PreauthorizedPaymentsFolder;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentsDTO> {

    private final PreauthorizedPaymentsVisorView visor;

    public PreauthorizedPaymentsForm(PreauthorizedPaymentsVisorView visor) {
        super(PreauthorizedPaymentsDTO.class);
        this.visor = visor;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().tenantInfo(), new CEntityLabel<PreauthorizedPaymentsDTO.TenantInfo>()));
        main.setH3(1, 0, 1, proto().preauthorizedPayments().getMeta().getCaption());
        main.setWidget(2, 0, inject(proto().preauthorizedPayments(), new PreauthorizedPaymentsFolder() {

            @Override
            public List<LeasePaymentMethod> getAvailablePaymentMethods() {
                return PreauthorizedPaymentsForm.this.getValue().availablePaymentMethods();
            }

            @Override
            protected void createNewEntity(final AsyncCallback<PreauthorizedPayment> callback) {
                visor.getController().create(new DefaultAsyncCallback<PreauthorizedPayment>() {
                    @Override
                    public void onSuccess(PreauthorizedPayment result) {
                        callback.onSuccess(result);
                    }
                });
            }
        }));

        main.getWidget(0, 0).setWidth("50em");
        main.getWidget(0, 0).getElement().getStyle().setMargin(0.5, Unit.EM);
        main.getWidget(0, 0).getElement().getStyle().setMarginLeft(1, Unit.EM);
        main.getWidget(0, 0).getElement().getStyle().setFontWeight(FontWeight.BOLD);
        main.getWidget(0, 0).getElement().getStyle().setFontSize(1.2, Unit.EM);

        return main;
    }
}
