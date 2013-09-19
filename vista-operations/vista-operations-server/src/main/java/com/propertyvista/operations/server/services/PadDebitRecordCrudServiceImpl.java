/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;

public class PadDebitRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<PadDebitRecord, PadDebitRecordDTO> implements PadDebitRecordCrudService {

    public PadDebitRecordCrudServiceImpl() {
        super(PadDebitRecord.class, PadDebitRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }
}
