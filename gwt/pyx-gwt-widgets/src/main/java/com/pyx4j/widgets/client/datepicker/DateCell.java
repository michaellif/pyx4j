package com.pyx4j.widgets.client.datepicker;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Label;
import com.pyx4j.widgets.client.style.Selector;

public class DateCell extends Label {
	private boolean enabled = true;

	private Date date;

	private DateGrid parent;

	public DateCell() {
		addHandlers();
	}

	public void addHandlers() {
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DateCell cell = (DateCell) event.getSource();
				cell.setStyleName(
						Selector.getDependentName(DatePickerExtended.StyleDependent.selected),
						true);
			}
		});
		this.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				heighlight(true);
			}
		});
		this.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				heighlight(false);
			}
		});
	}

	@Override
	public DateGrid getParent() {
		return parent;
	}

	public void setParent(DateGrid parent) {
		this.parent = parent;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = new Date(date.getTime());
		redraw();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.setStyleName(Selector
				.getDependentName(DatePickerExtended.StyleDependent.disabled),
				!enabled);
	}

	public final void setSelected(boolean selected) {
		this.setStyleName(Selector
				.getDependentName(DatePickerExtended.StyleDependent.selected),
				selected);
	}

	private void heighlight(boolean isheighlighted) {
		this.setStyleName(
				Selector.getDependentName(DatePickerExtended.StyleDependent.heighlighted),
				isheighlighted);
	}

	private void redraw() {
		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DAY);
		String label = format.format(date);
		this.setText(label);
	}
}