/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.viewfactories.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserEditorView;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserListerView;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserListerViewImpl;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserViewerView;
import com.propertyvista.operations.client.ui.crud.adminusers.AdminUserViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.legal.VistaTermsEditorView;
import com.propertyvista.operations.client.ui.crud.legal.VistaTermsEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.legal.VistaTermsViewerView;
import com.propertyvista.operations.client.ui.crud.legal.VistaTermsViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.maintenance.MaintenanceEditorView;
import com.propertyvista.operations.client.ui.crud.maintenance.MaintenanceEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.maintenance.MaintenanceViewerView;
import com.propertyvista.operations.client.ui.crud.maintenance.MaintenanceViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulation.SimulationEditorView;
import com.propertyvista.operations.client.ui.crud.simulation.SimulationEdtiorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulation.SimulationViewerView;
import com.propertyvista.operations.client.ui.crud.simulation.SimulationViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileListerView;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileListerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileViewerView;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimFileViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordListerView;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordListerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordViewerView;
import com.propertyvista.operations.client.ui.crud.simulator.dbp.DirectDebitSimRecordViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadBatchEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadBatchEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadBatchViewerView;
import com.propertyvista.operations.client.ui.crud.simulator.pad.batch.PadBatchViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileEditorView;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileListerView;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileListerViewImpl;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileViewerView;
import com.propertyvista.operations.client.ui.crud.simulator.pad.file.PadFileViewerViewImpl;
import com.propertyvista.operations.client.ui.crud.systemdefaults.VistaSystemDefaultsEditorView;
import com.propertyvista.operations.client.ui.crud.systemdefaults.VistaSystemDefaultsEditorViewImpl;
import com.propertyvista.operations.client.ui.crud.systemdefaults.VistaSystemDefaultsViewerView;
import com.propertyvista.operations.client.ui.crud.systemdefaults.VistaSystemDefaultsViewerViewImpl;

public class AdministrationVeiwFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IPane> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (MaintenanceViewerView.class.equals(type)) {
                map.put(type, new MaintenanceViewerViewImpl());
            } else if (MaintenanceEditorView.class.equals(type)) {
                map.put(type, new MaintenanceEditorViewImpl());

            } else if (VistaSystemDefaultsViewerView.class.equals(type)) {
                map.put(type, new VistaSystemDefaultsViewerViewImpl());

            } else if (VistaSystemDefaultsEditorView.class.equals(type)) {
                map.put(type, new VistaSystemDefaultsEditorViewImpl());

            } else if (SimulationViewerView.class.equals(type)) {
                map.put(type, new SimulationViewerViewImpl());
            } else if (SimulationEditorView.class.equals(type)) {
                map.put(type, new SimulationEdtiorViewImpl());

            } else if (DirectDebitSimRecordListerView.class.equals(type)) {
                map.put(type, new DirectDebitSimRecordListerViewImpl());
            } else if (DirectDebitSimRecordViewerView.class.equals(type)) {
                map.put(type, new DirectDebitSimRecordViewerViewImpl());
            } else if (DirectDebitSimRecordEditorView.class.equals(type)) {
                map.put(type, new DirectDebitSimRecordEditorViewImpl());

            } else if (DirectDebitSimFileListerView.class.equals(type)) {
                map.put(type, new DirectDebitSimFileListerViewImpl());
            } else if (DirectDebitSimFileViewerView.class.equals(type)) {
                map.put(type, new DirectDebitSimFileViewerViewImpl());

            } else if (PadFileViewerView.class.equals(type)) {
                map.put(type, new PadFileViewerViewImpl());
            } else if (PadFileEditorView.class.equals(type)) {
                map.put(type, new PadFileEditorViewImpl());
            } else if (PadFileListerView.class.equals(type)) {
                map.put(type, new PadFileListerViewImpl());

            } else if (PadBatchViewerView.class.equals(type)) {
                map.put(type, new PadBatchViewerViewImpl());
            } else if (PadBatchEditorView.class.equals(type)) {
                map.put(type, new PadBatchEditorViewImpl());

            } else if (AdminUserViewerView.class.equals(type)) {
                map.put(type, new AdminUserViewerViewImpl());
            } else if (AdminUserEditorView.class.equals(type)) {
                map.put(type, new AdminUserEditorViewImpl());
            } else if (AdminUserListerView.class.equals(type)) {
                map.put(type, new AdminUserListerViewImpl());

            } else if (VistaTermsViewerView.class.equals(type)) {
                map.put(type, new VistaTermsViewerViewImpl());
            } else if (VistaTermsEditorView.class.equals(type)) {
                map.put(type, new VistaTermsEditorViewImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
