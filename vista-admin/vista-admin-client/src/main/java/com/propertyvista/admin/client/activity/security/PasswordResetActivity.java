/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.security;

import com.google.gwt.place.shared.Place;

import com.propertyvista.admin.client.viewfactories.AdminVeiwFactory;
import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;

// TODO Admin Site: do we need Password Reset Feature???
public class PasswordResetActivity extends AbstractPasswordResetActivity {

    public PasswordResetActivity(Place place) {
        super(place, AdminVeiwFactory.instance(PasswordResetView.class), null);
    }

}
