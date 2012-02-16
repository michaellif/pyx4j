/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit.dialogs;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public abstract class MakeVacantDialog extends OkCancelDialog {

    private final static I18n i18n = I18n.get(MakeVacantDialog.class);

    private CDatePicker startDate;

    public MakeVacantDialog() {
        super(i18n.tr("Make Vacant..."));
        setBody(createBody());
    }

    private Widget createBody() {
        FormFlexPanel body = new FormFlexPanel();

        body.setWidget(0, 0, new HTML(i18n.tr("Starting From") + ":"));
        body.setWidget(0, 1, startDate = new CDatePicker());
        startDate.setValue(new LogicalDate());

        return body;
    }

    protected LogicalDate getStartingDate() {
        return new LogicalDate(startDate.getValue());
    }
}
