plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14-SNAPSHOT" apply false
}

stonecutter active "1.21.11"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string {
        direction = eval(current.version, "> 1.21.5")
        replace("// moj_import <", "//!moj_import <")
    }

    filters.include("**/*.fsh", "**/*.vsh")
}
