# SISTEMA DE CURSOS ONLINE
• Contexto: Administración de cursos, lecciones y estudiantes.
• Árbol usado: Árbol N-ario (Curso → Módulos → Lecciones).
• Objetivos:
o CRUD de cursos y estudiantes.
o Seguimiento del progreso.
• Persistencia: JSON.
• Extras: Estadísticas de estudiantes por curso


## Requisitos Comunes
###  Estructura de Capas:
• model: Clases del dominio (ej. Libro, Contacto, Producto).
• controller: Lógica del negocio (árboles, operaciones CRUD).
• persistence: Manejo de archivos JSON/XML/CSV.
• utils: Métodos auxiliares (validaciones, conversores).
• viewController: Controladores de JavaFX (FXML + lógica visual).
• resources-data: Archivos de persistencia de prueba.
• i18n: Archivos .properties para idiomas.
• images: Íconos, logos, portadas.
### Buenas Prácticas:
• Uso de Maven para dependencias.
• JavaDoc en clases públicas.
• Patrones recomendados: MVC, DAO, Singleton (para gestor de persistencia).
• Manejo de excepciones personalizado.
### Testing
• Construcción de pruebas unitarias con una cobertura mínima del 75% en lógica
de negocio.

## Planteamiento
### Clases a utilizar:
 Clase abstracta Users: La clase básica que define todos los atributos de los usuarios en general.
 Estudiante: Desciende de la clase usuario y con una lista de IDs de Cursos inscritos 
 Administrador: Maneja los CRUDS de Cursos, de Admins, Modulos y lecciones

 Interfaz de ElementoEducativo: interfaz con los contratos para Cursos, Modulos y lecciones
 Cursos: contiene una lista de modulos y una id 
 Modulos: Contiene una lista de lecciones y una id
 Lecciones: Contiene una lista de estudiantes y una id
 
