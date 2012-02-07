/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.Key;

/**
 * Gadget instance that is supported by building gadget.
 */
// TODO add marker interface for gadget metadata, and thing how to bind between metadata building interface and this interface as painless as possible
public interface IBuildingBoardGadgetInstance extends IGadgetInstance {

    void setBuildingsSource(BuildingsSource source);

    interface BuildingsSource {

        List<Key> getBuildings();

    }

    @Deprecated
    void setFiltering(FilterData filterData);

    @Deprecated
    class FilterData {
        /**
         * Process all buildings if list is empty.
         */
        public final List<Key> buildings = new ArrayList<Key>();

        /**
         * Use all past records if null.
         */
        public Date fromDate = null;

        /**
         * Use all current/future records if null.
         */
        public Date toDate = null;

        // Construction:

        public FilterData() {
        }

        public FilterData(Key buildingId) {
            this(buildingId, null, null);
        }

        public FilterData(Date from, Date to) {
            this(null, from, to);
        }

        public FilterData(Key buildingId, Date from, Date to) {
            if (buildingId != null) {
                buildings.add(buildingId);
            }
            fromDate = from;
            toDate = to;
        }
    }
}