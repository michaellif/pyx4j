/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.vewers;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.editors.BuildingEditorForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.Building;

public class BuildingViewerViewImpl extends DockLayoutPanel implements BuildingViewerView {

    private static I18n i18n = I18nFactory.getI18n(BuildingViewerViewImpl.class);

    private final BuildingCrudService service = GWT.create(BuildingCrudService.class);

    private final BuildingEditorForm viewer = new BuildingEditorForm(new CrmViewersComponentFactory());

    public BuildingViewerViewImpl() {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(new CrmSiteMap.Viewers.Building()).getCaption()), 3);

        viewer.initialize();
        add(new ScrollPanel(viewer.asWidget()));
    }

    @Override
    public void setViewingEntityId(long entityId) {
        if (service != null) {
            service.retrieve(new AsyncCallback<Building>() {

                @Override
                public void onSuccess(Building result) {
                    viewer.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }
}
