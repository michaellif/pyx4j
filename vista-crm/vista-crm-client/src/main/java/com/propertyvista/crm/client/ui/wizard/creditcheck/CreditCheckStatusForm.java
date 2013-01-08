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
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;

public class CreditCheckStatusForm extends CrudEntityForm<CreditCheckStatusDTO> {

    public CreditCheckStatusForm(IFormView<CreditCheckStatusDTO> view) {
        super(CreditCheckStatusDTO.class, view);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;
        return contentPanel;
    }

}
