/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.account;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.organisation.employee.LoginAttemptsListerView;
import com.propertyvista.crm.rpc.dto.account.LoginAttemptDTO;
import com.propertyvista.crm.rpc.services.security.CrmLoginAttemptsListerService;

public class LoginAttemptsListerActivity extends AbstractListerActivity<LoginAttemptDTO> {

    private Key userKey;

    public LoginAttemptsListerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(LoginAttemptsListerView.class), GWT
                .<CrmLoginAttemptsListerService> create(CrmLoginAttemptsListerService.class), LoginAttemptDTO.class);

        String val;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            userKey = new Key(val);
            // Validate argument
            try {
                userKey.asLong();
            } catch (NumberFormatException e) {
                userKey = null;
            }

        }
    }

    @Override
    public void populate() {
        if (userKey != null) {
            clearPreDefinedFilters();
            setPreDefinedFilters(Arrays.<Criterion> asList(PropertyCriterion.eq(EntityFactory.getEntityPrototype(LoginAttemptDTO.class).userKey(), userKey)));
        }
        super.populate();
    }

}
