/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.tester.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.tester.domain.Department;
import com.propertyvista.portal.tester.domain.Employee;
import com.propertyvista.portal.tester.ui.EditDepartmentView;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.AppPlace;

public class EditDepartmenActivity extends AbstractActivity implements EditDepartmentView.Presenter {

    private static final Logger log = LoggerFactory.getLogger(EditDepartmenActivity.class);

    private final EditDepartmentView view;

    private final PlaceController placeController;

    @Inject
    public EditDepartmenActivity(EditDepartmentView view, PlaceController placeController) {
        this.view = view;
        this.placeController = placeController;
        view.setPresenter(this);
    }

    public EditDepartmenActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        //TODO make this in EventBus
        {
            Department department = EntityFactory.create(Department.class);
            department.name().setValue("R&D");
            department.manager().firstName().setValue("The manager");
            {
                Employee emp = department.employees().$();
                emp.firstName().setValue("John");
                emp.lastName().setValue("Dow");
                emp.phone().setValue("444-4444");
                department.employees().add(emp);
            }
            {
                Employee emp = department.employees().$();
                emp.firstName().setValue("Peter");
                emp.lastName().setValue("Pen");
                emp.phone().setValue("555-4444");
                department.employees().add(emp);
            }

            //contractors
            {
                Employee emp = department.contractors().$();
                emp.firstName().setValue("Vlad");
                emp.lastName().setValue("S");
                emp.phone().setValue("100-8523");
                department.contractors().add(emp);
            }

            view.populate(department);
        }
    }

    @Override
    public void save(Department entity) {
        log.info("SAVED {}", entity);
    }

}