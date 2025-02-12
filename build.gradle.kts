plugins {
    kotlin("jvm")  // Не указываем версию
}

dependencies {
    implementation(kotlin("stdlib"))
}

repositories {
    mavenCentral()  // Это основной репозиторий для Kotlin и других зависимостей
    google()        // Если используете Android, также добавьте Google репозиторий
}
