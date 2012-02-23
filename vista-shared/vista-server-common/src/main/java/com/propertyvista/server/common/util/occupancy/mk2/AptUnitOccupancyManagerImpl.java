/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy.mk2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.occupancy.DefineSourceOccupancyStatePrefix;
import com.propertyvista.domain.occupancy.IAptUnitOccupancyManager;
import com.propertyvista.domain.occupancy.IAptUnitOccupancyOperation;
import com.propertyvista.domain.occupancy.UnitStatus;
import com.propertyvista.domain.occupancy.operations.OpScopeAvailable;
import com.propertyvista.domain.occupancy.operations.OpScopeOffMarket;
import com.propertyvista.domain.occupancy.operations.OpScopeRenovation;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;

public class AptUnitOccupancyManagerImpl implements IAptUnitOccupancyManager {

    @Override
    public Vector<IAptUnitOccupancyOperation> getAvailableOperations(final Key unitPk, final LogicalDate when) {

        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitPk));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), when));
        criteria.asc(criteria.proto().dateFrom());

        List<AptUnitOccupancySegment> state = Persistence.secureQuery(criteria);

        Vector<IAptUnitOccupancyOperation> available = new Vector<IAptUnitOccupancyOperation>();

        for (StatePrefixToOperationMatcher<?> m : getOperationMatchers()) {
            IAptUnitOccupancyOperation op = m.instance(when, state);
            if (op != null) {
                available.add(op);
            }
        }

        return available;
    }

    @Override
    public void apply(IAptUnitOccupancyOperation op) {
        // TODO validate if the old state is the state that can be replaced by the new one
        if (op.isConfigured()) {

            AptUnit unit = op.oldState().get(0).unit();
            AptUnitOccupancySegment oldfirstSeg = op.oldState().get(0);
            EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

            criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
            criteria.add(PropertyCriterion.ge(criteria.proto().dateFrom(), oldfirstSeg));
            criteria.asc(criteria.proto().dateFrom());

            Iterator<AptUnitOccupancySegment> segments = Persistence.secureQuery(criteria).iterator();
            if (segments.hasNext()) {
                // chop the first segment into pieces                
                AptUnitOccupancySegment firstSegment = segments.next();
                if (firstSegment.dateFrom().getValue().before(op.getApplyStartDate())) {
                    firstSegment.dateTo().setValue(AptUnitOccupancyManagerHelper.addDay(op.getApplyStartDate()));
                    Persistence.secureSave(firstSegment);
                }
                EntityQueryCriteria<AptUnitOccupancySegment> deleteOld = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
                criteria.add(PropertyCriterion.ge(criteria.proto().dateFrom(), op.getApplyStartDate()));
                Persistence.service().delete(criteria);

                for (AptUnitOccupancySegment segment : op.newState()) {
                    Persistence.secureSave(segment);
                }

            } else {
                throw new IllegalStateException("occupancy operation failed: no old segments were found using the given operation's criteria");
            }
        } else {
            throw new IllegalArgumentException("the provided operation must is not configured");
        }

    }

    // TODO maybe pass this in a constructor as a weird kind of dep. injection ?    
    private static List<StatePrefixToOperationMatcher<? extends IAptUnitOccupancyOperation>> getOperationMatchers() {

        ArrayList<StatePrefixToOperationMatcher<? extends IAptUnitOccupancyOperation>> operationMatchers = new ArrayList<StatePrefixToOperationMatcher<? extends IAptUnitOccupancyOperation>>();

        operationMatchers.add(new StatePrefixToOperationMatcher<OpScopeAvailable>(OpScopeAvailable.class) {
            @Override
            protected OpScopeAvailable createInstance(LogicalDate startDay, Vector<Vector<AptUnitOccupancySegment>> matches) {
                return new OpScopeAvailable(startDay, matches);
            }
        });
        operationMatchers.add(new StatePrefixToOperationMatcher<OpScopeOffMarket>(OpScopeOffMarket.class) {
            @Override
            protected OpScopeOffMarket createInstance(LogicalDate startDay, Vector<Vector<AptUnitOccupancySegment>> matches) {
                return new OpScopeOffMarket(startDay, matches);
            }
        });
        operationMatchers.add(new StatePrefixToOperationMatcher<OpScopeRenovation>(OpScopeRenovation.class) {
            @Override
            protected OpScopeRenovation createInstance(LogicalDate startDay, Vector<Vector<AptUnitOccupancySegment>> matches) {
                return new OpScopeRenovation(startDay, matches);
            }
        });

        return operationMatchers;
    }

    // TODO Can't think of anything better right now
    /**
     * Matches all the states that can be replace by the provided operation, and then instantiates the operation bound to those states.
     * 
     * @param <O>
     */
    private static abstract class StatePrefixToOperationMatcher<O extends IAptUnitOccupancyOperation> {

        private final UnitStatus[] prefixPattern;

        private final Class<O> opClass;

        public StatePrefixToOperationMatcher(Class<O> opClass) {
            this.opClass = opClass;
            DefineSourceOccupancyStatePrefix prefix = opClass.getAnnotation(DefineSourceOccupancyStatePrefix.class);
            assert prefix != null : "the operation class must be annotated with " + DefineSourceOccupancyStatePrefix.class;
            prefixPattern = prefix.value();
            assert prefixPattern.length != 0 : "state prefix pattern can't be empty";
        }

        protected abstract O createInstance(LogicalDate startDay, Vector<Vector<AptUnitOccupancySegment>> matches);

        public O instance(LogicalDate startDay, List<AptUnitOccupancySegment> state) {
            Vector<Vector<AptUnitOccupancySegment>> matches = new Vector<Vector<AptUnitOccupancySegment>>();

            Vector<AptUnitOccupancySegment> matched = new Vector<AptUnitOccupancySegment>();
            int i = 0;
            for (AptUnitOccupancySegment s : state) {
                if (prefixPattern[i] == unitStatus(s)) {
                    ++i;
                    matched.add(s);
                    if (i == prefixPattern.length) {
                        matches.add(matched);
                        matched = new Vector<AptUnitOccupancySegment>();
                        i = 0;
                    }
                }
            }
            return createInstance(startDay, matches);
        }

        private UnitStatus unitStatus(AptUnitOccupancySegment s) {
            UnitStatus status = null;
            switch (s.status().getValue()) {
            case vacant:
                status = UnitStatus.V;
                break;
            case available:
                status = UnitStatus.A;
                break;
            case reserved:
                status = s.dateTo().getValue().equals(AptUnitOccupancyManagerHelper.MAX_DATE) ? UnitStatus.R : UnitStatus.r;
                break;
            case leased:
                status = s.dateTo().getValue().equals(AptUnitOccupancyManagerHelper.MAX_DATE) ? UnitStatus.L : UnitStatus.l;
                break;
            case renovation:
                status = UnitStatus.n;
                break;
            case offMarket:
                status = s.dateTo().getValue().equals(AptUnitOccupancyManagerHelper.MAX_DATE) ? UnitStatus.O : UnitStatus.o;
                break;
            default:
                break;
            }
            return status;
        }
    }

}
