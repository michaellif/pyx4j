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
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.dto.LegalStatusDTO;
import com.propertyvista.dto.LegalStatusN4DTO;

public class LegalStatusN4Dialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(LegalStatusN4Dialog.class);

    private final LegalStatusN4Form form;

    public LegalStatusN4Dialog(LegalStatusN4DTO initialValue) {
        super(i18n.tr("Set Legal Status"));
        form = new LegalStatusN4Form(true);
        form.init();
        form.populate(initialValue);
        setBody(form);
        setDialogPixelWidth(600);
    }

    @Override
    public boolean onClickOk() {
        form.setVisitedRecursive();
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
