rootProject.name = "siotlab"

recursiveInclude("services")

// projectDir 를 프로젝트로 포함한 뒤 이 프로젝트를 기준 하위 디렉토리에
// *.gradle* 파일이 존재한다면, 대상 폴더를 및 하위 폴더를 프로젝트로 포함한다.
fun recursiveInclude(vararg dirNames: String) = dirNames.forEach { dirName ->
    val projectDir = file(dirName)
    println(findSubProjectPaths(projectDir))

    include(*(findSubProjectPaths(projectDir).toTypedArray()))
}

// *.gradle* 파일명을 기준으로 프로젝트 패스를 검색한다.
fun findSubProjectPaths(projectDir: File, pathPrefix: String = "", depth: Int = 0): List<String> {
    if (projectDir.isDirectory.not()) return emptyList()
    val files = projectDir.listFiles() ?: return emptyList()

    val noScripts = depth != 0 && files.map(File::getName)
        .find { ".gradle" in it } == null
    if (noScripts) return emptyList()

    val projectPath = "$pathPrefix${projectDir.name}"
    return listOf(projectPath) + files.map { findSubProjectPaths(it, "$projectPath:", depth + 1) }.flatten()
}