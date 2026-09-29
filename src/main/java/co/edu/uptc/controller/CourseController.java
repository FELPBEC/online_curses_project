package co.edu.uptc.controller;

import java.util.ArrayList;
import java.util.List;

import co.edu.uptc.exceptions.CourseNotFoundException;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.InvalidParentException;
import co.edu.uptc.exceptions.LessonNotFoundException;
import co.edu.uptc.exceptions.ModuleNotFoundException;
import co.edu.uptc.exceptions.NoAvaliableLessonsInTheCourseException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Module;
import co.edu.uptc.model.TreeNode;

/**
 * Clase CourseController que se encarga de:
 * 1. Manejar el CRUD de cursos
 * 2. Manejar el CRUD de módulos
 * 3. Manejar el CRUD de lecciones
 * 
 * @author @jm1407db
 * @version v1.1
 * @since 20/09/2026
 */
public class CourseController {

    private final Repository<Course> repository;
    private List<Course> courseList;

    /**
     * Método constructor para la clase CourseController que inicializa la lista en memoria
     * por medio del método sendAll() del repositorio que se envía como parámetro.
     *  
     * @param repository Repositorio de cualquier tipo que maneja la persistencia.
     */
    public CourseController(Repository<Course> repository) {
        this.repository = repository;
        this.courseList = repository.sendAll();
    }

    /**
     * Agrega un nuevo curso a la lista global de cursos y genera su nodo raíz.
     * 
     * @param titleCourse Título del nuevo curso.
     * @param descriptionCourse Descripción del nuevo curso.
     * @return El curso recién creado.
     */
    public Course addCourse(String titleCourse, String descriptionCourse) {
        String newIdCourse = String.valueOf(getNextCourseIdNumber());
        Course newCourse = new Course();
        newCourse.setId(newIdCourse);
        newCourse.setTitle(titleCourse);
        newCourse.setDescription(descriptionCourse);
        TreeNode<EducativeElement> rootNode = new TreeNode<>(newCourse);
        newCourse.setRoot(rootNode);
        courseList.add(newCourse);
        return newCourse;
    }

    /**
     * Busca un curso por su ID dentro de la lista global en memoria.
     * 
     * @param courseId Identificador del curso (ej: "COURSE-1").
     * @return El objeto {@link Course} encontrado o {@code null} si no existe.
     */
    public Course findCourse(String courseId) {
        for (Course course : courseList) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Elimina un curso completo de la lista global a partir de su ID.
     * Al eliminar el curso, se remueve automáticamente toda su estructura jerárquica
     * de módulos y lecciones asociadas.
     * 
     * @param courseId Identificador único del curso a eliminar (ej: "COURSE-1").
     * @return {@code true} si el curso fue encontrado y eliminado con éxito; 
     *         {@code false} de lo contrario.
     */
    public boolean deleteCourse(String courseId) {
        return courseList.removeIf(course -> course != null && course.getId().equals(courseId));
    }

    /**
     * Actualiza el título de un curso existente.
     * 
     * @param courseId ID del curso a modificar.
     * @param newTitleCourse Nuevo título del curso.
     * @return {@code true} si la actualización fue exitosa, {@code false} si el curso no existe.
     */
    public boolean updateTitleCourse(String courseId, String newTitleCourse) {
        Course course = findCourse(courseId);
        if (course != null && newTitleCourse != null && !newTitleCourse.isBlank()) {
            course.setTitle(newTitleCourse);
            return true;
        }
        return false;
    }

    /**
     * Agrega un nuevo módulo como hijo de otro nodo (del curso o de otro módulo).
     * 
     * @param courseId ID del curso al que pertenece.
     * @param parentId ID del nodo padre (puede ser el ID del curso o de otro módulo).
     * @param moduleTitle Título del nuevo módulo.
     * @param description Descripción del nuevo módulo.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     * @throws InvalidParentException Si el nodo padre especificado no existe dentro del árbol del curso.
     */
    public void addModule(String courseId, String parentId, String moduleTitle, String description) {
        Course course = findCourse(courseId);
        TreeNode<EducativeElement> parentNode = findNode(course.getRoot(), parentId);
        if (parentNode != null) {
            String newIdModule = String.valueOf(getNextIdNumber(course.getRoot(), EducativeElementType.MODULO));
            Module newModule = new Module();
            newModule.setId(newIdModule);
            newModule.setDescription(description);
            newModule.setTitle(moduleTitle);
            TreeNode<EducativeElement> newNode = new TreeNode<>(newModule);
            parentNode.addSon(newNode);
        } else {
            throw new InvalidParentException("El nodo padre especificado con ID '" + parentId + "' no existe.");
        }
    }

    /**
     * Busca un módulo específico dentro del árbol de un curso por su ID.
     * 
     * @param courseId ID del curso al que pertenece el módulo (ej: "COURSE-1").
     * @param moduleId ID del módulo a buscar (ej: "COURSE-1-MOD-1").
     * @return El objeto {@link Module} si se encuentra y es de tipo módulo, o {@code null} si no coincide el tipo.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     */
    public Module findModule(String courseId, String moduleId) {
        Course course = findCourse(courseId);
        if (course == null) {
            return null;
        }
        TreeNode<EducativeElement> node = findNode(course.getRoot(), moduleId);
        if (node != null && node.getData() != null && node.getData().getElementType() == EducativeElementType.MODULO) {
            return (Module) node.getData();
        }
        return null;
    }

    /**
     * Elimina un módulo específico del árbol de un curso a partir de su ID.
     * 
     * @param courseId Identificador único del curso (ej: "COURSE-1").
     * @param moduleId Identificador único del módulo a eliminar (ej: "MODULE-1").
     * @return {@code true} si el módulo fue encontrado y eliminado con éxito; {@code false} de lo contrario.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     * @throws ModuleNotFoundException Si el módulo a eliminar no existe en el curso.
     */
    public boolean deleteModule(String courseId, String moduleId) {
        Course course = findCourse(courseId);
        if (course == null) {
            return false;
        }
        Module deleteModule = findModule(courseId, moduleId);
        if (deleteModule == null) {
            return false;
        }
        TreeNode<EducativeElement> parentNode = findParentNode(course.getRoot(), moduleId);
        if (parentNode != null) {
            return parentNode.getSons().removeIf(son ->
                son != null && son.getData() != null && son.getData().getElementType() == EducativeElementType.MODULO && son.getData().getId().equals(moduleId));
        }
        return false;
    }

    /**
     * Actualiza los datos de un módulo existente. Si un campo viene nulo o vacío, 
     * se conserva el valor actual que tenía el módulo.
     * 
     * @param courseId ID del curso al que pertenece el módulo.
     * @param moduleId ID del módulo a modificar.
     * @param newTitle Nuevo título del módulo (opcional, null para mantener el actual).
     * @param newDescription Nueva descripción del módulo (opcional, null para mantener la actual).
     * @return {@code true} si se encontró el módulo y se aplicaron los cambios; {@code false} de lo contrario.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     */
    public boolean updateModule(String courseId, String moduleId, String newTitle, String newDescription) {
        Module module = findModule(courseId, moduleId);
        if (module == null) {
            return false;
        }
        if (newTitle != null && !newTitle.isBlank()) {
            module.setTitle(newTitle);
        }
        if (newDescription != null) {
            module.setDescription(newDescription);
        }
        return true;
    }

    /**
     * Agrega una nueva lección dentro de un módulo específico en la jerarquía del curso.
     * 
     * @param courseId Identificador único del curso (ej: "COURSE-1").
     * @param parentId Identificador único del módulo padre (ej: "MODULE-1").
     * @param lessonTitle Título de la lección.
     * @param description Descripción detallada del contenido de la lección.
     * @param duration Duración estimada de la lección en minutos/horas.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     * @throws InvalidParentException Si el nodo padre especificado no existe o no corresponde a un Módulo.
     */
    public void addLesson(String courseId, String parentId, String lessonTitle, String description, double duration) {
        Course course = findCourse(courseId);
        if (course == null) {
            throw new CourseNotFoundException("El curso con ID '" + courseId + "' no existe.");
        }
        TreeNode<EducativeElement> parentNode = findNode(course.getRoot(), parentId);
        if (parentNode != null && parentNode.getData() != null && 
            parentNode.getData().getElementType() == EducativeElementType.MODULO) {
            String newIdLesson = String.valueOf(getNextIdNumber(course.getRoot(), EducativeElementType.LESSON));
            Lessons newLesson = new Lessons();
            newLesson.setId(newIdLesson);
            newLesson.setTitle(lessonTitle);
            newLesson.setDescription(description);
            newLesson.setDuration(duration);
            TreeNode<EducativeElement> newNode = new TreeNode<>(newLesson);
            parentNode.addSon(newNode);
        } else {
            throw new InvalidParentException("El nodo padre debe ser un Módulo válido.");
        }
    }

    /**
     * Busca una lección específica dentro de la estructura jerárquica de un curso por su ID.
     * 
     * @param courseId Identificador único del curso (ej: "COURSE-1").
     * @param lessonId Identificador único de la lección a buscar (ej: "LESSON-1").
     * @return El objeto {@link Lessons} si se encuentra y corresponde a una lección, 
     *         o {@code null} si el ID no pertenece a una lección.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     */
    public Lessons findLesson(String courseId, String lessonId) {
        Course course = findCourse(courseId);
        if (course == null) {
            throw new CourseNotFoundException("El curso con ID '" + courseId + "' no existe.");
        }
        TreeNode<EducativeElement> node = findNode(course.getRoot(), lessonId);
        if (node != null && node.getData() != null && node.getData().getElementType() == EducativeElementType.LESSON) {
            return (Lessons) node.getData();
        }
        return null;
    }

    /**
     * Elimina una lección específica del árbol de un curso.
     * 
     * @param courseId Identificador del curso.
     * @param lessonId Identificador de la lección que se desea eliminar.
     * @return {@code true} si fue eliminada con éxito, {@code false} de lo contrario.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     * @throws LessonNotFoundException Si la lección especificada no existe en el curso.
     */
    public boolean deleteLesson(String courseId, String lessonId) {
        Course course = findCourse(courseId);
        if (course == null) {
            return  false;
        }
        Lessons deleteLesson = findLesson(courseId, lessonId);
        if (deleteLesson == null) {
           return  false;
        }
        TreeNode<EducativeElement> parentNode = findParentNode(course.getRoot(), lessonId);
        if (parentNode != null) {
            return parentNode.getSons().removeIf(son -> son.getData() != null && son.getData().getId().equals(lessonId));
        }
        return false;
    }

    /**
     * Actualiza los datos de una lección existente.
     * 
     * @param courseId ID del curso al que pertenece la lección.
     * @param lessonId ID de la lección a modificar.
     * @param newTitle Nuevo título (opcional).
     * @param newDescription Nueva descripción (opcional).
     * @param newDuration Nueva duración (opcional, debe ser > 0).
     * @return {@code true} si la lección fue actualizada con éxito; {@code false} de lo contrario.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     */
    public boolean updateLesson(String courseId, String lessonId, String newTitle, String newDescription, double newDuration) {
        Lessons lesson = findLesson(courseId, lessonId);
        if (lesson == null) {
            return false;
        }
        if (newTitle != null && !newTitle.isBlank()) {
            lesson.setTitle(newTitle);
        }
        if (newDescription != null) {
            lesson.setDescription(newDescription);
        }
        if (newDuration > 0) {
            lesson.setDuration(newDuration);
        }
        return true;
    }

    /**
     * Obtiene el siguiente número entero consecutivo para generar un ID único
     * según el tipo de elemento educativo dentro del árbol del curso.
     * 
     * @param current Nodo raíz desde el cual se inicia la búsqueda recursiva.
     * @param type Tipo de {@link EducativeElementType} a evaluar (MODULO o LESSON).
     * @return El número entero consecutivo disponible para el nuevo elemento.
     */
    private int getNextIdNumber(TreeNode<EducativeElement> current, EducativeElementType type) {
        int maxId = findMaxIdRecursive(current, type);
        return maxId + 1;
    }

    /**
     * Obtiene el siguiente número consecutivo para la creación de un nuevo Curso.
     * 
     * @return El siguiente ID entero disponible para un Curso.
     * @throws InvalidFortmatException Si ocurre un error de formato al parsear el ID de curso.
     */
    private int getNextCourseIdNumber() {
        int maxId = 0;
        for (Course course : courseList) {
            if (course.getId() != null) {
                String[] parts = course.getId().split("-");
                if (parts.length > 1) {
                    try {
                        int currentNum = Integer.parseInt(parts[parts.length - 1]);
                        if (currentNum > maxId) {
                            maxId = currentNum;
                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidFortmatException("Error de formato en ID de curso: " + course.getId());
                    }
                }
            }
        }
        return maxId + 1;
    }

    /**
     * Método recursivo auxiliar que explora el subárbol para identificar el valor
     * numérico máximo que ha sido asignado a un tipo de elemento educativo específico.
     * 
     * @param node Nodo actual en el que se encuentra la exploración del árbol.
     * @param type Tipo de {@link EducativeElementType} filtrado (MODULO, LESSON o COURSE).
     * @return El número entero máximo encontrado entre los IDs del tipo especificado.
     * @throws InvalidFortmatException Si ocurre un error de formato al extraer la parte numérica del ID.
     */
    private int findMaxIdRecursive(TreeNode<EducativeElement> node, EducativeElementType type) {
        if (node == null || node.getData() == null) {
            return 0;
        }
        int currentMax = 0;
        if (node.getData().getElementType() == type) {
            String idStr = node.getData().getId();
            String[] parts = idStr.split("-");
            if (parts.length > 1) {
                try {
                    currentMax = Integer.parseInt(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    throw new InvalidFortmatException("Error de formato en ID: '" + idStr + "'.");
                }
            }
        }
        for (TreeNode<EducativeElement> son : node.getSons()) {
            int sonMax = findMaxIdRecursive(son, type);
            if (sonMax > currentMax) {
                currentMax = sonMax;
            }
        }
        return currentMax;
    }

    /**
     * Método recursivo que busca un nodo específico dentro del Árbol N-ario.
     * 
     * @param current Nodo actual en el que está la recursión.
     * @param targetId ID del elemento que estamos buscando.
     * @return El {@link TreeNode} que contiene al elemento buscado, o {@code null} si no lo encuentra.
     */
    private TreeNode<EducativeElement> findNode(TreeNode<EducativeElement> current, String targetId) {
        if (current == null || current.getData() == null) {
            return null;
        }
        if (current.getData().getId().equals(targetId)) {
            return current;
        }
        for (TreeNode<EducativeElement> son : current.getSons()) {
            TreeNode<EducativeElement> found = findNode(son, targetId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Busca de forma recursiva el nodo padre de un elemento específico dentro del árbol.
     * 
     * @param current Nodo actual en la exploración recursiva.
     * @param targetId Identificador del nodo hijo que se está buscando.
     * @return El {@link TreeNode} que actúa como padre del targetId, o {@code null} si no lo encuentra.
     */
    private TreeNode<EducativeElement> findParentNode(TreeNode<EducativeElement> current, String targetId) {
        if (current == null || current.getSons() == null) {
            return null;
        }
        for (TreeNode<EducativeElement> son : current.getSons()) {
            if (son.getData() != null && son.getData().getId().equals(targetId)) {
                return current; 
            }
        }
        for (TreeNode<EducativeElement> son : current.getSons()) {
            TreeNode<EducativeElement> parent = findParentNode(son, targetId);
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }

    /**
     * Realiza un recorrido en PreOrden sobre el árbol de un curso y retorna 
     * una lista plana con todos los elementos educativos (Módulos y Lecciones).
     * 
     * @param courseId Identificador del curso.
     * @return Lista ordenada con todos los elementos del árbol en secuencia PreOrden.
     */
    public List<EducativeElement> getPreOrder(String courseId) {
        List<EducativeElement> result = new ArrayList<>();
        Course course = findCourse(courseId);
        if (course != null && course.getRoot() != null) {
            collectPreOrderRecursive(course.getRoot(), result);
        }
        return result;
    }

    /**
     * Método auxiliar recursivo para el recorrido PreOrden.
     * 
     * @param node Nodo actual a recorrer.
     * @param result Lista acumuladora de elementos educativos.
     */
    private void collectPreOrderRecursive(TreeNode<EducativeElement> node, List<EducativeElement> result) {
        if (node == null || node.getData() == null) {
            return;
        }
        result.add(node.getData());
        for (TreeNode<EducativeElement> son : node.getSons()) {
            collectPreOrderRecursive(son, result);
        }
    }

    /**
     * Genera una lista con todas las lecciones pertenecientes a un curso.
     * 
     * @param idCourse Identificador del curso.
     * @return Lista de elementos educativos de tipo {@link EducativeElementType#LESSON}.
     */
    private List<EducativeElement> getListOfLessonsInCourse(String idCourse) {
        List<EducativeElement> lessonsByCourse = new ArrayList<>();
        List<EducativeElement> courseList = getPreOrder(idCourse);
        for (EducativeElement element : courseList) {
            if (element.getElementType() == EducativeElementType.LESSON) {
                lessonsByCourse.add(element);
            }
        }
        return lessonsByCourse;
    }

    /**
     * Obtiene el identificador de la primera lección del curso.
     * 
     * @param idCourse ID del curso (ej: "COR-1").
     * @return ID de la primera lección (ej: "LESS-1").
     * @throws NoAvaliableLessonsInTheCourseException Si el curso no contiene lecciones.
     */
    public String getIdFirstLesson(String idCourse) {
        List<EducativeElement> lessonsByCourse = getListOfLessonsInCourse(idCourse);
        if (lessonsByCourse.isEmpty()) {
            throw new NoAvaliableLessonsInTheCourseException("El curso no tiene lecciones disponibles.");
        }
        return lessonsByCourse.get(0).getId();
    }

    /**
     * Genera una lista con todos los módulos pertenecientes a un curso.
     * 
     * @param idCourse Identificador del curso.
     * @return Lista de elementos educativos de tipo {@link EducativeElementType#MODULO}.
     */
    private List<EducativeElement> getListOfModulesInCourse(String idCourse) {
        List<EducativeElement> modulesByCourse = new ArrayList<>();
        List<EducativeElement> courseList = getPreOrder(idCourse);
        for (EducativeElement element : courseList) {
            if (element.getElementType() == EducativeElementType.MODULO) {
                modulesByCourse.add(element);
            }
        }
        return modulesByCourse;
    }

    /**
     * Retorna el identificador del módulo al que pertenece la lección actual según el progreso del estudiante.
     * 
     * @param idCourse ID del curso (ej: "COUR-1").
     * @param idLesson ID de la lección (ej: "LESS-2").
     * @return ID del módulo actual.
     */
    private String getIdCurrentModulo(String idCourse, String idLesson) {
        String idCurrentModulo = "";
        List<EducativeElement> courseList = getPreOrder(idCourse);
        int positionLesson = 0;
        for (int i = 0; i < courseList.size(); i++) {
            if (idLesson.equals(courseList.get(i).getId())) {
                positionLesson = i;
            }
            if (i < positionLesson) {
                if (courseList.get(i).getElementType() == EducativeElementType.MODULO) {
                    idCurrentModulo = courseList.get(i).getId();
                }
            }
        }
        return idCurrentModulo;
    }

    /**
     * Envía la id de la siguiente lección de un curso.
     * 
     * @param idCourse ID del curso, por ejemplo: COR-1.
     * @param idCurrentLesson ID de la lección actual, por ejemplo: LESS-3.
     * @return ID de la lección siguiente.
     * @throws NoAvaliableLessonsInTheCourseException Excepción que se lanza si ya no hay una lección siguiente a la ingresada, útil para indicar que el curso ha sido completado.
     */
    public String getIdNextLesson(String idCourse, String idCurrentLesson) throws NoAvaliableLessonsInTheCourseException {
        List<EducativeElement> lessonsByCourse = getListOfLessonsInCourse(idCourse);
        for (int i = 0; i < lessonsByCourse.size(); i++) {
            if (idCurrentLesson.equals(lessonsByCourse.get(i).getId())) {
                if ((i + 1) >= lessonsByCourse.size()) {
                    throw new NoAvaliableLessonsInTheCourseException("Has completado todas las lecciones del curso");
                } else {
                    return lessonsByCourse.get(i + 1).getId();
                }
            }
        }
        return idCurrentLesson;
    }

    /**
     * Obtiene la posición (índice) de la lección actual dentro de la lista de lecciones del curso.
     * 
     * @param idCourse ID del curso (ej: "COR-1").
     * @param idLesson ID de la lección.
     * @return La posición entera de la lección actual.
     */
    private int getActualPositionOfLesson(String idCourse, String idLesson) {
        int position = 0;
        List<EducativeElement> lessonsByCourse = getListOfLessonsInCourse(idCourse);
        for (int i = 0; i < lessonsByCourse.size(); i++) {
            if (idLesson.equals(lessonsByCourse.get(i).getId())) {
                position = i;
            }
        }
        return position;
    }

    /**
     * Obtiene la posición (índice) del módulo actual dentro de la lista de módulos del curso.
     * 
     * @param idCourse ID del curso.
     * @param idLesson ID de la lección actual.
     * @return La posición entera del módulo actual.
     */
    private int getActualPositonModulo(String idCourse, String idLesson) {
        int position = 0;
        List<EducativeElement> moduloByCourse = getListOfModulesInCourse(idCourse);
        String idModule = getIdCurrentModulo(idCourse, idLesson);
        for (int i = 0; i < moduloByCourse.size(); i++) {
            if (idModule.equals(moduloByCourse.get(i).getId())) {
                position = i;
            }
        }
        return position;
    }

    /**
     * Envía el porcentaje de completado de las lecciones de un curso.
     * 
     * @param idCourse ID del curso (ej: "COR-1").
     * @param idLesson ID de la lección (ej: "LESS-3").
     * @return El porcentaje de completado del curso con base a las lecciones completadas.
     */
    public double getPercentOfLessonsComplete(String idCourse, String idLesson) {
        int completedLessons = getActualPositionOfLesson(idCourse, idLesson);
        int lessonsTotal = getListOfLessonsInCourse(idCourse).size();
        if (lessonsTotal == 0) return 0.0;
        return (100.0 * completedLessons) / lessonsTotal;
    }

    /**
     * Envía el porcentaje de completado de los módulos de un curso.
     * 
     * @param idCourse ID del curso (ej: "COR-1").
     * @param idLesson ID de la lección (ej: "LESS-3").
     * @return El porcentaje de completado del curso con base a los módulos completados.
     */
    public double getPercentOfModulesComplete(String idCourse, String idLesson) {
        int actualModule = getActualPositonModulo(idCourse, idLesson) + 1;
        int moduleTotal = getListOfModulesInCourse(idCourse).size();
        if (moduleTotal == 0) return 0.0;
        return (100.0 * actualModule) / moduleTotal;
    }

    /**
     * Guarda el estado actual de la lista de cursos en el almacenamiento persistente.
     */
    public void saveChanges() {
        if (repository != null) {
            repository.saveAll(courseList);
        }
    }

    /**
     * Retorna la lista global de cursos cargados en memoria.
     * 
     * @return Lista de cursos.
     */
    public List<Course> getCourseList() {
        return courseList;
    }

    /**
     * Obtiene todas las lecciones pertenecientes a un módulo específico (incluyendo
     * lecciones de submódulos anidados) recolectadas en recorrido PreOrden.
     * 
     * @param courseId ID del curso al que pertenece el módulo (ej: "COURSE-1").
     * @param moduleId ID del módulo del cual se requieren las lecciones hijas (ej: "MODULE-1").
     * @return Lista de objetos {@link Lessons} pertenecientes al módulo en orden PreOrden.
     * @throws CourseNotFoundException Si el curso especificado no existe.
     * @throws ModuleNotFoundException Si el módulo especificado no existe en el curso.
     */
    public List<Lessons> getLessonsByModule(String courseId, String moduleId) {
        List<Lessons> lessonsList = new ArrayList<>();
        Course course = findCourse(courseId);
        if (course == null) {
            throw new CourseNotFoundException("El curso con ID '" + courseId + "' no existe.");
        }

        TreeNode<EducativeElement> moduleNode = findNode(course.getRoot(), moduleId);
        if (moduleNode != null && moduleNode.getData() != null && 
            moduleNode.getData().getElementType() == EducativeElementType.MODULO) {
            collectLessonsInPreOrder(moduleNode, lessonsList);
        } else {
            throw new ModuleNotFoundException("El módulo con ID '" + moduleId + "' no existe.");
        }

        return lessonsList;
    }

    /**
     * Método recursivo auxiliar para explorar el subárbol de un módulo y recolectar
     * sus lecciones en secuencia PreOrden (Padre -> Hijos de izquierda a derecha).
     * 
     * @param node Nodo actual en la exploración del subárbol.
     * @param lessonsList Lista acumuladora de lecciones.
     */
    private void collectLessonsInPreOrder(TreeNode<EducativeElement> node, List<Lessons> lessonsList) {
        if (node == null || node.getData() == null) {
            return;
        }

        if (node.getData().getElementType() == EducativeElementType.LESSON) {
            lessonsList.add((Lessons) node.getData());
        }

        for (TreeNode<EducativeElement> son : node.getSons()) {
            collectLessonsInPreOrder(son, lessonsList);
        }
    }
}