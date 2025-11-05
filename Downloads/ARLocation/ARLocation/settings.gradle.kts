pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // JitPack은 필요하면 여기에 추가할 수 있습니다.
        // maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 필요시 JitPack 추가 가능:
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ARLocationQR"
include(":app")

// 로컬로 포함한 모듈이 있다면 정확히 필요한 모듈만 include 하세요.
// 예: include(":sceneview-android") 등 — 다만 로컬 모듈을 include 했다면
// sceneview-android의 각 하위 모듈(예: :sceneview-core, :arsceneview 등)을 settings에 맞게 include 해야 합니다.
// 현재 원칙: 원격 artifact(io.github.sceneview:sceneview / arsceneview)를 사용하도록 구성합니다.