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
package com.propertyvista.operations.client.ui.crud.simulator.pad.batch;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;

public class PadSimBatchViewerViewImpl extends OperationsViewerViewImplBase<PadSimBatch> implements PadSimBatchViewerView {

    public PadSimBatchViewerViewImpl() {
        setForm(new PadSimBatchForm(this));
    }
}