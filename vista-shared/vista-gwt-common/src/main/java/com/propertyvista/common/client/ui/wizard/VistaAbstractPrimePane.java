/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.wizard;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.ui.prime.IPrimePane;
import com.pyx4j.site.client.ui.prime.misc.IMemento;
import com.pyx4j.site.client.ui.prime.misc.MementoImpl;

public class VistaAbstractPrimePane extends VistaAbstractView implements IPrimePane {

    private final IMemento memento = new MementoImpl();

    @Override
    public IMemento getMemento() {
        return memento;
    }

    @Override
    public void storeState(Place place) {
        memento.setCurrentPlace(place);
    }

    @Override
    public void restoreState() {
    }
}
