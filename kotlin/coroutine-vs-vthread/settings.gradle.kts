rootProject.name = "coroutine-vs-vthread"

include(":shared")
include(":scenarios:sleep-io")
include(":scenarios:http-io")
include(":scenarios:db-io")
include(":scenarios:cpu-bound")
include(":scenarios:fanout-fanin")
include(":scenarios:cancellation")
