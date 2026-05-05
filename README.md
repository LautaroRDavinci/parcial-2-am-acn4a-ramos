# Fit-Routine 💪

Aplicación Android para anotar y gestionar tu rutina de ejercicios del día.

La idea es simple: entrás, escribís los ejercicios que vas a hacer, los agregás a la lista, y los vas tachando a medida que los completás. Por ahora no guarda los datos (eso vendría en una próxima versión con una base de datos).

---

## ¿Qué puede hacer la app?

- Escribís el nombre de un ejercicio en el campo de texto y lo agregás a la lista con el botón
- Si intentás agregar algo vacío, te avisa con un mensaje
- Tocás cualquier ejercicio de la lista y se marca como completado (aparece el ✔ y cambia de color)
- Si lo tocás de nuevo, se desmarca
- Tiene una barra de navegación abajo con tres opciones: Rutina, Progreso y Perfil. Las últimas dos todavía no están implementadas (muestran "Próximamente")

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
│   ├── java/com/example/fit_routine/
│   │   └── MainActivity.java        ← toda la lógica de la app
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    ← diseño de la pantalla
│   │   └── values/
│   │       ├── strings.xml          ← todos los textos de la app
│   │       ├── colors.xml           ← paleta de colores
│   │       └── themes.xml           ← estilos y tema global
│   └── AndroidManifest.xml          ← configuración general de la app
```

---

## Descripción de los archivos principales

### AndroidManifest.xml

Aqui definimos que la `MainActivity` es el punto de entrada (la primera pantalla que ve el usuario cuando abre la app). También le dijimos que el tema visual es `Theme.FitRoutine` y que el nombre de la app es "Fit-Routine".


### MainActivity.java

Es la única Activity que tiene el proyecto por ahora.

Lo que hace cuando arranca:
1. Inicializa la pantalla con `setContentView()`
2. Busca los elementos del XML con `findViewById()`
3. Le asigna comportamiento a los botones con `setOnClickListener()`

Cuando el usuario apreta "Agregar ejercicio":
- Agarra el texto del `EditText`
- Si está vacío, muestra un Toast con un aviso
- Si tiene texto, crea un `TextView` nuevo en código (sin tenerlo predefinido en el XML) y lo agrega al contenedor de la lista
- Al mismo tiempo le asigna un listener de click a ese nuevo `TextView` para que se pueda marcar como completado

Para marcar los ejercicios revisamos si el texto empieza con "✔ ". Si sí, lo quitamos y restauramos el color oscuro. Si no, lo agregamos y ponemos el texto en gris. Es básicamente un checkbox hecho a mano con un `TextView`.

### activity_main.xml

Define cómo se ve la pantalla. La estructura es:

```
ConstraintLayout (contenedor raíz)
├── LinearLayout (parte de arriba)
│   ├── TextView — título "Rutina de hoy"
│   ├── TextView — subtítulo
│   ├── EditText — campo para escribir el ejercicio
│   └── Button  — "Agregar ejercicio"
├── ScrollView (zona del medio, con scroll)
│   └── LinearLayout — acá se van agregando los ejercicios dinámicamente
└── LinearLayout (barra de navegación abajo)
    ├── Button — Rutina
    ├── Button — Progreso
    └── Button — Perfil
```

Usamos `ConstraintLayout` como raíz. El `ScrollView` tiene altura `0dp` con constraints al layout de arriba y a la barra de abajo, así ocupa exactamente el espacio disponible entre los dos.

### strings.xml

Guardamos todos los textos de la app acá en lugar de escribirlos directo en el código o en el XML. Es una buena práctica porque si el día de mañana queremos traducir la app a otro idioma, solo hay que crear otro `strings.xml` con las traducciones y Android elige el correcto según el idioma del dispositivo.

### colors.xml

Define la paleta de colores del diseño de la aplicación. Centralizarlos acá nos permite mantener consistencia visual en toda la app y cambiar un color en un solo lugar sin tener que buscarlo en cada archivo.

Colores


 `primary`:        `#FF5722`    Color naranja principal (botones, tema global)     
 `background`:     `#FAFAFA`    Fondo de la pantalla, gris muy clarito             
 `text`:           `#212121`    Color de los textos principales, casi negro       
 `text_secondary`: `#757575`    Ejercicios marcados como completados, gris medio   
 `black`:          `#FF000000`  Negro puro                                       
 `white`:          `#FFFFFFFF`  Blanco puro                                      

### themes.xml

Acá definimos el tema visual global. Heredamos de `Theme.Material3.DayNight.NoActionBar` lo que nos da soporte automático para modo oscuro y nos saca la barra de título de arriba (la manejamos nosotros desde el layout).

También definimos dos estilos de texto: `TextStyle.Title` para el título grande y `TextStyle.Subtitle` para el subtítulo.

---

## Cómo corre la app

1. El usuario abre la app → se inicia `MainActivity`
2. Aparece la pantalla con el título y el campo de texto
3. El usuario escribe un ejercicio y toca "Agregar ejercicio"
4. El ejercicio aparece en la lista
5. El usuario puede ir tocando cada ejercicio para marcarlo con ✔
6. Los botones Progreso y Perfil muestran "Próximamente"
7. Al cerrar la app los datos se pierden 

---

## Lo que falta / ideas para después

- **Persistencia de datos**: guardar los ejercicios con Room Database para que no se pierdan al cerrar
- **Pantallas de Progreso y Perfil**: completar la navegación que ya está planteada en la barra inferior
- **RecyclerView**: reemplazar el LinearLayout para listas más largas
- **Eliminar ejercicios**: por ahora no hay forma de sacar uno de la lista

---

## Control de Versiones

El proyecto se gestionó con **Git** y se subió a **GitHub** para trabajar en equipo.

Los dos integrantes del grupo realizamos commits a lo largo del desarrollo, lo que permite ver en el historial la evolución del proyecto desde el layout inicial hasta el estado actual.

