/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.tools.oapi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.activity.AbstractListerActivity;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.tools.oapi.OapiListerView;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;
import com.propertyvista.operations.rpc.services.OapiCrudService;

public class OapiListerActivity extends AbstractListerActivity<OapiConversionDTO> {//implements OapiListerView.Presenter {

    private static final I18n i18n = I18n.get(OapiListerActivity.class);

    public OapiListerActivity(Place place) {
        super(OapiConversionDTO.class, place, OperationsSite.getViewFactory().getView(OapiListerView.class), GWT
                .<AbstractCrudService<OapiConversionDTO>> create(OapiCrudService.class));
    }

}
