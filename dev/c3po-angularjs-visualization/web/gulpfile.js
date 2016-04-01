'use strict';

var gulp = require('gulp');

gulp.paths = {
  src:  'client',
  dist: 'dist',
  tmp:  '.tmp',
  e2e:  'e2e'
};

require('require-dir')('./gulp');

gulp.task('build', ['clean'], function () {
    gulp.start('buildapp');
});
