<script type="text/javascript" language="javascript">

function formCheckAll(formname, checktoggle) {
	var checkboxes = new Array(); 
	checkboxes = document[formname].getElementsByTagName('input');
	for (var i=0; i<checkboxes.length; i++)  {
		if (checkboxes[i].type == 'checkbox')   {
			checkboxes[i].checked = checktoggle;
		}
	}
}

var lastChecked = null;

function setCheckbox(checkbox, event) {
	event = event || window.event;
	if (event.ctrlKey) {
		checkAll(checkbox);
	} else if (event.shiftKey && lastChecked) {
		checkRanges(lastChecked, checkbox, checkbox.checked);
	}
	lastChecked = checkbox;
}

function checkAll(checkbox) {
	checktoggle = checkbox.checked;
	var checkboxes = document.getElementsByName(checkbox.name);
	for (var i=0; i < checkboxes.length; i++)  {
		if (checkboxes[i].type == 'checkbox')   {
			checkboxes[i].checked = checktoggle;
		}
	}
}

function checkRanges(firstCheckbox, lastCheckbox, checktoggle) {
	start = Math.min(firstCheckbox.id,lastCheckbox.id);
	end =	Math.max(firstCheckbox.id,lastCheckbox.id);
	for (i = start; i <= end; i++) { 
		checkbox = document.getElementById(i);
		if (checkbox && checkbox.type == 'checkbox')   {
			checkbox.checked = checktoggle;
		}
	}
}

</script>
