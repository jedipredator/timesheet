(function() {
    'use strict';

    angular
        .module('timeSheetApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('timesheet', {
            parent: 'entity',
            url: '/timesheet',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'timeSheetApp.timesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/timesheet/timesheets.html',
                    controller: 'TimesheetController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('timesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('timesheet-detail', {
            parent: 'entity',
            url: '/timesheet/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'timeSheetApp.timesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/timesheet/timesheet-detail.html',
                    controller: 'TimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('timesheet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Timesheet', function($stateParams, Timesheet) {
                    return Timesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('timesheet-detail.edit', {
            parent: 'timesheet-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/timesheet/timesheet-dialog.html',
                    controller: 'TimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Timesheet', function(Timesheet) {
                            return Timesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('timesheet.new', {
            parent: 'timesheet',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/timesheet/timesheet-dialog.html',
                    controller: 'TimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                approved: null,
                                hours: null,
                                date: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('timesheet', null, { reload: 'timesheet' });
                }, function() {
                    $state.go('timesheet');
                });
            }]
        })
        .state('timesheet.edit', {
            parent: 'timesheet',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/timesheet/timesheet-dialog.html',
                    controller: 'TimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Timesheet', function(Timesheet) {
                            return Timesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('timesheet', null, { reload: 'timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('timesheet.delete', {
            parent: 'timesheet',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/timesheet/timesheet-delete-dialog.html',
                    controller: 'TimesheetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Timesheet', function(Timesheet) {
                            return Timesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('timesheet', null, { reload: 'timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
