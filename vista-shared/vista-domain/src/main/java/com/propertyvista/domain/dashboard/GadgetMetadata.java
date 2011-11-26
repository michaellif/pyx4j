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

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.gadgets.AbstractGadgetSettings;

public interface GadgetMetadata extends ISharedUserEntity {

    @I18n
    @I18nComment("Gadget Type")
    public static enum GadgetType {
        Test,

        Demo,

        BuildingLister,

        BarChartDisplay,

        PieChartDisplay,

        LineChartDisplay,

        BarChartDisplayBuilding,

        PieChartDisplayBuilding,

        GaugeDisplay,

        DeprecatedUnitAvailabilityReport,

        DeprecatedAvailabilitySummary,

        DeprecatedTurnoverAnalysisGraph,

        UnitAvailabilityReport,

        AvailabilitySummary,

        TurnoverAnalysisGraph,

        RentArrearsGadget,

        ParkingArrearsGadget,

        OtherArrearsGadget,

        TotalArrearsGadget,

        ArrearsARBalanceComparisonChart,

        ArrearsSummaryGadget;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @MemberColumn(name = "gadgetType")
    IPrimitive<GadgetType> type();

    IPrimitive<String> name();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    // Save gadgets Settings separately
    @EmbeddedEntity
    AbstractGadgetSettings settings();

    /*
     * Dashboard: 0, 1, 2;
     * Report : -1, 0, 1;
     */
    @MemberColumn(name = "gadgetColumn")
    IPrimitive<Integer> column();
}
