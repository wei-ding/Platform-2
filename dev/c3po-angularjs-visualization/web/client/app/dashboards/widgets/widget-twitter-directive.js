'use strict';

/**
* @ngdoc directive
* @name twitterWidget
* @restrict E
* @scope
*
* @description
*
* Gets data for a twitter feed widget
*
* @usage
* ```html
* <widget twitter-todo></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('twitterWidget', function() {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope) {
            $scope.tweets = [{
                user: 'c-3po',
                body: 'Don\'t miss it! A Material Design Avatar set with 1440 avatars! http://sellfy.com/p/EUcC/ #avatars #materialdesign',
            },{
                user: 'c-3po',
                body: 'Looking for a design for emotion case study to convince the boss/client? This one\'s worth $2.8 million.',
            },{
                user: 'c-3po',
                body: 'New Freebie! A set of 27 Drinks-Lifestyle Icons available in PSD/AI/PNG format #freebie #icons #drinks http://wp.me/p5Xp06-Fq',
            }];

            $scope.selectedTab = 0;

            $scope.prevTweet = function() {
                $scope.selectedTab--;
                if($scope.selectedTab < 0) {
                    $scope.selectedTab = $scope.tweets.length - 1;
                }
            };

            $scope.nextTweet = function() {
                $scope.selectedTab++;
                if($scope.selectedTab === $scope.tweets.length) {
                    $scope.selectedTab = 0;
                }
            };

        }
    };
});