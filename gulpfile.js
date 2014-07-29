var gulp = require('gulp'), //
    //coffee = require('gulp-coffee'); //
    uglify = require('gulp-uglify');

gulp.task('js', function(){
    return gulp.src('./src/js/test/*.js')
        //.pipe(coffee())
        .pipe(uglify())
        .pipe(gulp.dest('./resources/public/js/'));
});

gulp.task('watch', function(){
    gulp.watch('./src/js/test/*.js', ['js']);
});