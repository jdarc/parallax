plugins { application }

repositories { mavenCentral() }

dependencies { implementation(project(":engine")) }

application { mainClass.set("demo.runaround.ProgramKt") }
