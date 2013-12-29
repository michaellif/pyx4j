/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.site.gadgets;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface HomePageGadget extends IEntity {
    public enum GadgetArea {
        narrow, wide
    }

    public enum GadgetType {
        quickSearch(QuickSearchGadgetContent.class), news(NewsGadgetContent.class), testimonials(TestimonialsGadgetContent.class), promo(
                PromoGadgetContent.class), custom(CustomGadgetContent.class);

        private final Class<? extends GadgetContent> contentClass;

        private GadgetType(Class<? extends GadgetContent> contentClass) {
            this.contentClass = contentClass;
        }

        public Class<? extends GadgetContent> getContentClass() {
            return contentClass;
        }

        public static GadgetType getGadgetType(Class<? extends GadgetContent> contentClass) {
            GadgetType gadgetType = null;
            for (GadgetType type : GadgetType.values()) {
                if (type.getContentClass().equals(contentClass)) {
                    gadgetType = type;
                    break;
                }
            }
            return gadgetType;
        }
    }

    public enum GadgetStatus {
        disabled, editing, published
    }

    @NotNull
    IPrimitive<GadgetArea> area();

    IPrimitive<GadgetStatus> status();

    @NotNull
    IPrimitive<String> name();

    @Owned(forceCreation = true)
    GadgetContent content();

    @Transient
    IPrimitive<GadgetType> type();
}
