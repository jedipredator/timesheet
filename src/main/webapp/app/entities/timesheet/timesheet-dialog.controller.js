(function() {
    'use strict';

    angular
        .module('timeSheetApp')
        .controller('TimesheetDialogController', TimesheetDialogController);

    TimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Timesheet', 'Project', 'Employee'];

    function TimesheetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Timesheet, Project, Employee) {
        var vm = this;

        vm.timesheet = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.projects = Project.query();
        vm.employees = Employee.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.timesheet.id !== null) {
                Timesheet.update(vm.timesheet, onSaveSuccess, onSaveError);
            } else {
                Timesheet.save(vm.timesheet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timeSheetApp:timesheetUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
