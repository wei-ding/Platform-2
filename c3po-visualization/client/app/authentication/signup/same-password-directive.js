'use strict';

/**
* @ngdoc directive
* @name samePassword
* @restrict A
* @scope
*
* @description
* `samePassword` is a directive with the purpose to validate a password input based on the value of another input.
* When both input values are the same the inputs will be set to valid
*
* @usage
* ```html
* <form name="signup">
*     <input name="password" type="password" ng-model="user.password" same-password="signup.confirm" />
*     <input name="confirm" type="password" ng-model="user.confirm" same-password="signup.confirm" />
* </form>
* ```
* @author Wei Ding
* @version 0.1.0
**/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2015> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
/*
   replace it soon.  
*/
angular.module('c-3poAuthentication').directive('samePassword', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            samePassword: '='
        },
        link: function(scope, element, attrs, ngModel) {
            ngModel.$viewChangeListeners.push(function() {
                ngModel.$setValidity('samePassword', scope.samePassword.$modelValue === ngModel.$modelValue);
                scope.samePassword.$setValidity('samePassword', scope.samePassword.$modelValue === ngModel.$modelValue);
            });
        }
    };
});
