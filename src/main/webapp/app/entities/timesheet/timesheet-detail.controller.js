(function() {
    'use strict';

    angular
        .module('timeSheetApp')
        .controller('TimesheetDetailController', TimesheetDetailController);

    TimesheetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Timesheet', 'Project', 'Employee'];

    function TimesheetDetailController($scope, $rootScope, $stateParams, previousState, entity, Timesheet, Project, Employee) {
        var vm = this;

        vm.timesheet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('timeSheetApp:timesheetUpdate', function(event, result) {
            vm.timesheet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
