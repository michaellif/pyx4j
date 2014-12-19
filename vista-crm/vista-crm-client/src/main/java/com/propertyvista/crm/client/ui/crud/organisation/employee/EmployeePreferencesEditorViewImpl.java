/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.dto.CrmUserDeliveryPreferencesDTO;

public class EmployeePreferencesEditorViewImpl extends CrmEditorViewImplBase<CrmUserDeliveryPreferencesDTO> implements EmployeePreferencesEditorView {
    private static final I18n i18n = I18n.get(EmployeePreferencesForm.class);

    public EmployeePreferencesEditorViewImpl() {
        setForm(new EmployeePreferencesForm(this));
        setCaption(i18n.tr("Account Preferences"));
    }

}
