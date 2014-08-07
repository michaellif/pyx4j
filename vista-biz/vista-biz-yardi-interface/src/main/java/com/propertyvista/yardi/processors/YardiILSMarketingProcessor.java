/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.DepositType;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.Property;
import com.yardi.entity.ils.VacateDate;
import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.PropertyIDType;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.mappers.MappingUtils;
import com.propertyvista.yardi.mappers.UnitsMapper;

public class YardiILSMarketingProcessor {

    public boolean updateAvailability(AptUnit unit, Availability avail) throws YardiServiceException {
        // no availability means "not available"
        LogicalDate dateAvail = null;
        if (avail != null) {
            // use MadeReadyDate if set, otherwise VacateDate
            dateAvail = toDate(avail.getMadeReadyDate());
            if (dateAvail == null) {
                dateAvail = toDate(avail.getVacateDate());
            }
        }
        return ServerSideFactory.create(OccupancyFacade.class).setAvailability(unit, dateAvail);
    }

    /**
     * Yardi bug TR#333065 - addressed in 7S ILSGuestCard Plugin v3
     * AddressLine1 = marketing name, AddressLine2 = property address
     * ==============================================================
     * <PropertyID>
     * <MITS:Identification Type="other">
     * <MITS:PrimaryID>aven2175</MITS:PrimaryID>
     * <MITS:MarketingName>PD Kanco LP - 2175 Avenue Road</MITS:MarketingName>
     * </MITS:Identification>
     * <MITS:Address Type="property">
     * <MITS:Address1>PD Kanco LP - 2175 Avenue Road</MITS:Address1>
     * <MITS:Address2>2175 Avenue Road</MITS:Address2>
     * . . .
     * </MITS:Address>
     * </PropertyID>
     */
    public PropertyIDType fixPropertyID(PropertyIDType propertyID) {
        // fix the address if the addressLine1 is the same as marketing name
        String marketingName = propertyID.getIdentification().getMarketingName();
        Address addr = propertyID.getAddress().get(0);
        String addr1 = addr.getAddress1();
        if (addr1 == null || addr1.equals(marketingName)) {
            StringBuilder address2 = new StringBuilder();
            for (String item : addr.getAddress2()) {
                address2.append(item);
            }
            addr.setAddress1(address2.toString());
            addr.getAddress2().clear();
        }
        return propertyID;
    }

    public Map<String, BigDecimal> getDepositInfo(Property property) {
        Map<String, BigDecimal> depositInfo = new HashMap<String, BigDecimal>();
        for (ILSUnit ilsUnit : property.getILSUnit()) {
            // process deposit data
            DepositType depositType = ilsUnit.getDeposit();
            if (ilsUnit.getUnit().getInformation().size() == 1 && depositType != null && depositType.getAmount().getValue() != null) {
                depositInfo.put(UnitsMapper.getUnitID(ilsUnit.getUnit().getInformation().get(0)), depositType.getAmount().getValue() == null ? null
                        : depositType.getAmount().getValue().setScale(2));
            }
        }
        return depositInfo;
    }

    private LogicalDate toDate(VacateDate vacDate) {
        return MappingUtils.toLogicalDate(vacDate.getYear() + "-" + vacDate.getMonth() + "-" + vacDate.getDay());
    }

    private LogicalDate toDate(MadeReadyDate rdyDate) {
        return MappingUtils.toLogicalDate(rdyDate.getYear() + "-" + rdyDate.getMonth() + "-" + rdyDate.getDay());
    }

}
