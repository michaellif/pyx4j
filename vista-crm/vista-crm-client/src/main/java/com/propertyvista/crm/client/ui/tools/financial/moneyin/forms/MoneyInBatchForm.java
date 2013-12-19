/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.forms;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

public class MoneyInBatchForm extends CrmEntityForm<MoneyInBatchDTO> {

    private static final I18n i18n = I18n.get(MoneyInBatchForm.class);

    public MoneyInBatchForm(IForm<MoneyInBatchDTO> view) {
        super(MoneyInBatchDTO.class, view);
        selectTab(addTab(createGeneralTab(), i18n.tr("Batch Details")));
    }

    private Widget createGeneralTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        return panel;
    }

}
