/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.dialog.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface DialogImages extends ClientBundle {

    @Source("dialogConfirm.gif")
    ImageResource confirm();

    @Source("dialogError.gif")
    ImageResource error();

    @Source("dialogInformation.gif")
    ImageResource info();

    @Source("dialogWarning.gif")
    ImageResource warning();

}
