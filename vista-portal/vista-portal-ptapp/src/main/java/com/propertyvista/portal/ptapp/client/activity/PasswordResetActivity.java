/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.PtPasswordResetService;

public class PasswordResetActivity extends AbstractPasswordResetActivity {

    public PasswordResetActivity(Place place) {
        super(place, PtAppViewFactory.instance(PasswordResetView.class), GWT.<PtPasswordResetService> create(PtPasswordResetService.class),
                new PtSiteMap.Login());
    }
}
