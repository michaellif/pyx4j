/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 9, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.List;

import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

/**
 * Basically this is used to wrap another Gadget in order to have object with different address so it could be used as a duplicate in collections.
 * 
 * @author ArtyomB
 */
public class GadgetCloner implements IGadgetFactory {
    IGadgetFactory other;

    private GadgetCloner(IGadgetFactory other) {
        this.other = other;
    }

    @Override
    public List<String> getCategories() {
        return other.getCategories();
    }

    @Override
    public String getDescription() {
        return other.getDescription();
    }

    @Override
    public boolean isBuildingGadget() {
        return other.isBuildingGadget();
    }

    @Override
    public IGadgetInstanceBase createGadget(GadgetMetadata metadata) throws Error {
        return other.createGadget(metadata);
    }

    @Override
    public String getType() {
        return other.getType();
    }

    @Override
    public String getName() {
        return other.getName();
    }

    @Override
    public boolean isAcceptedBy(DashboardType dashboardType) {
        return other.isAcceptedBy(dashboardType);
    }

    public static IGadgetFactory clone(IGadgetFactory other) {
        return new GadgetCloner(other);
    }
}
