# 1) Cambios en Gradle

## build.gradle.kts (nivel app) — plugins
    plugins {
        // ...los que ya tienes (android.application, kotlin.android, kotlin.compose)
        id("com.google.devtools.ksp") version "TU_VERSION_KOTLIN-1.0.xx"
    }
La versión de KSP debe empezar igual que tu versión de Kotlin
(ej. Kotlin 2.0.21 -> ksp "2.0.21-1.0.28"). Mira la versión de Kotlin en
libs.versions.toml o en el build.gradle.kts de la raíz.

## dependencies
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // navigation-compose ya lo tienes

# 2) Ícono personalizado
Android Studio -> clic derecho en res -> New -> Image Asset -> Launcher Icons
(Adaptive and Legacy). Usa tu logo de EventPro (fondo rosa, texto/imagen de evento).
Verifica que AndroidManifest tenga android:icon="@mipmap/ic_launcher".

# 3) APK
Build -> Build Bundle(s)/APK(s) -> Build APK(s). Sube el .apk a Classroom.

# 4) Texto sugerido para la entrega (Classroom)
Estructura: data/ (Entities, Daos, EventDatabase, EventRepository), viewmodel/ (EventViewModel),
ui/screens + ui/components (Compose), navigation/ (NavHost + barra inferior). Room con 3 tablas:
services, reservations y reservation_services (relación muchos a muchos).

Funcionalidades nuevas y cómo probarlas:
1. CRUD de servicios: Servicios -> "+ Nuevo", llenar y guardar; "Editar" para cambiar precio; "Eliminar" con confirmación.
2. Registro de reservas: Reservas -> "+ Nueva", completar datos (fecha DD/MM/AAAA), elegir servicios y registrar.
3. Consulta de reservas guardadas: Reservas lista todo desde Room, con servicios asociados y total.
4. Edición de reservas: en una reserva, "Editar", cambiar invitados/servicios y guardar (el total se recalcula).
5. Cambio de estado y eliminación: "Confirmar" / "Cancelar" / "Eliminar" en cada reserva.
6. Búsqueda y filtro por estado: en Reservas, escribir un cliente o usar los chips Pendiente/Confirmada/Cancelada.
7. Estadísticas: pestaña Estadísticas (totales por estado, ingresos confirmados, reservas por tipo).
Persistencia: crear datos, cerrar la app por completo y reabrirla; los datos siguen ahí.
