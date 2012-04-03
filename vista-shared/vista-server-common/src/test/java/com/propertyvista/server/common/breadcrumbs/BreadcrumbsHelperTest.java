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
package com.propertyvista.server.common.breadcrumbs;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.breadcrumbs.BreadcrumbDTO;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper;
import com.propertyvista.server.common.breadcurmbs.BreadcrumbsHelper.LabelCreator;

public class BreadcrumbsHelperTest {

    private Key level1pk;

    private BreadcrumbsHelper bchelper;

    @Test
    public void test() {
        Level1 level1 = Persistence.service().retrieve(Level1.class, level1pk);

        Key level4pk = level1.owned2As().get(1).owned3s().get(0).owned4s().get(1).getPrimaryKey();
        Level4 level4 = Persistence.service().retrieve(Level4.class, level4pk);

        List<BreadcrumbDTO> trail = bchelper.breadcrumbTrail(level4);
        BreadcrumbDTO breadcrumb = trail.get(0);

        Assert.assertEquals(Level1.class.getName(), breadcrumb.entityClass().getValue());
        Assert.assertEquals(level1.getPrimaryKey(), breadcrumb.entityId().getValue());
        Assert.assertEquals("*level 1", breadcrumb.label().getValue());

        breadcrumb = trail.get(1);
        Assert.assertEquals(Level2A.class.getName(), breadcrumb.entityClass().getValue());
        Assert.assertEquals(level1.owned2As().get(1).getPrimaryKey(), breadcrumb.entityId().getValue());
        Assert.assertEquals("level 2A: val=" + 1, breadcrumb.label().getValue());

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

        HashMap<Class<? extends IEntity>, BreadcrumbsHelper.LabelCreator> labelCreatorsMap = new HashMap<Class<? extends IEntity>, BreadcrumbsHelper.LabelCreator>();
        labelCreatorsMap.put(Level1.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return "*" + ((Level1) entity).name().getValue();
            }
        });
        labelCreatorsMap.put(Level2A.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return "level 2A: val=" + ((Level2A) entity).value().getValue();
            }
        });
        labelCreatorsMap.put(Level2B.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return "level 2B: name/val=" + ((Level2B) entity).name().getValue() + "/" + ((Level2B) entity).value().getValue();
            }
        });
        labelCreatorsMap.put(Level3.class, new LabelCreator() {
            @Override
            public String label(IEntity entity) {
                return ((Level3) entity).value1().getValue() + ":" + ((Level3) entity).value2().getValue();
            }
        });

        // no label creator for Level4: use string view of an entity

        bchelper = new BreadcrumbsHelper(labelCreatorsMap);
    }

    @After
    public void teardown() {
        //Persistence.service().delete(EntityQueryCriteria.create(Level1.class));
    }

}
