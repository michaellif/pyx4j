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
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.building.Building;

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

    @NotNull
    @ToString(index = 0)
    @Caption(watermark = "e.g. 1bdrm+f")
    IPrimitive<String> name();

    @Caption(watermark = "e.g. 1 Bedroom, Furnished")
    IPrimitive<String> marketingName();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Caption(name = "Number of Storeys")
    IPrimitive<Integer> floorCount();

    @NotNull
    @Caption(name = "Beds")
    IPrimitive<Integer> bedrooms();

    IPrimitive<Integer> dens();

    @NotNull
    @Caption(name = "Baths")
    IPrimitive<Integer> bathrooms();

    // Separate WC
    IPrimitive<Integer> halfBath();

    @Format("#0.000")
    IPrimitive<Double> area();

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
}
