/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.IsWidget;

public interface UserMessageView extends IsWidget {

    public enum Type {
        info("green"), warning("blue"), error("orange"), failure("red");

        private final String color;

        Type(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public void setPresenter(Presenter presenter);

    void show(String messages, Type type);

    void hide(Type type);

    public interface Presenter {

    }

}