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
import com.propertyvista.crm.client.ui.editors.UnitEditorForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.domain.property.asset.AptUnit;

public class UnitViewerViewImpl extends DockLayoutPanel implements UnitViewerView {

    private static I18n i18n = I18nFactory.getI18n(UnitViewerViewImpl.class);

    private final UnitCrudService service = GWT.create(UnitCrudService.class);

    private final UnitEditorForm viewer = new UnitEditorForm(new CrmViewersComponentFactory());

    public UnitViewerViewImpl() {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(new CrmSiteMap.Viewers.Unit()).getCaption()), 3);

        viewer.initialize();
        add(new ScrollPanel(viewer.asWidget()));
    }

    @Override
    public void setViewingEntityId(long entityId) {
        if (service != null) {
            service.retrieve(new AsyncCallback<AptUnit>() {

                @Override
                public void onSuccess(AptUnit result) {
                    viewer.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }
}
