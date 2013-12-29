/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.breadcrumbs;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.crm.server.services.breadcrumbs.BreadcrumbsHelper;

public class BreadcrumbsHelperTest {

    private Key level1pk;

    private BreadcrumbsHelper bchelper;

    @Test
    public void test() {
        Level1 level1 = Persistence.service().retrieve(Level1.class, level1pk);

        Key level4pk = level1.owned2As().get(1).owned3s().get(0).owned4s().get(1).getPrimaryKey();
        Level4 level4 = Persistence.service().retrieve(Level4.class, level4pk);

        List<IEntity> trail = bchelper.breadcrumbTrail(level4);
        IEntity breadcrumb = trail.get(0);

        Assert.assertEquals(Level1.class.getName(), breadcrumb.getInstanceValueClass().getName());
        Assert.assertEquals(level1.getPrimaryKey(), breadcrumb.getPrimaryKey());

        breadcrumb = trail.get(1);
        Assert.assertEquals(Level2A.class.getName(), breadcrumb.getInstanceValueClass().getName());
        Assert.assertEquals(level1.owned2As().get(1).getPrimaryKey(), breadcrumb.getPrimaryKey());

    }

    @Before
    public void setup() {
        VistaTestDBSetup.init();
        Level1 level1 = EntityFactory.create(Level1.class);
        level1.name().setValue("level 1");
        for (int i = 0; i < 2; ++i) {
            Level2A level2a = EntityFactory.create(Level2A.class);
            level2a.value().setValue(i);
            for (int j = 0; j < 2; ++j) {
                Level3 level3 = EntityFactory.create(Level3.class);
                level3.value1().setValue("FOO");
                level3.value2().setValue("" + j);
                for (int k = 0; k < 2; ++k) {
                    Level4 level4 = EntityFactory.create(Level4.class);
                    level4.x().setValue("level 4 of FOO #" + k);
                    level3.owned4s().add(level4);
                }
                level2a.owned3s().add(level3);
            }
            level1.owned2As().add(level2a);
        }

        for (int i = 0; i < 2; ++i) {
            Level2B level2b = EntityFactory.create(Level2B.class);
            level2b.value().setValue(i);
            level2b.name().setValue("2B");

            for (int j = 0; j < 2; ++j) {
                Level3 level3 = EntityFactory.create(Level3.class);
                level3.value1().setValue("FOO");
                level3.value2().setValue("" + j);
                for (int k = 0; k < 2; ++k) {
                    Level4 level4 = EntityFactory.create(Level4.class);
                    level4.x().setValue("level 4 of FOO #" + k);
                    level3.owned4s().add(level4);
                }
                level2b.owned3s().add(level3);
            }
            level1.owned2Bs().add(level2b);
        }

        Persistence.service().persist(level1);
        level1pk = level1.getPrimaryKey();

        bchelper = new BreadcrumbsHelper();
    }

    @After
    public void teardown() {
        //Persistence.service().delete(EntityQueryCriteria.create(Level1.class));
    }

}
