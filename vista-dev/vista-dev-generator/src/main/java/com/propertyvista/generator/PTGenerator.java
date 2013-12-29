/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.generator;

import java.util.Random;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.misc.VistaDevPreloadConfig;

public class PTGenerator {

    public static boolean equifaxDemo = false;

    protected static final long MAX_CREATE_WAIT = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_RESERVED_DURATION = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L * 3L;

    protected static final long MIN_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L;

    protected final Random rnd;

    public PTGenerator(VistaDevPreloadConfig config) {
        DataGenerator.setRandomSeed(config.leaseGenerationSeed);
        this.rnd = new Random(666l);
    }

    Pet createPet() {
        Pet pet = EntityFactory.create(Pet.class);

        pet.name().setValue(RandomUtil.random(PreloadData.PET_NAMES));
        pet.color().setValue(RandomUtil.random(PreloadData.PET_COLORS));
        pet.breed().setValue(RandomUtil.random(PreloadData.PET_BREEDS));

        if (RandomUtil.randomBoolean()) {
            pet.weightUnit().setValue(WeightUnit.kg);
            pet.weight().setValue(4 + RandomUtil.randomInt(20));
        } else {
            pet.weightUnit().setValue(WeightUnit.lb);
            pet.weight().setValue(10 + RandomUtil.randomInt(30));
        }

        pet.birthDate().setValue(RandomUtil.randomLogicalDate(1985, 2011));

        return pet;
    }

    Vehicle createVehicle() {
        Vehicle vehicle = EntityFactory.create(Vehicle.class);

        vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
        vehicle.year().setValue(RandomUtil.randomYear(1992, 2012));
        vehicle.make().setValue(RandomUtil.random(PreloadData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(PreloadData.CAR_MODELS));
        vehicle.province().code().setValue(RandomUtil.random(PreloadData.PROVINCES));
        vehicle.country().set(vehicle.province().country());

        return vehicle;
    }
}
