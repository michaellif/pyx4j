/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.domain.site;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

/**
 * Used as base for Site and Page Descriptors for hierarchy maintaining
 */

@Inheritance
@AbstractEntity
public interface Descriptor extends IEntity {

    /**
     * used as url parameter name in Site/PageViewerActivities and PageEditorActivity
     */
    public static final String PARENT_CLASS = "pc";

    @Owned
    IList<PageDescriptor> childPages();
}
