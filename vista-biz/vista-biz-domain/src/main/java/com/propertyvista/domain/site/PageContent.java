/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-07
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

public interface PageContent extends IEntity {

    @NotNull
    AvailableLocale locale();

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    PageDescriptor descriptor();

    @OrderColumn
    IPrimitive<Integer> orderInDescriptor();

    // -------------------------

    @Owned
    @Length(20845)
    @Editor(type = Editor.EditorType.richtextarea)
    //TODO Blob
    IPrimitive<String> content();

    // =========================

    @Transient
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    PageCaption _caption();
}
