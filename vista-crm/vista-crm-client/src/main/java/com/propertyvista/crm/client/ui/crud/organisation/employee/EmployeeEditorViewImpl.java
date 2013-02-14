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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeEditorViewImpl extends CrmEditorViewImplBase<EmployeeDTO> implements EmployeeEditorView {

    public EmployeeEditorViewImpl() {
        setForm(new EmployeeForm(this));
    }

    @Override
    public void restrictSecuritySensitiveControls(boolean isManager, boolean isSelfEditor) {
        ((EmployeeForm) getForm()).restrictSecurityRelatedControls(isManager, isSelfEditor);

    }
}
