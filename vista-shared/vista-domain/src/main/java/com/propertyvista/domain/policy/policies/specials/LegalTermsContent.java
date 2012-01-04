/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies.specials;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.site.AvailableLocale;

public interface LegalTermsContent extends IEntity {

    @NotNull
    AvailableLocale locale();

    @NotNull
    @Caption(name = "Caption")
    IPrimitive<String> localizedCaption();

    @Owner
    @Detached
    @ReadOnly
    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    LegalTermsDescriptor descriptor();

    @Owned
    @Length(20845)
    @Editor(type = Editor.EditorType.richtextarea)
    //TODO Blob
    IPrimitive<String> content();

}
