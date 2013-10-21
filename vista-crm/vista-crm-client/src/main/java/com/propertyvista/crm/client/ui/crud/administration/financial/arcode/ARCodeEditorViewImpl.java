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
package com.propertyvista.crm.client.ui.crud.administration.financial.arcode;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.financial.ARCode;

public class ARCodeEditorViewImpl extends CrmEditorViewImplBase<ARCode> implements ARCodeEditorView {

    public ARCodeEditorViewImpl() {
        setForm(new ARCodeForm(this));
    }

    @Override
    public void setYardiIntegrationModeEnabled(boolean enabled) {
        ((HasYardiIntegrationMode) getForm()).setYardiIntegrationModeEnabled(enabled);
    }

    @Override
    public void reportSaveError(UserRuntimeException caught) {
        MessageDialog.error("", caught.getMessage());
    }

}
