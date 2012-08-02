/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.io.Serializable;
import java.util.Vector;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

public class AvailabilityReportDataDTO implements Serializable {

    private static final long serialVersionUID = -4635623219109285728L;

    public Vector<UnitAvailabilityStatus> unitStatuses;

}
