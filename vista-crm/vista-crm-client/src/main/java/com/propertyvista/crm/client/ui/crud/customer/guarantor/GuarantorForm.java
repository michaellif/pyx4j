/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.guarantor;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantForm;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorForm extends LeaseParticipantForm<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorForm.class);

    public GuarantorForm(IPrimeFormView<GuarantorDTO, ?> view) {
        super(GuarantorDTO.class, view);

        Tab tab;

        selectTab(addTab(createDetailsTab(), i18n.tr("Details")));
        tab = addTab(createPaymentMethodsTab(), i18n.tr("Payment Methods"), DataModelPermission.permissionRead(LeasePaymentMethod.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeasePaymentMethod.class));
        }
    }
}