/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import java.util.Date;

import com.pyx4j.entity.adapters.index.AlphanumIndexAdapter;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoverStats;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.PropertyManagerReferenceAdapter;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserBuildings;

//TODO rename to Property?!
@ToStringFormat("{0}{1,choice,null#|!null#, {1}}")
@DiscriminatorValue("Disc_Building")
public interface Building extends PolicyNode {

    interface OrderInComplexId extends ColumnId {
    }

    @OrderColumn(OrderInComplexId.class)
    IPrimitive<Integer> orderInComplex();

    Complex complex();

    @Caption(name = "Primary in Complex")
    IPrimitive<Boolean> complexPrimary();

    @NotNull
    @ToString(index = 0)
    @Length(10)
    @Indexed(uniqueConstraint = true, ignoreCase = true)
    @MemberColumn(notNull = true, sortAdapter = AlphanumIndexAdapter.class)
    IPrimitive<String> propertyCode();

    IPrimitive<String> externalId();

    @Editor(type = EditorType.suggest)
    @Reference(adapter = PropertyManagerReferenceAdapter.class)
    PropertyManager propertyManager();

    @ToString(index = 1)
    @EmbeddedEntity
    @Caption(name = "Information")
    BuildingInfo info();

    @EmbeddedEntity
    BuildingFinancial financial();

    @EmbeddedEntity
    BuildingContactInfo contacts();

    @Owned
    Marketing marketing();

    @Owned
    @Detached
    IList<Media> media();

    @Detached
    @Owned(forceCreation = true, cascade = {})
    ProductCatalog productCatalog();

    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = CrmUserBuildings.class)
    ISet<CrmUser> userAccess();

    @Timestamp
    IPrimitive<Date> updated();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Owned
    @Detached(level = AttachLevel.Detached)
    IList<BuildingAmenity> amenities();

    @Owned
    @Detached(level = AttachLevel.Detached)
    IList<BuildingUtility> utilities();

    @Detached(level = AttachLevel.Detached)
    @Owned
    ISet<BuildingMerchantAccount> merchantAccounts();

    // ----------------------------------------------------
    // parent <-> child relationship:

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Floorplan> floorplans();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<AptUnit> units();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Elevator> elevators();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Boiler> boilers();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Parking> parkings();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<LockerArea> lockerAreas();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<Roof> roofs();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<BuildingArrearsSnapshot> arrearsSnapshots();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<UnitTurnoverStats> unitTurnoverStats();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<BillingCycle> billingCycles();

    IPrimitive<Boolean> useExternalBilling();

}
