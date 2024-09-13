# TestUpax
Proyecto de evaluación, basado en una arquitectura CLEAN con MVVM.

## Funciones de la aplicación

- Los usuarios pueden cargar una imagen desde una URL
- Los usuarios pueden ver el listado de pokemones y al hacer clic en cualquier item se pueden ver los detalles del mismo.

## Arquitectura de la aplicación
Basado en la arquitectura Clean y el patrón MVVM.

## La aplicación incluye los siguientes componentes principales:
- Un DAO que trabaja con la BD, proporcionando una interfaz de datos unificada.
- Un ViewModel que proporciona datos específicos para la interfaz de usuario.
- La interfaz de usuario, que muestra una representación visual de los datos en ViewModel.

## Paquetes de la aplicación
- database
- network
- repository
- ui
- utils

## Especificaciones de la aplicación
- SDK mínimo 24
- Java (en la rama maestra) y Kotlin (en la rama kotlin_support)
- Arquitectura MVVM
- Componentes de la arquitectura de Android (LiveData, ViewModel, Material Design).
- **ViewModel** para pasar datos del modelo a las vistas
- **LiveData**
- **Room** para persistencia de datos.