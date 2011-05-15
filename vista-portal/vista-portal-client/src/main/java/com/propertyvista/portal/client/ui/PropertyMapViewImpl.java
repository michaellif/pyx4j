/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class PropertyMapViewImpl extends SimplePanel implements PropertyMapView {

    public PropertyMapViewImpl() {
        HTML label = new HTML(
                "<iframe width=\"725\" height=\"650\" frameborder=\"0\" scrolling=\"no\" marginheight=\"0\" marginwidth=\"0\" src=\"http://maps.google.ca/maps?f=q&amp;source=s_q&amp;hl=en&amp;geocode=&amp;q=Richmond+Hill&amp;aq=&amp;sll=43.871168,-79.441624&amp;sspn=0.060638,0.222988&amp;ie=UTF8&amp;hq=&amp;hnear=Richmond+Hill,+York+Regional+Municipality,+Ontario&amp;ll=43.88493,-79.43039&amp;spn=0.090194,0.222988&amp;z=13&amp;iwloc=A&amp;output=embed\"></iframe><br /><small>View <a href=\"http://maps.google.ca/maps?f=q&amp;source=embed&amp;hl=en&amp;geocode=&amp;q=Richmond+Hill&amp;aq=&amp;sll=43.871168,-79.441624&amp;sspn=0.060638,0.222988&amp;ie=UTF8&amp;hq=&amp;hnear=Richmond+Hill,+York+Regional+Municipality,+Ontario&amp;ll=43.88493,-79.43039&amp;spn=0.090194,0.222988&amp;z=13&amp;iwloc=A\" style=\"color:#0000FF;text-align:left\">City-wide Search Result</a> in a larger map</small>");
        setWidget(label);
    }

}
