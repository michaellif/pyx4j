/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.maintenance.MaintenanceRequestPriority.PriorityLevel;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestStatusDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class MaintenanceRequestFolderItem extends CEntityForm<MaintenanceRequestStatusDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestFolderItem.class);

    private Anchor detailsLink;

    public MaintenanceRequestFolderItem() {
        super(MaintenanceRequestStatusDTO.class);

        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().subject(), new CLabel<String>()), 180).build());
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description(), new CLabel<String>()), 250).build());
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().status().phase(), new CLabel<StatusPhase>()), 180).build());
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().priority().level(), new CLabel<PriorityLevel>()), 180).build());
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().lastUpdated(), new CLabel<String>()), 180).build());

        detailsLink = new Anchor(i18n.tr("View Details"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Maintenance.MaintenanceRequestPage().formPlace(getValue().getPrimaryKey()));
            }
        });
        detailsLink.getElement().getStyle().setMarginTop(10, Unit.PX);

        content.setWidget(++row, 0, detailsLink);

        return content;
    }

}