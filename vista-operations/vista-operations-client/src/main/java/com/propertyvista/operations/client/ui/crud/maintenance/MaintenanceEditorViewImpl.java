/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 */
package com.propertyvista.operations.client.ui.crud.maintenance;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;

public class MaintenanceEditorViewImpl extends OperationsEditorViewImplBase<VistaSystemMaintenanceState> implements MaintenanceEditorView {

    public MaintenanceEditorViewImpl() {
        setForm(new MaintenanceForm(this));
    }
}
