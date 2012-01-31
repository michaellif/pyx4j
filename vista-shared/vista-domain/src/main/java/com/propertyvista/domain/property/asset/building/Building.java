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

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.PropertyManagerReferenceAdapter;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.ComplexReferenceAdapter;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserBuildings;

//TODO rename to Property?!
@ToStringFormat("{0}, {1}")
@DiscriminatorValue("Disc_Building")
public interface Building extends PolicyNode {

    // TODO: To be auto-generated by system unless user enters his own value. (Can be modified (by accounting)).
    //          It seems that two buttons are required: Validate (on Uniqueness) and Generate (new one).
    @NotNull
    @ToString(index = 0)
    @Length(10)
    @Indexed(uniqueConstraint = true)
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

    @EmbeddedEntity
    Marketing marketing();

    @Owner
//    @Editor(type = EditorType.suggest)
    @Reference(adapter = ComplexReferenceAdapter.class)
    Complex complex();

    @Caption(name = "Primary in Complex")
    IPrimitive<Boolean> complexPrimary();

    @Detached
    IList<Media> media();

    @Detached
    ProductCatalog serviceCatalog();

    @Detached
    DashboardMetadata dashboard();

    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = CrmUserBuildings.class, cascade = false)
    ISet<CrmUser> userAccess();
}
