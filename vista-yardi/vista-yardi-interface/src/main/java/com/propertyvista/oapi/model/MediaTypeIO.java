/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.propertyvista.domain.media.Media;
import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.ElementIO;

public class MediaTypeIO implements ElementIO {

    @XmlValue
    public Media.Type value;

    @XmlAttribute
    public Action action;

    public MediaTypeIO() {
    }

    public MediaTypeIO(Media.Type value) {
        this.value = value;
    }

    @Override
    public Action getAction() {
        return action;
    }

}
