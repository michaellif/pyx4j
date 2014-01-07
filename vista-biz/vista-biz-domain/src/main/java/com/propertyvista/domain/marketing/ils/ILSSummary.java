/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing.ils;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.MediaFile;

public interface ILSSummary extends IEntity {
    @NotNull
    IPrimitive<String> title();

    @NotNull
    @Length(4000)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> description();

    @Owned
    MediaFile frontImage();
}
