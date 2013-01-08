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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.domain.pmc.PmcPaymentMethod;

public class PmcPaymentMethodsForm extends CrmEntityForm<PmcPaymentMethodsDTO> {

    private static final I18n i18n = I18n.get(PmcPaymentMethod.class);

    public PmcPaymentMethodsForm(IFormView<PmcPaymentMethodsDTO> view) {
        super(PmcPaymentMethodsDTO.class, view);
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Payment Methods"));
        selectTab(addTab(content));
    }

}
