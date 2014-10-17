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

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.folders.PapFolder;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsForm extends CForm<PreauthorizedPaymentsDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private final PreauthorizedPaymentsVisorView visor;

    public PreauthorizedPaymentsForm(PreauthorizedPaymentsVisorView visor) {
        super(PreauthorizedPaymentsDTO.class);
        this.visor = visor;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.br();
        formPanel.append(Location.Left, proto().tenantInfo(), new CEntityLabel<PreauthorizedPaymentsDTO.TenantInfo>()).decorate();
        get(proto().tenantInfo()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        get(proto().tenantInfo()).asWidget().getElement().getStyle().setFontSize(1.2, Unit.EM);

        formPanel.append(Location.Right, proto().nextPaymentDate(), new CDateLabel()).decorate().labelWidth(200);

        formPanel.h3(proto().preauthorizedPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().preauthorizedPayments(), new PapFolder() {
            @Override
            protected void createNewEntity(final AsyncCallback<PreauthorizedPaymentDTO> callback) {
                visor.getController().create(new DefaultAsyncCallback<PreauthorizedPaymentDTO>() {
                    @Override
                    public void onSuccess(PreauthorizedPaymentDTO result) {
                        callback.onSuccess(result);
                    }
                });
            }

            @Override
            protected List<LeasePaymentMethod> getPaymentMethods() {
                return PreauthorizedPaymentsForm.this.getValue().availablePaymentMethods();
            }
        });
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        this.setEditable(SecurityController.check(getValue(), DataModelPermission.permissionUpdate(PreauthorizedPaymentsDTO.class)));
    }
}
