 /*jshint unused:false */

/***************
 
 Will delete it soon.

* @author Wei Ding
* @version 0.1.0
***************/
/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2015> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/

'use strict';

var gulp = require('gulp');
var gutil = require('gulp-util');
var argv = require('yargs').argv;
var map = require('map-stream');
var rename = require('gulp-rename');
var traverse = require('traverse');
var translate = require('yandex-translate');
var transform = require('vinyl-transform');
var jsonFormat = require('gulp-json-format');

var paths = gulp.paths;

gulp.task('translate', function () {
  var translateFile = transform(function(filename) {
    return map(function(data, done) {
      var j = JSON.parse(data);
      var translateCount = 0;
      var appTranslated = traverse(j).forEach(function(x) {
        if(typeof x !== 'object') {
          var self = this;
          translateCount++;
          translate(x, { to: argv.to }, function(err, res) {
            self.update(res.text.toString());
            translateCount--;
            if(translateCount === 0) {
              var finishedJSON = JSON.stringify(appTranslated);
              gutil.log(gutil.colors.green('Translated ' + filename));
              done(null, finishedJSON);
            }
          });
        }
      });
    })
  });

  // make sure we have a from and to language
  if(argv.from !== undefined && argv.to !== undefined) {
    return gulp.src([
      paths.src + '/app/**/il8n/' + argv.from + '.json',
    ])
    .pipe(translateFile)
    .pipe(jsonFormat(4))
    .pipe(rename({
      basename: argv.to,
    }))
    .pipe(gulp.dest(paths.src + '/app'));
  }
  else {
    gutil.log(gutil.colors.red('Need to specify 2 lanuages e.g. translate --from en --to fr <-- translate en json files to French'));
  }
});
