(function() {
    'use strict';

    angular
        .module('timeSheetApp')
        .controller('TimesheetController', TimesheetController);

    TimesheetController.$inject = ['$scope', '$state', 'Timesheet'];

    function TimesheetController ($scope, $state, Timesheet) {
        var vm = this;
        
        vm.timesheets = [];

        loadAll();

        function loadAll() {
            Timesheet.query(function(result) {
                vm.timesheets = result;
            });
        }
    }
})();
