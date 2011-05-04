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
package com.propertyvista.crm.client.ui.lister;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class BuildingListerViewImpl extends SimplePanel implements BuildingListerView {

    private static I18n i18n = I18nFactory.getI18n(BuildingListerViewImpl.class);

    public BuildingListerViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new CrmHeaderDecorator(AppSite.getHistoryMapper().getPlaceInfo(new CrmSiteMap.Properties.Buildings()).getCaption()));

        BuildingLister lister = new BuildingLister();
        ScrollPanel scroll = new ScrollPanel(lister);
        scroll.getElement().getStyle().setPosition(Position.ABSOLUTE);
        scroll.getElement().getStyle().setTop(45, Unit.PX);
        scroll.getElement().getStyle().setLeft(0, Unit.PX);
        scroll.getElement().getStyle().setRight(0, Unit.PX);
        scroll.getElement().getStyle().setBottom(0, Unit.PX);
        main.add(scroll);
        lister.populateData(0);

        main.setSize("100%", "100%");
        setWidget(main);
    }
}
