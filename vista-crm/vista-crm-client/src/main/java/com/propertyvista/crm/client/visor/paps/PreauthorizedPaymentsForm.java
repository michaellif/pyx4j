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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentsDTO> {

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, new DecoratorBuilder(inject(proto().tenant(), new CEntityLabel<Tenant>()), 25).build());
        main.setH4(1, 0, 1, proto().tenant().preauthorizedPayments().getMeta().getCaption());
        main.setWidget(2, 0, inject(proto().tenant().preauthorizedPayments(), new PreauthorizedPaymentsFolder(this)));

        return main;
    }
}
