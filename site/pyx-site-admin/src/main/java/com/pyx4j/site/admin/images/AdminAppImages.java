/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.site.admin.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AdminAppImages extends ClientBundle {

    @Source("image.png")
    ImageResource image();

    @Source("file-save.png")
    ImageResource save();

}
