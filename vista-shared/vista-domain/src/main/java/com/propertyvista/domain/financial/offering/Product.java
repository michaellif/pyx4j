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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.financial.offering.Product.ProductV;

@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface Product<V extends ProductV<?>> extends IVersionedEntity<V> {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    ProductCatalog catalog();

    @OrderColumn
    IPrimitive<Integer> orderInCatalog();

    @Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
    @AbstractEntity
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    public interface ProductV<P extends Product<?>> extends IVersionData<P> {

        @Owned
        @Detached
        IList<ProductItem> items();

        @Length(25)
        @ToString(index = 1)
        IPrimitive<String> name();

        @Length(250)
        @Editor(type = Editor.EditorType.textarea)
        IPrimitive<String> description();

        @NotNull
        IPrimitive<PublicVisibilityType> visibility();
    }
}
