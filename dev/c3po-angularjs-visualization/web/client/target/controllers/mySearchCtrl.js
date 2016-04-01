app.controller('search', function ($scope, $filter,$http,$log,url) {

	$http.get(url+'ang/MySearch/json/data').success(function(data){
		$scope.URL=url;
		$scope.sort = {       
				sortingOrder : 'searchStartTime',
				reverse : true
		};

		$scope.gap = 5;

		$scope.filteredItems = [];
		$scope.groupedItems = [];
		$scope.itemsPerPage = 10;
		$scope.pagedItems = [];
		$scope.currentPage = 0;
		$scope.data1=data;
		$scope.totalItems=Object.keys(data).length;
		$scope.numPages=Math.ceil($scope.totalItems/ $scope.itemsPerPage);
		console.log("maxsize"+$scope.totalItems);
		var searchMatch = function (haystack, needle) {
			if (!needle) {
				return true;
			}
			return haystack.toLowerCase().indexOf(needle.toLowerCase()) !== -1;
		};

		// init the filtered items
		$scope.search = function () {
			$scope.filteredItems = $filter('filter')($scope.data1, function (item) {
				for(var attr in item) {
					if (searchMatch(item[attr], $scope.query))
						return true;
				}
				return false;
			});
			// take care of the sorting order
			if ($scope.sort.sortingOrder !== '') {
				$scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sort.sortingOrder, $scope.sort.reverse);
			}
			
			// now group by pages
			$scope.groupToPages();
		};
			// calculate page in place
			$scope.groupToPages = function () {
			$scope.pagedItems = $scope.filteredItems;
			console.log('sdfgvf'+$scope.filteredItems);

		};
		// functions have been describe process the data for display
		$scope.search();
		return data;
	}).error(function(data){
		console.log("failed to retrive");});



});

app.$inject = ['$scope', '$filter'];

app.directive("customSort", function() {
	return {
		restrict: 'A',
		transclude: true,    
		scope: {
		order: '=',
		sort: '='
	},
	template : 
		' <a ng-click="sort_by(order)" style="color: #555555;">'+
		'    <span ng-transclude></span>'+
		'    <i ng-class="selectedCls(order)"></i>'+
		'</a>',
		link: function(scope) {

		// change sorting order
		scope.sort_by = function(newSortingOrder) {       
			var sort = scope.sort;

			if (sort.sortingOrder == newSortingOrder){
				sort.reverse = !sort.reverse;
			}                    

			sort.sortingOrder = newSortingOrder;        
		};


		scope.selectedCls = function(column) {
			if(column == scope.sort.sortingOrder){
				return ('icon-chevron-' + ((scope.sort.reverse) ? 'down' : 'up'));
			}
			else{            
				return'icon-sort' 
			} 
		};      
	}// end link
	}
});
