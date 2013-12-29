/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.GlCodeCategory;
import com.propertyvista.test.mock.MockDataModel;

public class GLCodeDataModel extends MockDataModel<GlCode> {

    public GLCodeDataModel() {
    }

    @Override
    protected void generate() {
    }

    private GlCodeCategory ensureGlCodeCategory(int categoryId) {

        EntityQueryCriteria<GlCodeCategory> criteria = new EntityQueryCriteria<GlCodeCategory>(GlCodeCategory.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().categoryId(), categoryId));
        List<GlCodeCategory> glCodeCategories = Persistence.service().query(criteria);

        GlCodeCategory glCodeCategory = null;

        if (glCodeCategories.size() == 0) {
            glCodeCategory = EntityFactory.create(GlCodeCategory.class);
            glCodeCategory.categoryId().setValue(categoryId);
            glCodeCategory.description().setValue("Category " + categoryId);
            Persistence.service().persist(glCodeCategory);
        } else if (glCodeCategories.size() == 1) {
            glCodeCategory = glCodeCategories.get(0);
        } else {
            throw new Error();
        }

        return glCodeCategory;

    }

    public GlCode addGLCode(String name, int codeId, int categoryId, boolean reserved) {

        GlCode gl = EntityFactory.create(GlCode.class);
        gl.codeId().setValue(codeId);
        if (!Persistence.service().retrieve(gl)) {
            gl.glCodeCategory().set(ensureGlCodeCategory(categoryId));
            Persistence.service().persist(gl);
        }

        addItem(gl);

        return gl;

    }
}
