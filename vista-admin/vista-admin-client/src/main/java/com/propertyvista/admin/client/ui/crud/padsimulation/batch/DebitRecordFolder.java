/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.padsimulation.batch;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.admin.domain.payment.pad.sim.PadSimDebitRecord;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;

public class DebitRecordFolder extends VistaBoxFolder<PadSimDebitRecord> {

    public DebitRecordFolder(boolean modifyable) {
        super(PadSimDebitRecord.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PadSimDebitRecord) {
            return new DebitRecordEditor();
        }
        return super.create(member);
    }
}