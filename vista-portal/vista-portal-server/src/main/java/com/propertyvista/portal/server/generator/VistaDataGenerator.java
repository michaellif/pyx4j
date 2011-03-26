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
package com.propertyvista.portal.server.generator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.server.pt.services.ApartmentServicesImpl;
import com.propertyvista.portal.server.pt.services.ApplicationServicesImpl;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;

public class VistaDataGenerator {

//    private User user;

    private final Application application;

    public VistaDataGenerator(Application application) {
        this.application = application;
    }

    public static User createUser() {
        User user = EntityFactory.create(User.class);
        user.name().setValue("Gregory Holmes");
        user.email().setValue("gregory@221b.com");

        UserCredential credential = EntityFactory.create(UserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword("london"));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(VistaBehavior.POTENTIAL_TENANT);

        return user;
    }

    public static Application createApplication(User user) {
        Application application = EntityFactory.create(Application.class);
        application.user().set(user);
        return application;
    }

    public ApplicationProgress createApplicationProgress() {
        ApplicationProgress progress = ApplicationServicesImpl.createApplicationProgress();
        progress.application().set(application);
        return progress;
    }

    public UnitSelection createUnitSelection(Application application) {
        UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
        unitSelection.application().set(application);

        // unit selection criteria
        UnitSelectionCriteria criteria = EntityFactory.create(UnitSelectionCriteria.class);
        criteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);
        criteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);

        Calendar avalableTo = new GregorianCalendar();
        avalableTo.setTime(new Date());
        avalableTo.add(Calendar.MONTH, 1);
        DateUtils.dayStart(avalableTo);

        criteria.availableFrom().setValue(new Date());
        criteria.availableTo().setValue(avalableTo.getTime());

        unitSelection.selectionCriteria().set(criteria);

        ApartmentServicesImpl apartmentServices = new ApartmentServicesImpl();
        apartmentServices.loadAvailableUnits(unitSelection);

        // chose the first unit for demo
        ApartmentUnit selectedUnit = unitSelection.availableUnits().units().iterator().next();
        unitSelection.selectedUnit().set(selectedUnit);
        unitSelection.selectedUnitId().set(selectedUnit.id());
        unitSelection.markerRent().set(unitSelection.selectedUnit().marketRent().get(1)); // choose second lease
        unitSelection.rentStart().setValue(selectedUnit.avalableForRent().getValue());

        return unitSelection;
    }
}
