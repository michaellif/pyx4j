/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class BuildingEditorViewImpl extends DockLayoutPanel implements BuildingEditorView {

    private static I18n i18n = I18nFactory.getI18n(BuildingEditorViewImpl.class);

    public BuildingEditorViewImpl() {
        super(Unit.EM);
        setSize("100%", "100%");
        addNorth(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(new CrmSiteMap.Editor.Building()).getCaption()), 3);

        BuildingEditorForm editor = new BuildingEditorForm();
        ScrollPanel scroll = new ScrollPanel(editor.asWidget());

        add(scroll);
    }
}
