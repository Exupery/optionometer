$(document).ready(function() {
	
	$("#screener-full").submit(function() {
		submitScreen("#"+this.id);
		return false;
	});
	
	$(".table-sorted").tablesorter();

	updateScreenerInputs();

});

function submitScreen(formId) {
	var strat = $(formId + " #strat").val();
	var sym = $(formId + " #sym").val();
	var href = "/screener/";
	href += (sym && sym.length > 0) ?  strat + "/" + sym : strat;
	href += screenQueryString(formId);
	window.location = href;
}

function screenQueryString(formId) {
	var queryString = "";
	var moneyness = $(formId + " #moneyness").val();
	var numParams = {
		mindays: $(formId + " #mindays").val(),
		maxdays: $(formId + " #maxdays").val(),
		minprofitpercent: $(formId + " #minprofitpercent").val(),
		minprofitamount: $(formId + " #minprofitamount").val(),
		maxlossamount: $(formId + " #maxlossamount").val(),
	};
	
	var isFirstParam = true;
	
	if (moneyness != "any") {
		queryString += (isFirstParam) ? "?" : "&";
		queryString += "moneyness=" + moneyness;
		isFirstParam = false;
	}
	
	for (var param in numParams) {
		var value = numParams[param];
		if (value) {
			queryString += (isFirstParam) ? "?" : "&";
			queryString += param + "=" + value;
			isFirstParam = false;
		}
	}
	
	return queryString;
}

function updateScreenerInputs() {
	var ck = $.cookie();
//	if (ck.strat) {
//		$("#strat").val(ck.strat);
//	}
//	if (ck.sym) {
//		$("#sym").val(ck.sym);
//	}
//	if (ck.moneyness) {
//		$("#moneyness").val(ck.moneyness);
//	}
	var alphaParams = ["strat", "sym", "moneyness"];
	for (var i in alphaParams) {
		var key = alphaParams[i];
		if (ck[key]) {
			$("#"+key.toLowerCase()).val(ck[key]);
		}
	}
	
	var numericParams = [
         "minDays", 
         "maxDays", 
         "minProfitAmount", 
         "minProfitPercent", 
         "maxLossAmount",
    ];
	for (var i in numericParams) {
		var key = numericParams[i];
		if (ck[key] && ck[key] > 0) {
			$("#"+key.toLowerCase()).val(ck[key]);
		}
	}
	
}

function mailto() {
	ML="f>o@eh=p./rcl nti\"m:<a";
	MI="DE=5:406ABE@<?2C@>02327?@2>2B4?4:8;2BA1@>02327?@2>2B4?4:8;2BD9E1";
	OT="";
	for (j=0;j<MI.length;j++) {
		OT+=ML.charAt(MI.charCodeAt(j)-48);
	} document.write(OT);
}