'use strict';

/**
* Handles the main admin sidemenu
*
* @usage
* ```html
* <side-menu></side-menu>
* ```
* @author Wei Ding
* @version 0.1.0
*/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2014> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
angular.module('c-3po')
.service('SideMenu', function() {
    var menu = [];    

    function traverse(obj, callback, depth) {
        depth = depth === undefined ? -1 : depth;

        if (obj instanceof Array) {
            depth++;
            for (var i = 0; i < obj.length; i++) {
                traverse(obj[i], callback, depth);
            }
        } else {
            callback(obj, depth);
            if(obj.children !== undefined) {
                traverse(obj.children, callback, depth);
            }
        }
    }

    var service = {
        addMenu: function(item) {
            menu.push(item);
        },
        getMenu: function() {
            return menu;
        },
        traverseMenu: function(callback) {
            traverse(menu, callback);
        },
        getPath: function() {
            var path = [];
            service.traverseMenu(function(item) {
                if(item.active) {
                    path.push(item);
                }
            });
            return path;
        }
    };

    return service;
});
