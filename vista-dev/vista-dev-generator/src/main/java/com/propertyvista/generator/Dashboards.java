/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 13, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.generator;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public class Dashboards {

    public final List<DashboardMetadata> systemDashboards = new ArrayList<DashboardMetadata>();

    public final List<DashboardMetadata> buildingDashboards = new ArrayList<DashboardMetadata>();
}
