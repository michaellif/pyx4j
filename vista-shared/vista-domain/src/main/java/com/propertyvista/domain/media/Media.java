/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.media;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.File;

public interface Media extends IEntity {

    @Translatable
    public enum Type {

        file,

        youTube,

        externalUrl;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString
    IPrimitive<String> caption();

    @MemberColumn(name = "mediaType")
    IPrimitive<Type> type();

    @EmbeddedEntity
    @MemberColumn(name = "mediaFile")
    File file();

    IPrimitive<String> youTubeVideoID();

    IPrimitive<String> url();

    @Caption(name = "Public")
    IPrimitive<Boolean> visibleToPublic();
}
