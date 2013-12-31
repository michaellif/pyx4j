/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.model.PadProcessorInformation.PadProcessingStatus;

@Transient
public interface PadEFTReportModel extends PadFileModel {

    IPrimitive<BigDecimal> calulatedEftAmount();

    IPrimitive<BigDecimal> eftAmount();

    IPrimitive<BigDecimal> eftAmountDelta();

    IPrimitive<String> amountStored();

    @Format("#,##0.00")
    IPrimitive<Double> percentStored();

    IPrimitive<Boolean> invalid();

    IPrimitive<PadProcessingStatus> status();

    IPrimitive<String> message();

}
