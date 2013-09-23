/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-19
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.marketing;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.ils.ILSOpenHouse;
import com.propertyvista.domain.marketing.ils.ILSOpenHouse.OpenHouseDateId;

public interface Marketing extends IEntity {

    @NotNull
    IPrimitive<PublicVisibilityType> visibility();

    @ToString
    @Length(128)
    IPrimitive<String> name();

    @Length(20845)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @EmbeddedEntity
    AddressStructured marketingAddress();

    @Owned
    MarketingContacts marketingContacts();

    @Owned
    @Detached
    @OrderBy(OpenHouseDateId.class)
    IList<ILSOpenHouse> openHouseSchedule();

    @Owned
    @Detached
    @Caption(name = "Marketing Blurb")
    IList<AdvertisingBlurb> adBlurbs();
}
