/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 */
package com.propertyvista.biz.legal.eviction;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4Batch;

public interface N4ManagementFacade {

    public static final String N4_REPORT_SECTION = "N4";

    void issueN4(N4Batch batch, ExecutionMonitor monitor) throws IllegalStateException, FormFillError;

    void issueN4(EvictionCase evictionCase, ExecutionMonitor monitor) throws IllegalStateException, FormFillError;

    void autoCancelN4(ExecutionMonitor monitor);
}
