# Fit-Routine 💪

Aplicación Android para anotar y gestionar tu rutina de ejercicios del día.

La idea es simple: entrás con tu cuenta, gestionás tu rutina diaria de ejercicios, los vas completando y hacés un seguimiento de tu progreso semanal. Desde la versión 2.0 todo queda guardado en la nube, así que podés cerrar la app o cambiar de celular y tu rutina sigue ahí.

---

## ¿Qué puede hacer la app?

### Versión 0.5 (Base)
- Escribís el nombre de un ejercicio en el campo de texto y lo agregás a la lista con el botón.
- Si intentás agregar algo vacío, te avisa con un mensaje.
- Tocás cualquier ejercicio de la lista y se marca como completado (aparece el ✔ y cambia de color).
- Si lo tocás de nuevo, se desmarca.
- Tiene una barra de navegación abajo con tres opciones: Rutina, Progreso y Perfil (las últimas dos mostraban "Próximamente").

### Versión 1.0
- **Carga de Rutinas Sugeridas**: Tenés un menú colapsable para cargar rutinas completas de Tren Superior, Tren Inferior o Core con un solo toque.
- **Detalle de Ejercicios**: Hacés click en un ejercicio de la lista para ver su descripción, series, repeticiones y una foto real.
- **Edición y Eliminación**: Dentro de la pantalla de detalle, podés modificar cualquier dato del ejercicio (incluso cambiar su imagen por URL) o directamente eliminarlo de tu rutina.
- **Check Completo**: Cada tarjeta de ejercicio tiene su propio botón de ✔ para marcarlo como hecho de forma rápida.
- **Contador de Entrenamientos**: Cuando completás todos los ejercicios del día, la app te felicita con un cartel y te permite ir directo a la pantalla de progreso.
- **Objetivo Semanal**: En la pantalla de Progreso podés ver cuántos entrenamientos completaste en la semana. Si llegás a 4, salta un cartel de felicitación que reinicia la semana a 0 automáticamente.
- **Reinicio Limpio**: El botón "Reiniciar progreso" elimina los ejercicios del día para empezar una rutina nueva, pero mantiene intacto tu contador de entrenamientos completados.
- **Perfil Editable**: En la pantalla de Perfil podés editar tu Nombre de usuario, Objetivo y Nivel en la misma pantalla sin cambiar de actividad, con validación de campos.
- **Estilo Gym Blanco y Rojo**: Rediseñamos toda la app con una tipografía deportiva condensada y una paleta de colores limpia en blanco y rojo, unificando los botones inferiores.

### Versión 2.0 (Nueva Versión)
- **Cuentas de usuario**: La app arranca en una pantalla de login. Podés crear una cuenta con tu correo y contraseña, o entrar directo con tu cuenta de Google.
- **Registro con nombre**: Al crear la cuenta te pedimos tu nombre, y ese es el que después ves en tu perfil. Antes estaba fijo en el código.
- **Sesión que se acuerda de vos**: Si ya entraste una vez, la próxima vez que abrís la app te saltea el login.
- **Cerrar sesión**: Desde la pantalla de Perfil, con un botón que te devuelve al login.
- **Tu rutina se guarda en la nube**: Los ejercicios que cargás, los que tildás y los que editás quedan en Firestore. Cerrás la app, la volvés a abrir, y está todo como lo dejaste.
- **Tu perfil también**: Nombre, objetivo, nivel y el contador de entrenamientos semanales viajan con tu cuenta.
- **Cada uno ve lo suyo**: Las reglas de seguridad de Firestore hacen que un usuario solo pueda leer y escribir sus propios datos.
- **Imágenes más rápidas**: Cambiamos la descarga manual de fotos por Glide, que las guarda en caché y no las vuelve a bajar cada vez que entrás al detalle.

---

## Tecnologías que usamos

- **Java 11** — lenguaje principal del proyecto
- **Android SDK 36** (Android 15 como target, mínimo Android 7.0)
- **Firebase Authentication** — login con correo/contraseña y con Google
- **Cloud Firestore** — base de datos en la nube para el perfil y la rutina
- **Glide** — descarga y caché de las imágenes de los ejercicios
- **Material Design 3** — para los estilos y componentes visuales
- **ConstraintLayout** — para organizar los elementos en pantalla
- **Gradle con Kotlin DSL** — sistema de construcción del proyecto

---

## Estructura del proyecto

```
fit-routine/
├── app/
│   ├── google-services.json              ← configuración del proyecto de Firebase
│   └── src/main/
│       ├── assets/
│       │   └── suggested_routines.json   ← JSON con los ejercicios sugeridos y fotos reales
│       ├── java/com/example/fit_routine/
│       │   ├── LoginActivity.java        ← ingreso con correo o con Google
│       │   ├── RegisterActivity.java     ← creación de cuenta
│       │   ├── MainActivity.java         ← lógica principal y de la rutina diaria
│       │   ├── ExerciseDetailActivity.java ← detalle, edición y eliminación de ejercicios
│       │   ├── ProgressActivity.java     ← contador de entrenamientos y estadísticas
│       │   ├── ProfileActivity.java      ← visualización, edición del perfil y cierre de sesión
│       │   ├── data/
│       │   │   └── UserRepository.java   ← todo el acceso a Firestore vive acá
│       │   └── models/
│       │       ├── Exercise.java         ← modelo de datos de un ejercicio
│       │       └── UserProfile.java      ← modelo de datos del perfil
│       ├── res/
│       │   ├── drawable/
│       │   │   ├── ic_google.xml         ← logo de Google como vector
│       │   │   └── bg_exercise_placeholder.xml ← fondo mientras carga la foto
│       │   ├── layout/
│       │   │   ├── activity_login.xml
│       │   │   ├── activity_register.xml
│       │   │   ├── activity_main.xml
│       │   │   ├── activity_exercise_detail.xml
│       │   │   ├── activity_progress.xml
│       │   │   └── activity_profile.xml
│       │   └── values/
│       │       ├── strings.xml           ← todos los textos de la app
│       │       ├── colors.xml            ← paleta de colores (Blanco/Rojo Gym)
│       │       ├── dimens.xml            ← márgenes, tamaños de fuente y medidas
│       │       └── themes.xml            ← estilos, temas y tipografía global
│       └── AndroidManifest.xml           ← configuración general y permisos de red
```

---

## Descripción de los archivos principales

### AndroidManifest.xml
Definimos que `LoginActivity` es la pantalla inicial de la app. También habilitamos el permiso de Internet y tráfico plano (`usesCleartextTraffic`) para que la app pueda descargar las imágenes reales de los ejercicios de manera dinámica.

### LoginActivity.java
Es la primera pantalla. Valida que no dejes campos vacíos y entra con Firebase Authentication. En `onStart` revisa si ya hay una sesión abierta: si la hay, te manda directo a la rutina sin mostrarte nada. También maneja el ingreso con Google, que devuelve un token que después le pasamos a Firebase para armar la sesión.

### RegisterActivity.java
Crea la cuenta. Antes de llamar a Firebase revisa que los campos estén completos, que la contraseña tenga al menos 6 caracteres y que las dos contraseñas coincidan. Cuando la cuenta se crea, guarda el nombre que escribiste en el perfil de la cuenta.

### MainActivity.java
Es la pantalla de la rutina diaria. Al abrirse trae de Firestore tu perfil y la rutina que tenías guardada. Administra la lista, carga las rutinas sugeridas desde el archivo JSON, permite agregar ejercicios manualmente y detecta cuándo completaste todos para mostrarte el cartel de felicitación. Cada cambio que hacés lo escribe en la base.

### ExerciseDetailActivity.java
Muestra la información de un ejercicio. Permite editar los datos (nombre, descripción, series, reps, URL de imagen) o borrar el ejercicio. Usa Glide para bajar la foto, con un header de navegador porque varios de los sitios de donde vienen las imágenes rechazan los pedidos que no lo tienen.

### ProgressActivity.java
Calcula el porcentaje de progreso de tu rutina actual y lleva la cuenta de tus entrenamientos semanales. Si tenés 4 completados, te muestra el cartel de meta cumplida. Su botón "Reiniciar progreso" le avisa a `MainActivity` que limpie la lista del día, pero guarda tu record semanal.

### ProfileActivity.java
Controla la sección de perfil. Tiene un modo de visualización y uno de edición que se alternan en la misma pantalla. Valida que no dejes campos vacíos, guarda los cambios en Firestore y también los devuelve a `MainActivity`. Desde acá cerrás sesión.

### data/UserRepository.java
Concentra todo el acceso a Firestore. Ninguna Activity arma rutas de colecciones ni escribe nombres de campos a mano: le pide los datos a esta clase. Acá viven las lecturas del perfil y la rutina, y las escrituras de cada cambio.

### strings.xml
Centraliza todas las cadenas de texto de la aplicación. Los textos que llevan datos adentro usan plantillas con parámetros (por ejemplo `Series: %1$d - Repeticiones: %2$d`) en vez de armarse con concatenación.

### colors.xml
Define los colores de la aplicación, unificados bajo un estilo de gimnasio en rojo y blanco, más los colores de marca del botón de Google.

### dimens.xml
Junta los márgenes, paddings, tamaños de fuente y medidas de los componentes, para no repetir números sueltos en los layouts.

### themes.xml
Define los estilos y estilos de botones. Acá forzamos la tipografía condensada a nivel global de la app para darle un look deportivo moderno, y está el estilo del botón de Google.

---

## La base de datos

Cada usuario tiene un documento propio, identificado con el `uid` que le da Firebase Authentication:

```
users/{uid}
    name           → nombre que pusiste al registrarte
    goal           → tu objetivo
    level          → tu nivel
    email          → tu correo
    workoutCount   → entrenamientos completados en la semana

users/{uid}/routine/{idEjercicio}
    name, description, muscleGroup, imageUrl
    sets, reps
    completed      → si lo tildaste o no
    position       → para que la lista mantenga el orden
```

La rutina va como subcolección y no como una lista dentro del documento por dos razones: así podemos actualizar un solo ejercicio cuando lo tildás, sin reescribir toda la lista, y así los datos quedan ordenados por documento.

---

## Cómo corre la app

### Flujo en la Versión 0.5
1. El usuario abre la app → se inicia `MainActivity` con ejercicios precargados.
2. El ejercicio se marca tocándolo directamente en la lista.
3. Los botones de progreso y perfil muestran un aviso de "Próximamente" al hacer click.
4. Al cerrar la app todo se pierde.

### Flujo en la Versión 2.0 (Actual)
1. Abrís la app → se inicia `LoginActivity`. Si ya habías entrado antes, te saltea el login.
2. Si no tenés cuenta, tocás "¿No tenés cuenta? Registrate", cargás nombre, correo y contraseña, y entrás. También podés usar el botón de Google.
3. Entrás a la rutina. Si ya tenías ejercicios guardados, aparecen tal como los dejaste.
4. Podés escribir un ejercicio y agregarlo, o abrir "Rutina sugerida" y elegir una (Tren Superior, Tren Inferior o Core). El menú se cierra solo al elegir.
5. Tocando el ✔ de cada tarjeta, marcás los ejercicios realizados. Al completarlos todos, salta el cartel para ir a Progreso.
6. Si tocás "Ver detalle", vas al detalle del ejercicio. Ahí podés editar sus series, repeticiones, descripción o imagen, y también eliminarlo.
7. En **Progreso** ves tus números, el porcentaje de avance y el contador semanal. Si llegás a 4 entrenamientos, salta el cartel semanal y se resetea a 0. Si tocás "Reiniciar progreso", vaciás la rutina diaria.
8. En **Perfil** podés cambiar tu nombre, objetivo y nivel, y también cerrar sesión.
9. Cerrás la app. Cuando la volvés a abrir, sigue todo donde lo dejaste.

---

## Cómo levantar el proyecto

1. Cloná el repositorio y abrilo con Android Studio.
2. El archivo `app/google-services.json` ya está en el repo, así que no hace falta configurar nada de Firebase para probarlo.
3. Sincronizá con Gradle y corré la app en un emulador **con Google Play Services**. Sin eso el botón de Google no funciona (el login con correo sí).

---

## Lo que falta / ideas para después

- **RecyclerView**: optimizar el listado dinámico de ejercicios para listas muy largas.
- **Notificaciones**: recordatorios diarios para hacer ejercicio.
- **Historial**: guardar los entrenamientos de semanas anteriores en vez de resetear el contador.
- **Recuperar contraseña**: que se pueda pedir el mail de restablecimiento desde el login.

---

## Control de Versiones

El proyecto se gestionó con **Git** y se subió a **GitHub** para trabajar en equipo.
Los integrantes del grupo realizamos commits convencionales a lo largo del desarrollo, lo que permite ver en el historial la evolución del proyecto desde el layout inicial hasta la versión 2.0 actual.

---

## Informe

El informe describe cada pantalla con sus funcionalidades, su flujo de uso y la captura correspondiente.

- **Para descargar**: [informe-pantallas.pdf](https://github.com/LautaroRDavinci/final-am-acn4a-ramos/raw/main/docs/informe-pantallas.pdf)
- **Para leer acá mismo**: [docs/informe.md](docs/informe.md)

Las capturas sueltas están en [docs/capturas](docs/capturas).
