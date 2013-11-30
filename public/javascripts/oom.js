google.load("visualization", "1.0", {packages:["corechart"]});

$(document).ready(function() {
	
	$("#screener-full").submit(function() {
		window.scrollTo(0, 0);
		Pace.restart();
		$("#screener-full :input").attr("readonly", true);
		$("#screener-full :input").attr("disabled", true);
		submitScreen("#"+this.id);
		return false;
	});
	
	$(".table-sorted").tablesorter({sortInitialOrder: "desc"});
	$(".table-sorted").oneSimpleTablePagination({rowsPerPage: 25});
	$(".table-sorted .hovertip").popover({placement: "bottom", trigger: "hover"});
	
	updateScreenerInputs();
	
	$("body").keydown(function(e) {
		var focusTag = document.activeElement.tagName;
		if (focusTag != "INPUT") {
			var key = e.keyCode;
			if (key==37 || key==74) {
				$("#tablePaginationPrevPage").click();
			} else if (key==39 || key==75) {
				$("#tablePaginationNextPage").click();
			}
		}
	});

});

function drawProfitLossChart(trade, id) {
	var strikeDiff = Math.abs(trade.higherPrice - trade.lowerPrice);
	var data = google.visualization.arrayToDataTable([
        ["Price", "Profit/Loss"],
        [Math.min(trade.lowerPrice-strikeDiff, trade.currentPrice-strikeDiff), trade.lowerAmount],
        [trade.lowerPrice, trade.lowerAmount],
        [trade.higherPrice, trade.higherAmount],
        [Math.max(trade.higherPrice+strikeDiff, trade.currentPrice+strikeDiff), trade.higherAmount],
    ]);
	
	var cpColor = (trade.currentlyProfitable) ? "#468847" : "#B94A48" ;
	new google.visualization.LineChart(document.getElementById(id)).draw(data, {
		curveType: "none",
		height: 350,
		lineWidth: 5,
		colors: ["#222222"],
		legend: {position: "none"},
		chartArea: {
			top: 10,
			height: "80%"
		},
		hAxis: {
			title: "Price at Expiration",
			baseline: trade.currentPrice,
			baselineColor: cpColor
		},
		vAxis: {
			viewWindow: {max: Math.max(trade.higherAmount, trade.lowerAmount) * 1.25},
			format: "$#",
			title: "Profit / Loss",
			baseline: 0
		}
	});
	increaseBaselineWidth(cpColor);
}

function increaseBaselineWidth(fillColor) {
	$("rect").each(function () {
		var fill = $(this).attr("fill");
		if (fill && fill.toUpperCase() == fillColor.toUpperCase()) {
			$(this).attr("width", 3);
		}
	});
}

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