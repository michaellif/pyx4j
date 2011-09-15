/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.server.common.generator.Model;
import com.propertyvista.yardi.bean.resident.ResidentTransactions;
import com.propertyvista.yardi.mapper.GetResidentTransactionsMapper;
import com.propertyvista.yardi.merger.UnitsMerger;

public class GetResidentTransactionsLifecycle {
//    private final static Logger log = LoggerFactory.getLogger(GetResidentTransactionsLifecycle.class);

    public Model load() {
        List<AptUnit> units = Persistence.service().query(new EntityQueryCriteria<AptUnit>(AptUnit.class));
        List<Tenant> tenants = Persistence.service().query(new EntityQueryCriteria<Tenant>(Tenant.class));
        List<Building> buildings = Persistence.service().query(new EntityQueryCriteria<Building>(Building.class));
        Model model = new Model();
        model.getAptUnits().addAll(units);
        model.getTenants().addAll(tenants);
        model.getBuildings().addAll(buildings);
        return model;
    }

    public Model download(YardiClient c, YardiParameters yp) throws AxisFault, RemoteException, JAXBException {
        ResidentTransactions transactions = YardiTransactions.getResidentTransactions(c, yp);

        GetResidentTransactionsMapper mapper = new GetResidentTransactionsMapper();
        mapper.map(transactions);

        return mapper.getModel();
    }

    public Model merge(YardiClient c, YardiParameters yp, boolean persist) throws AxisFault, RemoteException, JAXBException {
        Model imported = download(c, yp);
        Model existing = load();
        UnitsMerger merger = new UnitsMerger();
        Model merged = merger.merge(imported, existing);

        if (persist) {
            for (AptUnit unit : merged.getAptUnits()) {
                Persistence.service().persist(unit);
            }
        }

        return merged;
    }
}
