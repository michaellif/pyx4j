/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.policy.framework.LowestApplicableNode;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("EmailTemplatesPolicy")
@LowestApplicableNode(value = Building.class)
public interface EmailTemplatesPolicy extends Policy {

    @Owned
    IList<EmailTemplate> templates();

    @NotNull
    @Owned
    @Length(4096)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> header();

    @NotNull
    @Owned
    @Length(4096)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> footer();
}
