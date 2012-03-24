/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy.mk2;

import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.addDay;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.assertStatus;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.merge;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancySegment;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.split;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.substractDay;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;
import com.propertyvista.server.common.util.occupancy.AvailabilityReportManager;
import com.propertyvista.server.common.util.occupancy.SplittingHandler;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ApproveLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelEndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelReservationConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.EndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ReserveConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeAvailableConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeOffMarketConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeRenovationConstraintsDTO;

// TODO add `now` date for all the functions
public class UnitOccupancyManager implements IUnitOccupancyManager {

    public static IUnitOccupancyManager exec() {
        return new UnitOccupancyManager();
    }

    @Override
    public void scopeAvailable(Key unitId, LogicalDate availableFrom) {
        ScopeAvailableConstraintsDTO constraints = getScopeAvailableConstrants(unitId);
        if (constraints.minAvaiableFrom().isNull() || constraints.minAvaiableFrom().getValue().after(availableFrom)) {
            throw new IllegalArgumentException("availableFrom is greater than minimum value");
        }

        AptUnitOccupancyManagerHelper.split(unitId, availableFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });

        updateUnitAvailableFrom(unitId, availableFrom);
        AvailabilityReportManager.generateUnitAvailability(unitId, availableFrom);
    }

    @Override
    public ScopeAvailableConstraintsDTO getScopeAvailableConstrants(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.vacant));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        ScopeAvailableConstraintsDTO constraints = EntityFactory.create(ScopeAvailableConstraintsDTO.class);
        if (segment != null) {
            constraints.minAvaiableFrom().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void scopeOffMarket(Key unitId, LogicalDate offMarketFrom, final OffMarketType type) {

        ScopeOffMarketConstraintsDTO constraints = getScopeOffMarketConstrants(unitId);
        if (constraints.minOffMarketFrom().isNull() || constraints.minOffMarketFrom().getValue().after(offMarketFrom)) {
            throw new IllegalArgumentException("offMarketFrom is greater than minimum value");
        }

        AptUnitOccupancyManagerHelper.split(unitId, offMarketFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.offMarket);
                segment.offMarket().setValue(type);
            }
        });

        updateUnitAvailableFrom(unitId, offMarketFrom);
    }

    @Override
    public ScopeOffMarketConstraintsDTO getScopeOffMarketConstrants(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.vacant));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        ScopeOffMarketConstraintsDTO constraints = EntityFactory.create(ScopeOffMarketConstraintsDTO.class);
        if (segment != null) {
            constraints.minOffMarketFrom().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void scopeRenovation(Key unitId, LogicalDate renovationStart, LogicalDate renovationEnd) {
        ScopeRenovationConstraintsDTO constraints = getScopeRenovationConstraints(unitId);
        if ((constraints.minRenovationStart().isNull() || constraints.minRenovationStart().getValue().after(renovationStart))
                | (constraints.minRenovationEnd().isNull() || constraints.minRenovationEnd().getValue().after(renovationEnd))
                | (renovationStart.after(renovationEnd))) {
            throw new IllegalArgumentException("operation arguments don't match the constraints");
        }

        AptUnitOccupancyManagerHelper.split(unitId, renovationStart, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.renovation);
            }
        });
        AptUnitOccupancyManagerHelper.split(unitId, addDay(renovationEnd), new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });

    }

    @Override
    public ScopeRenovationConstraintsDTO getScopeRenovationConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.vacant));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        ScopeRenovationConstraintsDTO constraints = EntityFactory.create(ScopeRenovationConstraintsDTO.class);
        if (segment != null) {
            constraints.minRenovationStart().setValue(segment.dateFrom().getValue());
            constraints.minRenovationEnd().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void makeVacant(Key unitId, LogicalDate vacantFrom) {
        if (vacantFrom == null) {
            throw new IllegalArgumentException("vacantFrom must not be null");
        }
        MakeVacantConstraintsDTO constraints = getMakeVacantConstraints(unitId);
        LogicalDate min = constraints.minVacantFrom().getValue();
        LogicalDate max = constraints.maxVacantFrom().getValue();
        if (//@formatter:off
             constraints == null
                 || !((vacantFrom.after(min) | vacantFrom.equals(min))                             
                         & (max == null || (vacantFrom.before(max) | vacantFrom.equals(max))))) { //@formatter:on
            throw new IllegalArgumentException(SimpleMessageFormat.format("vacantFrom {0} doesn't match the constraints", vacantFrom));
        }

        AptUnitOccupancySegment makeVacantStartSegment = retrieveOccupancySegment(unitId, vacantFrom);
        AptUnitOccupancySegment vacantSegment = split(makeVacantStartSegment, vacantFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.vacant);
                segment.offMarket().setValue(null);
                segment.dateTo().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
            }
        });

        // now remove the rest
        EntityQueryCriteria<AptUnitOccupancySegment> deleteCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        deleteCriteria.add(PropertyCriterion.eq(deleteCriteria.proto().unit(), unitId));
        deleteCriteria.add(PropertyCriterion.ge(deleteCriteria.proto().dateTo(), vacantFrom));
        deleteCriteria.add(PropertyCriterion.ne(deleteCriteria.proto().id(), vacantSegment.id().getValue()));
        Persistence.service().delete(deleteCriteria);

        merge(unitId, substractDay(vacantFrom), Arrays.asList(Status.vacant), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });

        clearUnitAvailableFrom(unitId);
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.desc(criteria.proto().dateFrom());

        List<AptUnitOccupancySegment> occupancy = Persistence.secureQuery(criteria);

        LogicalDate minVacantFromCandidate = null;
        LogicalDate maxVacantFromCandidate = null;
        boolean readCluster = false;
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.leased | segment.status().getValue() == Status.reserved) {
                break;
            } else {
                if (!readCluster) {
                    if (segment.status().getValue() != Status.vacant) {
                        readCluster = true;
                        if (segment.status().getValue() == Status.available) {
                            maxVacantFromCandidate = segment.dateFrom().getValue();
                        } else {
                            maxVacantFromCandidate = segment.dateTo().getValue();
                        }
                        minVacantFromCandidate = segment.dateFrom().getValue();

                    }
                } else {
                    if (segment.status().getValue() != Status.vacant) {
                        minVacantFromCandidate = segment.dateFrom().getValue();
                    } else {
                        break;
                    }
                }
            }
        }

        MakeVacantConstraintsDTO constraints = EntityFactory.create(MakeVacantConstraintsDTO.class);
        constraints.minVacantFrom().setValue(minVacantFromCandidate);
        if (!EqualsHelper.equals(maxVacantFromCandidate, UnitOccupancyConstants.MAX_DATE)) {
            constraints.maxVacantFrom().setValue(maxVacantFromCandidate);
        }

        return constraints;
    }

    @Override
    public void reserve(Key unitId, LogicalDate reserveFrom, final Lease lease) {
        ReserveConstraintsDTO constraints = getReserveConstraints(unitId);
        if (constraints.minReserveFrom().isNull()) {
            throw new IllegalStateException("reserve operation is not applicable");
        }
        if (constraints.minReserveFrom().getValue().after(reserveFrom)) {
            throw new IllegalArgumentException("reserveFrom argument doesn't match the constraints");
        }
        split(unitId, reserveFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                assertStatus(segment, Status.available);
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.reserved);
                segment.lease().set(lease);
            }
        });
        clearUnitAvailableFrom(unitId);
    }

    @Override
    public ReserveConstraintsDTO getReserveConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.available));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        ReserveConstraintsDTO constraints = EntityFactory.create(ReserveConstraintsDTO.class);
        if (segment != null) {
            constraints.minReserveFrom().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void cancelReservation(Key unitId, LogicalDate cancelFrom) {
        CancelReservationConstraintsDTO constraints = getCancelReservationConstraints(unitId);
        if (constraints.minCancelFrom().isNull()) {
            throw new IllegalStateException("cancel reservation operation is not applicable for the current occupancy");
        }
        if (constraints.minCancelFrom().getValue().after(cancelFrom)) {
            throw new IllegalArgumentException("cancelFrom date is not copmatible with current constraints");
        }
        // leave "reserved" part in the past (if past reserved part is present)
        split(unitId, cancelFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
                segment.lease().setValue(null);
            }
        });
        merge(unitId, substractDay(cancelFrom), Arrays.asList(Status.available), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });

        updateUnitAvailableFrom(unitId, cancelFrom);
    }

    @Override
    public CancelReservationConstraintsDTO getCancelReservationConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.reserved));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        CancelReservationConstraintsDTO constraints = EntityFactory.create(CancelReservationConstraintsDTO.class);
        if (segment != null) {
            constraints.minCancelFrom().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void approveLease(Key unitId, LogicalDate leaseFrom) {
        ApproveLeaseConstraintsDTO constraints = getApproveLeaseConstraints(unitId);
        if (constraints.minLeaseFrom().isNull()) {
            throw new IllegalStateException("approve lease operation is not applicable in current occupancy state");
        }
        if (constraints.minLeaseFrom().getValue().after(leaseFrom)) {
            throw new IllegalArgumentException("leaseFrom parameter doesn't match the constraints");
        }

        split(unitId, leaseFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.leased);
            }
        });

    }

    @Override
    public ApproveLeaseConstraintsDTO getApproveLeaseConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.reserved));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        ApproveLeaseConstraintsDTO constraints = EntityFactory.create(ApproveLeaseConstraintsDTO.class);
        if (segment != null) {
            constraints.minLeaseFrom().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void endLease(Key unitId, LogicalDate leaseEnd) {
        EndLeaseConstraintsDTO constraints = getEndLeaseConstraints(unitId);
        if (constraints.minLeaseEnd().isNull()) {
            throw new IllegalStateException("end lease operation is not applicable for current occupancy");
        }
        if (constraints.minLeaseEnd().getValue().after(leaseEnd)) {
            throw new IllegalArgumentException("leaseEnd parameter doens't match the constraints");
        }

        split(unitId, addDay(leaseEnd), new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.vacant);
                segment.lease().setValue(null);
            }
        });
    }

    @Override
    public EndLeaseConstraintsDTO getEndLeaseConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.eq(criteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.leased));
        AptUnitOccupancySegment segment = Persistence.secureRetrieve(criteria);

        EndLeaseConstraintsDTO constraints = EntityFactory.create(EndLeaseConstraintsDTO.class);
        if (segment != null) {
            constraints.minLeaseEnd().setValue(segment.dateFrom().getValue());
        }
        return constraints;
    }

    @Override
    public void cancelEndLease(Key unitId) {
        CancelEndLeaseConstraintsDTO constraints = getCancelEndLeaseConstraints(unitId);
        if (!constraints.isCancellable().isBooleanTrue()) {
            throw new IllegalStateException("cancel end lease operation is not applicable in current occupancy state");
        }

        EntityQueryCriteria<AptUnitOccupancySegment> lastSegmentCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        lastSegmentCriteria.add(PropertyCriterion.eq(lastSegmentCriteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        AptUnitOccupancySegment lastSegment = Persistence.secureRetrieve(lastSegmentCriteria);
        Persistence.service().delete(lastSegment);

        EntityQueryCriteria<AptUnitOccupancySegment> leasedSegmentCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        leasedSegmentCriteria.add(PropertyCriterion.eq(leasedSegmentCriteria.proto().dateTo(), substractDay(lastSegment.dateFrom().getValue())));
        AptUnitOccupancySegment leasedSegment = Persistence.secureRetrieve(leasedSegmentCriteria);
        leasedSegment.dateTo().setValue(UnitOccupancyConstants.MAX_DATE);
        Persistence.secureSave(leasedSegment);

        clearUnitAvailableFrom(unitId);
    }

    @Override
    public CancelEndLeaseConstraintsDTO getCancelEndLeaseConstraints(Key unitId) {
        EntityQueryCriteria<AptUnitOccupancySegment> lastSegCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        lastSegCriteria.add(PropertyCriterion.eq(lastSegCriteria.proto().dateTo(), UnitOccupancyConstants.MAX_DATE));
        lastSegCriteria.or().left(PropertyCriterion.eq(lastSegCriteria.proto().status(), Status.vacant))
                .right(PropertyCriterion.eq(lastSegCriteria.proto().status(), Status.available));
        AptUnitOccupancySegment lastSeg = Persistence.secureRetrieve(lastSegCriteria);
        CancelEndLeaseConstraintsDTO constraints = EntityFactory.create(CancelEndLeaseConstraintsDTO.class);
        if (lastSeg != null) {
            EntityQueryCriteria<AptUnitOccupancySegment> leasedSegmentCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
            leasedSegmentCriteria.add(PropertyCriterion.eq(leasedSegmentCriteria.proto().dateTo(), substractDay(lastSeg.dateFrom().getValue())));
            leasedSegmentCriteria.add(PropertyCriterion.eq(leasedSegmentCriteria.proto().status(), Status.leased));
            AptUnitOccupancySegment leasedSegment = Persistence.secureRetrieve(leasedSegmentCriteria);
            if (leasedSegment != null) {
                constraints.isCancellable().setValue(true);
            }
        }
        return constraints;
    }

    private void updateUnitAvailableFrom(Key unitId, LogicalDate newAvaialbleFrom) {
        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId);
        unit._availableForRent().setValue(newAvaialbleFrom);
        Persistence.secureSave(unit);
    }

    private void clearUnitAvailableFrom(Key unitId) {
        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId);
        unit._availableForRent().setValue(null);
        Persistence.secureSave(unit);
    }
}
