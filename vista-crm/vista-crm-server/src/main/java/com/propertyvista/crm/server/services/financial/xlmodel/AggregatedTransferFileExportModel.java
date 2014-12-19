/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2014
 * @author ernestog
 */
package com.propertyvista.crm.server.services.financial.xlmodel;

import com.pyx4j.entity.annotations.Transient;

@Transient
public interface AggregatedTransferFileExportModel extends PaymentRecordModel, CardsAggregatedTransferModel, EftAggregatedTransferModel,
        AggregatedTransferModel {

}
