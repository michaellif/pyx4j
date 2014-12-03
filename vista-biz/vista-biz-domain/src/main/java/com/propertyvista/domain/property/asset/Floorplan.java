/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.shared.config.YardiImported;

@DiscriminatorValue("Floorplan")
public interface Floorplan extends PolicyNode, HasNotesAndAttachments {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    Building building();

    // third party identifier
    @NotNull
    @ReadOnly
    @YardiImported
    IPrimitive<String> code();

    @NotNull
    @ToString(index = 0)
    @Caption(watermark = "e.g. 1bdrm+f")
    @YardiImported
    IPrimitive<String> name();

    @Caption(watermark = "e.g. 1 Bedroom, Furnished")
    IPrimitive<String> marketingName();

    @Length(4000)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Caption(name = "Number of Storeys")
    IPrimitive<Integer> floorCount();

    @NotNull
    @Caption(name = "Beds")
    @YardiImported
    IPrimitive<Integer> bedrooms();

    IPrimitive<Integer> dens();

    @NotNull
    @Caption(name = "Baths")
    @YardiImported
    IPrimitive<Integer> bathrooms();

    // Separate WC
    IPrimitive<Integer> halfBath();

    @Format("#0.000")
    @YardiImported
    IPrimitive<Double> area();

    @YardiImported
    IPrimitive<AreaMeasurementUnit> areaUnits();

    @Owned
    @Detached(level = AttachLevel.Detached)
    IList<MediaFile> media();

    @Owned
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    FloorplanCounters counters();

    @Timestamp
    IPrimitive<Date> updated();

    // ----------------------------------------------------
    // parent <-> child relationship:
    @Owned
    @OrderBy(FloorplanAmenity.OrderId.class)
    @Detached(level = AttachLevel.Detached)
    IList<FloorplanAmenity> amenities();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = AptUnit.class)
    ISet<AptUnit> units();
}
