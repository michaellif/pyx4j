/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import static java.util.Arrays.asList;

import org.junit.Ignore;

import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

@Ignore
public class AptUnitOccupancyManagerHelperTest extends AptUnitOccupancyManagerTestBase {

    private AptUnit getUnit() {
        // TODO Auto-generated method stub
        return null;
    }

    public void testMergeTwoSegments() {
        setup().fromTheBeginning().to("2011-12-31").status(Status.available).x();
        setup().from("2011-01-01").toTheEndOfTime().status(Status.available).x();

        now("2011-10-01");

        AptUnitOccupancyManagerHelper.merge(getUnit(), OccupancyFacade.MIN_DATE, asList(Status.available), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                merged.status().setValue(Status.pending);
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });

        expect().fromTheBeginning().toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
    }

    public void testMergeTwoMerges() {
        setup().fromTheBeginning().to("2010-05-01").status(Status.available).x();
        setup().from("2010-05-02").to("2010-12-31").status(Status.pending).x();
        setup().from("2011-01-01").to("2012-12-31").status(Status.pending).x();
        setup().from("2013-01-01").to("2013-05-01").status(Status.available).x();
        setup().from("2013-05-02").to("2013-12-31").status(Status.pending).x();
        setup().from("2014-01-01").to("2014-12-31").status(Status.pending).x();
        setup().from("2015-01-01").toTheEndOfTime().status(Status.available).x();

        now("2011-10-01");

        AptUnitOccupancyManagerHelper.merge(getUnit(), OccupancyFacade.MIN_DATE, asList(Status.pending), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                merged.status().setValue(Status.offMarket);
                merged.offMarket().setValue(OffMarketType.down);
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });

        expect().fromTheBeginning().to("2010-05-01").status(Status.available).x();
        expect().from("2010-05-02").to("2012-12-31").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2013-01-01").to("2013-05-01").status(Status.available).x();
        expect().from("2013-05-02").to("2014-12-31").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2015-01-01").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    public void testMergeAll() {
        setup().fromTheBeginning().to("2010-05-01").status(Status.available).x();
        setup().from("2010-05-02").to("2010-12-31").status(Status.pending).x();
        setup().from("2011-01-01").to("2012-12-31").status(Status.pending).x();
        setup().from("2013-01-01").to("2013-05-01").status(Status.available).x();
        setup().from("2013-05-02").to("2013-12-31").status(Status.pending).x();
        setup().from("2014-01-01").to("2014-12-31").status(Status.pending).x();
        setup().from("2015-01-01").toTheEndOfTime().status(Status.available).x();

        now("2010-04-20");

        AptUnitOccupancyManagerHelper.merge(getUnit(), OccupancyFacade.MIN_DATE, asList(Status.pending, Status.available), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                merged.status().setValue(Status.offMarket);
                merged.offMarket().setValue(OffMarketType.down);
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });

        expect().fromTheBeginning().toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }
}
