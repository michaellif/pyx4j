/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components.details;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import com.pyx4j.entity.shared.Path;

import com.propertyvista.domain.property.asset.building.Building;

public class CounterGadgetFilter implements Serializable {

    private static final long serialVersionUID = -7785010850030327508L;

    private final Vector<Building> buildings;

    private final Path counterMember;

    public CounterGadgetFilter() {
        this.buildings = new Vector<Building>();
        this.counterMember = new Path("/");
    }

    public CounterGadgetFilter(Collection<Building> buildings, Path counterMember) {
        this.buildings = new Vector<Building>(buildings);
        this.counterMember = counterMember;
    }

    public Vector<Building> getBuildings() {
        return new Vector<Building>(buildings);
    }

    public Path getCounterMember() {
        return counterMember;
    }

}
