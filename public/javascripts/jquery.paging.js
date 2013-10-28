/* @author Chun Lin (GCL Project)
 * @name oneSimpleTablePagination
 * @type jQuery
 * Modified by Matthew Frost to work properly with jquery.tablesorter
 * */

$.prototype.extend(
	{
		"oneSimpleTablePagination": function(userConfigurations) {
			var defaults = {
				rowsPerPage : 10,
				topNav : false
			};
			defaults = $.extend(defaults, userConfigurations);
			
			return this.each(function() {
				var table = $(this)[0];
				var currPageId = "#tablePaginationCurrPage";
				var tblLocation = (defaults.topNav) ? "prev" : "next";
				var tableRows = $.makeArray($("tbody tr", table));
				var totalPages = countNumberOfPages(tableRows.length);
				var currPageNumber = 1;		  
		  
				function hideOtherPages(pageNum) {
					var intRegex = /^\d+$/;
					if (!intRegex.test(pageNum) || pageNum < 1 || pageNum > totalPages)
						return;
					var startIndex = (pageNum - 1) * defaults.rowsPerPage;
					var endIndex = (startIndex + defaults.rowsPerPage - 1);
					$(tableRows).show();
					for (var i = 0; i < tableRows.length; i++) {
						if (i < startIndex || i > endIndex) {
							$(tableRows[i]).hide();
						}
					}
				}
		  
				function countNumberOfPages(numRows) {
					var preTotalPages = Math.round(numRows / defaults.rowsPerPage);
					var totalPages = (preTotalPages * defaults.rowsPerPage < numRows) ? preTotalPages + 1 : preTotalPages;
					return totalPages;
				}
		  
				function resetCurrentPage(currPageNum) {
					tableRows = $.makeArray($("tbody tr", table));
					var intRegex = /^\d+$/;
					if (!intRegex.test(currPageNum) || currPageNum < 1 || currPageNum > totalPages)
						return;
					currPageNumber = currPageNum;
					hideOtherPages(currPageNumber);
					$(table)[tblLocation]().find(currPageId).val(currPageNumber);
				}
		  
				function createPaginationElements() {
					var paginationHTML = "";
					paginationHTML += "<div id='tablePagination'>";
					paginationHTML += "<button id='tablePaginationFirstPage' class='btn btn-primary'>First</button>";
					paginationHTML += "<button id='tablePaginationPrevPage' class='btn btn-primary'><span class='glyphicon glyphicon-chevron-left'></span></button>";
					paginationHTML += "<span id='tablePaginationPageCounter'>Page";
					paginationHTML += "<input id='tablePaginationCurrPage' type='number' class='form-control' value='"+currPageNumber+"' size='1'>";
					paginationHTML += "of " + totalPages + "</span>";
					paginationHTML += "<button id='tablePaginationNextPage' class='btn btn-primary'><span class='glyphicon glyphicon-chevron-right'></span></button>";
					paginationHTML += "<button id='tablePaginationLastPage' class='btn btn-primary'>Last</button>";
					paginationHTML += "</div>";
					return paginationHTML;
				}
		  
				$(this).before("<style type='text/css'>a.button {color: #023042;font: bold 12px Helvetica, Arial, sans-serif;text-decoration: none;padding: 7px 12px;position: relative;display: inline-block;text-shadow: 0 1px 0 #fff;-webkit-transition: border-color .218s;-moz-transition: border .218s;-o-transition: border-color .218s;transition: border-color .218s;background: #99CCFF;background: -webkit-gradient(linear,0% 40%,0% 70%,from(#38C1F9),to(#C6EDFD));background: -moz-linear-gradient(linear,0% 40%,0% 70%,from(#38C1F9),to(#C6EDFD));border: solid 1px #023042;border-radius: 2px;-webkit-border-radius: 2px;-moz-border-radius: 2px;margin-right: 10px;}a.button:hover {color: #247FCA;border-color: #247FCA;-moz-box-shadow: 0 2px 0 rgba(0, 0, 0, 0.2) -webkit-box-shadow:0 2px 5px rgba(0, 0, 0, 0.2);box-shadow: 0 1px 2px rgba(0, 0, 0, 0.15);}a.button:active {color: #000;border-color: #444;}a.left {-webkit-border-top-right-radius: 0;-moz-border-radius-topright: 0;border-top-right-radius: 0;-webkit-border-bottom-right-radius: 0;-moz-border-radius-bottomright: 0;border-bottom-right-radius: 0;margin: 0;}a.right:hover { border-left: solid 1px #999 }a.right {-webkit-border-top-left-radius: 0;-moz-border-radius-topleft: 0;border-top-left-radius: 0;-webkit-border-bottom-left-radius: 0;-moz-border-radius-bottomleft: 0;border-bottom-left-radius: 0;border-left: solid 1px #f3f3f3;border-left: solid 1px rgba(255, 255, 255, 0);}</style>");
				
				if (defaults.topNav) {
					$(this).before(createPaginationElements());
				} else {
					$(this).after(createPaginationElements());
				}
		  
				hideOtherPages(currPageNumber);
				
				window.addEventListener("sortComplete", sorted, false);
				
				function sorted() {
					setTimeout(function() {
						resetCurrentPage(1);
					}, 1);
				}
				
				$(table)[tblLocation]().find("#tablePaginationFirstPage").click(function (e) {
					resetCurrentPage(1);
			  	});
			  
			  	$(table)[tblLocation]().find("#tablePaginationPrevPage").click(function (e) {
					resetCurrentPage(parseInt(currPageNumber) - 1);
			  	});
			  
			  	$(table)[tblLocation]().find("#tablePaginationNextPage").click(function (e) {
					resetCurrentPage(parseInt(currPageNumber) + 1);
			  	});
			  
			  	$(table)[tblLocation]().find("#tablePaginationLastPage").click(function (e) {
					resetCurrentPage(totalPages);
			  	});
		  
				$(table)[tblLocation]().find(currPageId).on("change", function (e) {
					resetCurrentPage(this.value);
				});
		  
			})
		}
	})