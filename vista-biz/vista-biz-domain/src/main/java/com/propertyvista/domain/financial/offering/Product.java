/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial.offering;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.ILooseVersioning;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Product.ProductV;
import com.propertyvista.domain.note.HasNotesAndAttachments;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@ToStringFormat("{1}, {0}{2,choice,null#|!null# - ${2}}")
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface Product<V extends ProductV<?>> extends IVersionedEntity<V>, ILooseVersioning, HasNotesAndAttachments {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    ProductCatalog catalog();

    @NotNull
    IPrimitive<Boolean> defaultCatalogItem();

    /**
     * expired date when it is no loner effective
     */
    IPrimitive<LogicalDate> expiredFrom();

    @ToString(index = 0)
    @NotNull
    @MemberColumn(notNull = true)
    ARCode code();

    /**
     * used in yardi-integration mode for storing Rentable Item Type Code
     */
    IPrimitive<String> yardiCode();

    @AbstractEntity
    @Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    public interface ProductV<P extends Product<?>> extends IVersionData<P> {

        @Owned
        @Detached(level = AttachLevel.Detached)
        IList<ProductItem> items();

        @Length(50)
        @ToString(index = 1)
        IPrimitive<String> name();

        @Length(250)
        @Editor(type = Editor.EditorType.textarea)
        IPrimitive<String> description();

        @NotNull
        @ToString(index = 2)
        @Format("#,##0.00")
        @Caption(name = "Market Price")
        @Editor(type = EditorType.money)
        IPrimitive<BigDecimal> price();

        @EmbeddedEntity
        ProductDeposit depositLMR();

        @EmbeddedEntity
        ProductDeposit depositMoveIn();

        @EmbeddedEntity
        ProductDeposit depositSecurity();

        @NotNull
        IPrimitive<Boolean> availableOnline();
    }
}
