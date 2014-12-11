/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.dto.CrmUserDeliveryPreferencesDTO;

public class EmployeePreferencesViewerViewImpl extends CrmViewerViewImplBase<CrmUserDeliveryPreferencesDTO> implements EmployeePreferencesViewerView {

    private static final I18n i18n = I18n.get(EmployeePreferencesForm.class);

    public EmployeePreferencesViewerViewImpl() {
        setForm(new EmployeePreferencesForm(this));
        setCaption(i18n.tr("Account Preferences"));
    }
}
