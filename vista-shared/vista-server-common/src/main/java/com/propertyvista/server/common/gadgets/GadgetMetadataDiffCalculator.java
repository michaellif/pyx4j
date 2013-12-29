/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets;

import java.io.Serializable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;

public class GadgetMetadataDiffCalculator {

    /**
     * Impossible to override a by null value.
     * Doesn't check for cycles in entity graph, so provided entities better be without any
     * 
     * @param orig
     * @param version
     * @return a "diff" version of an entity where only properties with values that differ are stored
     */
    public static <E extends IEntity> E diff(E orig, E version) throws IllegalArgumentException {
        if (!orig.getInstanceValueClass().equals(version.getInstanceValueClass())) {
            throw new IllegalArgumentException("both params must have the same value class");
        }
        E diff = (E) EntityFactory.create(orig.getInstanceValueClass());
        for (String memberName : orig.getEntityMeta().getMemberNames()) {
            IObject<?> origMember = orig.getMember(memberName);
            IObject<?> versionMember = version.getMember(memberName);
            if (origMember instanceof IPrimitive) {
                Object origValue = origMember.getValue();
                Object versionValue = versionMember.getValue();
                if ((origValue != null && !origValue.equals(versionValue))) {
                    diff.setMemberValue(memberName, (Serializable) versionValue);
                }
            } else if (origMember instanceof IEntity) {
                IEntity subDiff = diff((IEntity) origMember, (IEntity) versionMember);
                ((IEntity) diff.getMember(memberName)).set(subDiff);

            } else if (origMember instanceof ISet) {
                for (IEntity member : (ISet<?>) origMember) {

                }
            }
        }
        return diff;
    }

}
