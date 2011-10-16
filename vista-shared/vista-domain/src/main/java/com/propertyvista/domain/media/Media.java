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

import javax.xml.bind.annotation.XmlType;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.File;
import com.propertyvista.domain.marketing.PublicVisibilityType;

public interface Media extends IEntity {

    @I18n
    @XmlType(name = "MediaType")
    public enum Type {

        file,

        @Translate("YouTube")
        youTube,

        @Translate("External URL")
        externalUrl;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString
    IPrimitive<String> caption();

    @MemberColumn(name = "mediaType")
    @NotNull
    IPrimitive<Type> type();

    @EmbeddedEntity
    @MemberColumn(name = "mediaFile")
    File file();

    @Caption(name = "YouTube Video ID")
    IPrimitive<String> youTubeVideoID();

    @Caption(name = "URL")
    IPrimitive<String> url();

    IPrimitive<PublicVisibilityType> visibility();
}
