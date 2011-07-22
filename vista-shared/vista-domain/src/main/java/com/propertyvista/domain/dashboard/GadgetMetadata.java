/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-22
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.dashboard;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.ISharedUserEntity;

public interface GadgetMetadata extends ISharedUserEntity {

    public static enum GadgetType {
        Test,

        Demo,

        BuildingLister,

        BarChartDisplay,

        PieChartDisplay,

        LineChartDisplay,

        BarChartDisplayBuilding,

        PieChartDisplayBuilding
    }

    @MemberColumn(name = "gadgetType")
    IPrimitive<GadgetType> type();

    IPrimitive<String> name();

    IPrimitive<String> description();

    //This is temporary solution for multiple settings types, Managed on back-end
    @RpcTransient
    IPrimitive<String> settingsClass();

    // Save gadgets Settings separately
    @Transient
    AbstractGadgetSettings settings();

    /*
     * Dashboard: 0, 1, 2;
     * Report : -1, 0, 1;
     */
    @MemberColumn(name = "gadgetColumn")
    IPrimitive<Integer> column();
}
