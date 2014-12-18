/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 22, 2012
 * @author VladL
 */
package com.propertyvista.crm.client.visor.maintenance;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.visor.AbstractVisorPaneView;

import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestVisorView extends AbstractVisorPaneView {

    private static final I18n i18n = I18n.get(MaintenanceRequestVisorView.class);

    private final MaintenanceRequestLister lister;

    public MaintenanceRequestVisorView(MaintenanceRequestVisorController controller) {
        super(controller);
        this.lister = new MaintenanceRequestLister(this);

        // UI:
        setCaption(i18n.tr("Maintenance Requests"));
        setContentPane(new ScrollPanel(lister.asWidget()));
        getElement().getStyle().setProperty("padding", "6px");
    }

    public void populate() {
        lister.getDataSource().setParentEntityId(getBuildingId());
        if (getTenantId() != null) {
            lister.getDataSource().addPreDefinedFilter(
                    PropertyCriterion.eq(EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class).reporter().id(), getTenantId()));
        } else if (getUnitId() != null) {
            lister.getDataSource().addPreDefinedFilter(
                    PropertyCriterion.eq(EntityFactory.getEntityPrototype(MaintenanceRequestDTO.class).unit().id(), getUnitId()));
            lister.setAddNewActionEnabled(((MaintenanceRequestVisorController) getController()).isNewActionEnabled());
        }
        lister.populate();
    }

    public Key getBuildingId() {
        return ((MaintenanceRequestVisorController) getController()).getBuildingId();
    }

    public Key getTenantId() {
        return ((MaintenanceRequestVisorController) getController()).getTenantId();
    }

    public Key getUnitId() {
        return ((MaintenanceRequestVisorController) getController()).getUnitId();
    }
}
