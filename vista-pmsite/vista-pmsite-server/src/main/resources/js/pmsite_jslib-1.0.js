/*
 * Sets options for select field identified by selId
 */
function setSelectionOptions(selId, optArr, defOpt, selOpt) {
	var sel = document.getElementById(selId);
	if (! sel) return;
	sel.options.length = 0;
	if (defOpt) {
		sel.options[0] = new Option(defOpt,'');
	}
	for (var i = 0; i < optArr.length; i++) {
		var v = optArr[i];
		var selected = (v == selOpt);
		sel.options[sel.options.length] = new Option(v,v,selected,selected);
	}
}
