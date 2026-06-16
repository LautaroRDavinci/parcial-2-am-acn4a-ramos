# Fit-Routine 💪

Aplicación Android para anotar y gestionar tu rutina de ejercicios del día.

La idea es simple: entrás, gestionás tu rutina diaria de ejercicios, los vas completando y hacés un seguimiento de tu progreso semanal. Por ahora no guarda los datos de manera permanente (eso vendría en una próxima versión con una base de datos local).

---

## ¿Qué puede hacer la app?

### Versión 0.5 (Base)
- Escribís el nombre de un ejercicio en el campo de texto y lo agregás a la lista con el botón.
- Si intentás agregar algo vacío, te avisa con un mensaje.
- Tocás cualquier ejercicio de la lista y se marca como completado (aparece el ✔ y cambia de color).
- Si lo tocás de nuevo, se desmarca.
- Tiene una barra de navegación abajo con tres opciones: Rutina, Progreso y Perfil (las últimas dos mostraban "Próximamente").

### Versión 1.0 (Nueva Versión)
- **Carga de Rutinas Sugeridas**: Tenés un menú colapsable para cargar rutinas completas de Tren Superior, Tren Inferior o Core con un solo toque.
- **Detalle de Ejercicios**: Hacés click en un ejercicio de la lista para ver su descripción, series, repeticiones y una foto real.
- **Edición y Eliminación**: Dentro de la pantalla de detalle, podés modificar cualquier dato del ejercicio (incluso cambiar su imagen por URL) o directamente eliminarlo de tu rutina.
- **Check Completo**: Cada tarjeta de ejercicio tiene su propio botón de ✔ para marcarlo como hecho de forma rápida.
- **Contador de Entrenamientos**: Cuando completás todos los ejercicios del día, la app te felicita con un cartel y te permite ir directo a la pantalla de progreso.
- **Objetivo Semanal**: En la pantalla de Progreso podés ver cuántos entrenamientos completaste en la semana. Si llegás a 4, salta un cartel de felicitación que reinicia la semana a 0 automáticamente.
- **Reinicio Limpio**: El botón "Reiniciar progreso" en la sección de progreso elimina los ejercicios del día para empezar una rutina nueva, pero mantiene intacto tu contador de entrenamientos completados.
- **Perfil Editable**: En la pantalla de Perfil podés editar tu Nombre de usuario, Objetivo y Nivel en la misma pantalla sin cambiar de actividad, con validación de campos.
- **Estilo Gym Blanco y Rojo**: Rediseñamos toda la app con una tipografía deportiva condensada y una paleta de colores limpia en blanco y rojo, unificando los botones inferiores.

---

## Tecnologías que usamos

- **Java 11** — lenguaje principal del proyecto
- **Android SDK 36** (Android 15 como target, mínimo Android 7.0)
- **Material Design 3** — para los estilos y componentes visuales
- **ConstraintLayout** — para organizar los elementos en pantalla
- **Gradle con Kotlin DSL** — sistema de construcción del proyecto

---

## Estructura del proyecto

```
fit-routine/
├── app/src/main/
│   ├── assets/
│   │   └── suggested_routines.json   ← JSON con los ejercicios sugeridos y fotos reales
│   ├── java/com/example/fit_routine/
│   │   ├── MainActivity.java        ← lógica principal y de la rutina diaria
│   │   ├── ExerciseDetailActivity.java ← detalle, edición y eliminación de ejercicios
│   │   ├── ProgressActivity.java    ← contador de entrenamientos y estadísticas
│   │   ├── ProfileActivity.java     ← visualización y edición del perfil
│   │   └── models/
│   │       └── Exercise.java        ← modelo de datos de un ejercicio
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml    ← diseño de la pantalla de inicio
│   │   │   ├── activity_exercise_detail.xml ← diseño del detalle de ejercicio
│   │   │   ├── activity_progress.xml ← diseño de la pantalla de progreso
│   │   │   └── activity_profile.xml  ← diseño de la pantalla de perfil
│   │   └── values/
│   │       ├── strings.xml          ← todos los textos e internacionalización
│   │       ├── colors.xml           ← paleta de colores (Blanco/Rojo Gym)
│   │       └── themes.xml           ← estilos, temas y tipografía global
│   └── AndroidManifest.xml          ← configuración general y permisos de red
```

---

## Descripción de los archivos principales

### AndroidManifest.xml
Definimos que `MainActivity` es la pantalla inicial de la app. También habilitamos el permiso de Internet y tráfico plano (`usesCleartextTraffic`) para que la app pueda descargar las imágenes reales de los ejercicios de manera dinámica.

### MainActivity.java
Es la pantalla de la rutina diaria. Administra la lista en memoria, carga las rutinas sugeridas desde el archivo JSON, permite agregar ejercicios manualmente y detecta cuándo completaste todos los ejercicios para mostrarte el cartel de felicitación diaria y llevarte a Progreso.

### ExerciseDetailActivity.java
Muestra la información de un ejercicio. Permite al usuario editar los datos (nombre, descripción, series, reps, URL de imagen) o borrar el ejercicio. Utiliza un hilo secundario para descargar imágenes de Internet de forma segura.

### ProgressActivity.java
Calcula el porcentaje de progreso de tu rutina actual y lleva la cuenta de tus entrenamientos semanales. Si tenés 4 completados, te muestra el cartel de meta cumplida. Su botón "Reiniciar progreso" le avisa a `MainActivity` que limpie la lista del día, pero guarda tu record semanal.

### ProfileActivity.java
Controla la sección de perfil. Contiene un modo de visualización y un modo de edición alternables en pantalla. Valida que no dejes campos vacíos al editar y guarda los datos de vuelta en `MainActivity`.

### activity_main.xml / activity_exercise_detail.xml / activity_progress.xml / activity_profile.xml
Archivos de diseño XML que estructuran las pantallas de la aplicación de manera organizada mediante contenedores y botones estilizados.

### strings.xml
Centraliza todas las cadenas de texto de la aplicación para facilitar futuras traducciones y mantener el código ordenado.

### colors.xml
Define los colores de la aplicación, ahora unificados bajo un estilo de gimnasio en rojo y blanco.

### themes.xml
Define los estilos y estilos de botones. Aquí forzamos la tipografía condensada a nivel global de la app para darle un look deportivo moderno.

---

## Cómo corre la app

### Flujo en la Versión 0.5
1. El usuario abre la app → se inicia `MainActivity` con ejercicios precargados.
2. El ejercicio se marca tocándolo directamente en la lista.
3. Los botones de progreso y perfil muestran un aviso de "Próximamente" al hacer click.
4. Al cerrar la app todo se pierde.

### Flujo en la Versión 1.0 (Actual)
1. Abrís la app → la lista de ejercicios empieza vacía.
2. Podés escribir un ejercicio y agregarlo, o abrir la sección "Rutina sugerida" y elegir una (Tren Superior, Tren Inferior o Core) para cargar ejercicios con fotos reales.
3. Tocando el ✔ de cada tarjeta, marcás los ejercicios realizados. Al completarlos todos, salta el cartel para ir a Progreso.
4. Si tocás un ejercicio (en la tarjeta), vas al detalle. Ahí podés editar sus series, repeticiones, descripción o imagen, y también podés eliminar el ejercicio si no lo querés hacer.
5. En la sección **Progreso** (abajo a la izquierda), ves tus números, el porcentaje de avance, y el contador semanal. Si llegás a 4 entrenamientos completados, salta el cartel semanal y se resetea a 0. Si tocás "Reiniciar progreso", vaciás la rutina diaria.
6. En la sección **Perfil** (abajo a la derecha), podés presionar "Editar" para cambiar tu nombre, tu objetivo y tu nivel, y los cambios se quedan guardados en memoria mientras usás la app.

---

## Lo que falta / ideas para después

- **Persistencia de datos**: guardar los ejercicios y el perfil con Room Database o SharedPreferences para que no se pierdan al cerrar o apagar el celular.
- **RecyclerView**: optimizar el listado dinámico de ejercicios para listas muy largas.
- **Notificaciones**: recordatorios diarios para hacer ejercicio.

---

## Control de Versiones

El proyecto se gestionó con **Git** y se subió a **GitHub** para trabajar en equipo.
Los integrantes del grupo realizamos commits convencionales a lo largo del desarrollo, lo que permite ver en el historial la evolución del proyecto desde el layout inicial hasta la versión 1.0 actual.
