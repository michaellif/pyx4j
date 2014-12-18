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
 */
package com.propertyvista.domain.marketing;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.shared.config.YardiImported;

public interface Marketing extends IEntity {

    @NotNull
    IPrimitive<PublicVisibilityType> visibility();

    @ToString
    @Length(128)
    @YardiImported
    IPrimitive<String> name();

    @Length(4000)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    /** Indicates if the address below should be used for Marketing. Use Building address if false. */
    IPrimitive<Boolean> useCustomAddress();

    @EmbeddedEntity
    InternationalAddress marketingAddress();

    @Owned
    MarketingContacts marketingContacts();
}
