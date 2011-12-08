package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface GadgetCellTemplates extends SafeHtmlTemplates {
    @Template("<div title=\"{1}\" style=\"text-align: center\">{0}</div>")
    SafeHtml gadgetCellWithTooltipDescription(String name, String description);

    @Template("<div><div style=\"font-weight: bold; text-align: left; \">{0}</div><div style=\"text-align: justify\">{1}</div></div>")
    SafeHtml gadgetCellWithInlineDescription(String name, String description);

}
