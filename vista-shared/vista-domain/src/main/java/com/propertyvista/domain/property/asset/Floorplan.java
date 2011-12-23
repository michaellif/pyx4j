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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("Disc_Floorplan")
public interface Floorplan extends PolicyNode {

    @Owner
    @Detached
    @ReadOnly
    @Indexed
    Building building();

    @ToString(index = 0)
    IPrimitive<String> name();

    IPrimitive<String> marketingName();

    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

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

    @Detached
    IList<Media> media();

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    FloorplanCounters counters();
}