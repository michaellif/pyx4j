/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.List;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.widgets.client.dashboard.IBoard;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;

public interface ILayoutManager {

    interface Resources {

        ImageResource layoutIcon();

        ImageResource layoutIconSelected();

    }

    Resources getResources();

    String getLayoutName();

    /**
     * returns true if provided dashboard metadata holds information that is required for this layout manager
     */
    boolean canHandle(String encodedLayout);

    /** places gadgets in the board in the positions defined by layout data from dashboard metadata */
    IBoard arrange(String encodedLayout, List<IGadgetInstance> gadgtes);

    String encodeLayout(IBoard board);
}
