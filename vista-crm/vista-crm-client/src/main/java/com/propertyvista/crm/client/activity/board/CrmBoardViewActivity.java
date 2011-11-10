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
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.board;

import com.google.gwt.place.shared.Place;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public abstract class CrmBoardViewActivity<V extends BoardView> extends BoardViewActivity<V> {

    public CrmBoardViewActivity(V view, Place place) {
        super(view, place);

    }

    @Override
    public void onPopulateSuccess(DashboardMetadata result) {
        super.onPopulateSuccess(result);
    }
}