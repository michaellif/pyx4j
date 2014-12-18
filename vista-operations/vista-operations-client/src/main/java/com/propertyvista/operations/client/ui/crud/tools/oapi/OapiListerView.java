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
 */
package com.propertyvista.operations.client.ui.crud.tools.oapi;

import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeListerView;

import com.propertyvista.operations.rpc.dto.OapiConversionDTO;

public interface OapiListerView extends IPrimeListerView<OapiConversionDTO> {

    interface Presenter extends IPrimeListerView.IPrimeListerPresenter<OapiConversionDTO> {

    }

}
