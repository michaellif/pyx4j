/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.pyx4j.entity.client.ui.IForm;
import com.pyx4j.entity.client.ui.TextField;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.tester.domain.Customer;

public class TestForm implements IForm<Customer> {

    public void create() {

        TextField firstNameText = new TextField(this);
        firstNameText.bind(EntityFactory.create(Customer.class).firstName().getPath());

        TextField countryText = new TextField(this);
        countryText.bind(EntityFactory.create(Customer.class).homeAddress().country().name().getPath());
    }

    @Override
    public void setEntity(Customer entity) {
        // TODO Auto-generated method stub
    }

}
