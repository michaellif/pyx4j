/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulateddatapreload;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.IsView;

import com.propertyvista.operations.client.activity.crud.simulateddatapreload.SimulatedDataPreloadActivity;

public interface SimulatedDataPreloadView extends IsWidget, IsView {


    void setPresenter(SimulatedDataPreloadActivity activity);

    void updateArrearsHistoryGenerationProgress(int current, int total);
}
