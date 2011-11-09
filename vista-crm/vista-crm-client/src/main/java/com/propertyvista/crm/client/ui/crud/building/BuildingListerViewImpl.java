/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CHyperlink;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.UpdateUploadDialog;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerViewImpl extends CrmListerViewImplBase<BuildingDTO> implements BuildingListerView {

    private final CHyperlink upload;

    public BuildingListerViewImpl() {
        super(CrmSiteMap.Properties.Building.class);
        setLister(new BuildingLister());

        upload = new CHyperlink(new Command() {
            @Override
            public void execute() {
                UpdateUploadDialog.show();
            }
        });
        upload.setValue(i18n.tr("Upload update.xml"));
        lister.addActionButton(upload.asWidget());
    }

    @Override
    public void populate(List<BuildingDTO> entityes, int pageNumber, boolean hasMoreData, int totalRows) {
        super.populate(entityes, pageNumber, hasMoreData, totalRows);
        upload.setEnabled(true);
    }
}
