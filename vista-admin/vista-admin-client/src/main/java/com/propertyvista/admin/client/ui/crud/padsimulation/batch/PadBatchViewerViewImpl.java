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
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.client.ui.crud.padsimulation.PadFileEditorViewImpl;
import com.propertyvista.admin.domain.payment.pad.sim.PadSimBatch;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class PadBatchViewerViewImpl extends AdminViewerViewImplBase<PadSimBatch> implements PadBatchViewerView {

    private static final I18n i18n = I18n.get(PadFileEditorViewImpl.class);

    public PadBatchViewerViewImpl() {
        super(AdminSiteMap.Administration.PadSimulation.PadSimBatch.class, new PadBatchForm(true));
    }
}