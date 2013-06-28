({
    cssIn: "${project.basedir}/src/main/webapp/assets/css/styles.css",
    out: "${project.build.webapp}/assets/css/styles.min.css",

    // CSS optimization options are:
    //  - "standard": @import inlining, comment removal and line returns.
    //  - "standard.keepLines": like "standard" but keeps line returns.
    //  - "standard.keepComments": keeps the file comments, but removes line returns.
    //  - "standard.keepComments.keepLines": keeps the file comments and line returns.
    //  - "none": skip CSS optimizations.
    optimizeCss: "standard"
})
