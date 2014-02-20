/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.lease.common.LegalStatusForm;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.dto.LegalStatusDTO;

public class LegalStatusDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(LegalStatusDialog.class);

    private final LegalStatusForm form;

    public LegalStatusDialog() {
        super(i18n.tr("Set Legal Status"));
        form = new LegalStatusForm(true);
        form.initContent();
        form.populateNew();
        setBody(form);
        setDialogPixelWidth(600);
    }

    @Override
    public boolean onClickOk() {
        form.setVisitedRecursive();
        form.revalidate();
        for (LegalLetter letter : form.getValue().letters()) {
            if (letter.file().isEmpty()) {
                return false;
            }
        }
        if (form.isValid()) {
            onSetLegalStatus(form.getValue());
            return true;
        } else {
            return false;
        }
    }

    /** override to do something useful */
    public void onSetLegalStatus(LegalStatusDTO legalStatus) {

    }

}
