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

import com.google.gwt.user.client.Command;

import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;

public class OapiViewerViewImpl extends OperationsViewerViewImplBase<OapiConversionDTO> implements OapiViewerView {

    private final Button btnDownloadXml;

    public OapiViewerViewImpl() {

        setForm(new OapiForm(this));

        btnDownloadXml = new Button("Download XML", new Command() {
            @Override
            public void execute() {
                ((OapiViewerView.Presenter) getPresenter()).downloadXMLFile();
            }
        });
        addHeaderToolbarItem(btnDownloadXml.asWidget());

    }
}
