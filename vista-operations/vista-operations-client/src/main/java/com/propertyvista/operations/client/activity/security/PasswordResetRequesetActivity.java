/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.security;

import com.google.gwt.place.shared.Place;

import com.propertyvista.common.client.ui.components.login.AbstractPasswordResetRequestActivity;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class PasswordResetRequesetActivity extends AbstractPasswordResetRequestActivity implements PasswordResetRequestView.PasswordResetRequestPresenter {

    public PasswordResetRequesetActivity(Place place) {
        super(place, OperationsSite.getViewFactory().getView(PasswordResetRequestView.class), new OperationsSiteMap.Login());
    }

}
