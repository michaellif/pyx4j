/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.dbp;

import com.propertyvista.operations.client.ui.crud.OperationsEditorViewImplBase;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;

public class DirectDebitSimRecordEditorViewImpl extends OperationsEditorViewImplBase<DirectDebitSimRecord> implements DirectDebitSimRecordEditorView {

    public DirectDebitSimRecordEditorViewImpl() {
        setForm(new DirectDebitSimRecordForm(this));
    }

}
