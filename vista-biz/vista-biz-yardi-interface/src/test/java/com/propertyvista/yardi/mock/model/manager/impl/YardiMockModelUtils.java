/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiFloorplan;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiLeaseCharge;
import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;

public class YardiMockModelUtils {

    // Model manipulations
    static void addBuilding(YardiBuilding building) {
        assert building != null : "building cannot be null";

        YardiMock.server().getModel().getBuildings().add(building);
    }

    // Lookup methods
    public static YardiBuilding findBuilding(String buildingId) {
        assert buildingId != null : "building id cannot be null";

        for (YardiBuilding yb : YardiMock.server().getModel().getBuildings()) {
            if (buildingId.equals(yb.buildingId().getValue())) {
                return yb;
            }
        }
        return null;
    }

    public static YardiFloorplan findFloorplan(YardiBuilding building, String fpId) {
        assert building != null : "building cannot be null";
        assert fpId != null : "floorplan id cannot be null";

        for (YardiFloorplan fp : building.floorplans()) {
            if (fpId.equals(fp.floorplanId().getValue())) {
                return fp;
            }
        }
        return null;
    }

    public static YardiUnit findUnit(YardiBuilding building, String unitId) {
        assert building != null : "building cannot be null";
        assert unitId != null : "unit id cannot be null";

        for (YardiUnit fp : building.units()) {
            if (unitId.equals(fp.unitId().getValue())) {
                return fp;
            }
        }
        return null;
    }

    public static YardiRentableItem findRentableItem(YardiBuilding building, String itemId) {
        assert building != null : "building cannot be null";
        assert itemId != null : "item id cannot be null";

        for (YardiRentableItem fp : building.rentableItems()) {
            if (itemId.equals(fp.itemId().getValue())) {
                return fp;
            }
        }
        return null;
    }

    public static YardiLease findLease(YardiBuilding building, String leaseId) {
        assert building != null : "building cannot be null";
        assert leaseId != null : "lease id cannot be null";

        for (YardiLease yl : building.leases()) {
            if (leaseId.equals(yl.leaseId().getValue())) {
                return yl;
            }
        }
        return null;
    }

    public static YardiTenant findTenant(YardiLease lease, String tenantId) {
        assert lease != null : "lease cannot be null";
        assert tenantId != null : "tenant id cannot be null";

        for (YardiTenant yt : lease.tenants()) {
            if (tenantId.equals(yt.tenantId().getValue())) {
                return yt;
            }
        }
        return null;
    }

    public static YardiLeaseCharge findLeaseCharge(YardiLease lease, String chargeId) {
        assert lease != null : "lease cannot be null";
        assert chargeId != null : "charge id cannot be null";

        for (YardiLeaseCharge ylc : lease.charges()) {
            if (chargeId.equals(ylc.chargeId().getValue())) {
                return ylc;
            }
        }
        return null;
    }

    // Conversions
    public static BigDecimal toAmount(String amount) {
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_DOWN);
    }

    public static Date toDate(String date) {
        return DateUtils.detectDateformat(date);
    }

    public static LogicalDate toLogicalDate(String date) {
        return new LogicalDate(toDate(date));
    }

    public static String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String format(BigDecimal amount) {
        return amount.toPlainString();
    }
}
