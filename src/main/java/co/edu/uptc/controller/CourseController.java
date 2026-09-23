package co.edu.uptc.controller;

import java.util.ArrayList;
import java.util.List;
import co.edu.uptc.exceptions.InvalidFortmatException;
import co.edu.uptc.exceptions.InvalidParentException;
import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.interfaces.EducativeElementType;
import co.edu.uptc.interfaces.Repository;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Module;
import co.edu.uptc.model.TreeNode;
/**Clase CourseController que se encarga de:
 * 1. Manejar el CRUD de cursos
 * 2. Manejar el CRUD de modulos
 * 3. Manejar el CRUD de lecciones
 * 
 * @author @jm1407db
 * @version v1.0
 * @since 20/09/2026
 */
public class CourseController {
    private final Repository<Course> repository;
    private List<Course> courseList;
    /**Método constructor para la clase CourseController
     * que inicializa la lista en memoria por medio del método sendAll() del repositorio que se envía como párametro
     *  
     * @param repository repositorio de cualquier tipo que maneja la persistencia 
     */
    public CourseController(Repository<Course> repository) {
        this.repository = repository;
        this.courseList = repository.sendAll();
    }
    /**
     * Agrega un nuevo curso a la lista global de cursos y genera su nodo raíz.
     * 
     * @param title Título del nuevo curso
     */
    public void addCourse(String titleCourse){
        String newIdCourse= String.valueOf(getNextCourseIdNumber());
        Course newCourse= new Course();
        newCourse.setId(newIdCourse);
        newCourse.setTitle(titleCourse);
        TreeNode<EducativeElement> rootNode= new TreeNode<>(newCourse);
        newCourse.setRoot(rootNode);
        courseList.add(newCourse);
    }
    /**
     * Busca un curso por su ID dentro de la lista global en memoria.
     * 
     * @param courseId Identificador del curso (ej: "COURSE-1")
     * @return El objeto Course encontrado o null si no existe
     */
    public Course findCourse(String courseId){
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
    public boolean deleteCourse (String courseId){
        return courseList.removeIf(course->course!=null&& course.getId().equals(courseId));
    }
    /**
     * Actualiza el título de un curso existente.
     * 
     * @param courseId ID del curso a modificar.
     * @param newTitle Nuevo título del curso.
     * @return true si la actualización fue exitosa, false si el curso no existe.
     */
    public boolean updateTitleCourse(String courseId,String newTitleCourse){
        Course course= findCourse(courseId);
        if (course!=null&& newTitleCourse!=null&& !newTitleCourse.isBlank()) {
            course.setTitle(newTitleCourse);
            return true;
        }
        return false;
    }
    /**
     * Agrega un nuevo módulo como hijo de otro nodo (del curso o de otro módulo).
     * 
     * @param courseId ID del curso al que pertenece
     * @param parentId ID del nodo padre (puede ser el ID del curso o de otro módulo)
     * @param moduleTitle Título del nuevo módulo
     * @param description Descripción del nuevo módulo
     */
    public void addModule(String courseId,String parentId,String moduleTitle,String description){
        Course course= findCourse(courseId);
        if (course==null) {
            System.out.println("Error el curso no existe");//TODO: BORRAR: manejar en excepcion
            return;
        }
        TreeNode<EducativeElement> parentNode = findNode(course.getRoot(), parentId);
        if (parentNode !=null) {
            String newIdModule=String.valueOf(getNextIdNumber(course.getRoot(), EducativeElementType.MODULO));
            Module newModule= new Module();
            newModule.setId(newIdModule);
            newModule.setDescription(description);
            newModule.setTitle(moduleTitle);
            TreeNode<EducativeElement> newNode=new TreeNode<>(newModule);
            parentNode.addSon(newNode);
        }
    }
    /**
     * Busca un módulo específico dentro del árbol de un curso por su ID.
     * 
     * @param courseId ID del curso al que pertenece el módulo (ej: "COURSE-1")
     * @param moduleId ID del módulo a buscar (ej: "COURSE-1-MOD-1")
     * @return El objeto {@link Module} si se encuentra y es un módulo, o null si no existe
     */
    public Module findModule(String courseId,String moduleId){
        Course course= findCourse(courseId);
        if (course==null) {
            System.out.println("Error el curso no existe");// TODO: BORRAR: manejar en excepcion
            return null;
        }
        TreeNode<EducativeElement> node = findNode(course.getRoot(), moduleId);
        if (node !=null && node.getData()!=null && node.getData().getElementType()== EducativeElementType.MODULO) {
            return (Module) node.getData();
        }
        return null;
    }
        /**
     * Elimina un módulo específico del árbol de un curso a partir de su ID.
     * 
     * @param courseId Identificador único del curso (ej: "COURSE-1").
     * @param moduleId Identificador único del módulo a eliminar (ej: "MODULE-1").
     * @return {@code true} si el módulo fue encontrado y eliminado con éxito; 
     *         {@code false} de lo contrario.
     */
    public boolean deleteModule(String courseId,String moduleId){
        Course course= findCourse(courseId);
        if (course==null) {
            return false;
        }
        Module deleteModule= findModule(courseId, moduleId);
        if (deleteModule==null) {
            System.out.println("Error el modulo no existe");// TODO: BORRAR: manejar en excepcion ModuleNotFoundException
            return false;
        }
        TreeNode<EducativeElement> parentNode= findParentNode(course.getRoot(), moduleId);
        if (parentNode!=null) {
        return  parentNode.getSons().removeIf(son->
            son!=null && son.getData()!=null&& son.getData().getElementType()==EducativeElementType.MODULO&& son.getData().getId().equals(moduleId));
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
     */
    public void addLesson(String courseId, String parentId, String lessonTitle, String description, double duration) {
        Course course = findCourse(courseId);
        if (course == null) {
            // TODO: Reemplazar por excepción personalizada (ej: CourseNotFoundException)
            System.out.println("Error: El curso no existe.");
            return;
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
            throw new InvalidParentException("Error: El nodo padre debe ser un Módulo válido.");
        }
    }
    /**
     * Busca una lección específica dentro de la estructura jerárquica de un curso por su ID.
     * 
     * @param courseId Identificador único del curso (ej: "COURSE-1").
     * @param lessonId Identificador único de la lección a buscar (ej: "LESSON-1").
     * @return El objeto {@link Lessons} si se encuentra y corresponde a una lección, 
     *         o  null si el curso no existe o el ID no pertenece a una lección.
     */
    public Lessons findLesson(String courseId,String lessonId){
        Course course = findCourse(courseId);
        if (course == null) {
            // TODO: Reemplazar por excepción personalizada (ej: CourseNotFoundException)
            System.out.println("Error: El curso no existe.");
            return null;
        }
        TreeNode<EducativeElement> node=findNode(course.getRoot(), lessonId);
        if (node !=null && node.getData()!=null && node.getData().getElementType()== EducativeElementType.LESSON) {
            return (Lessons) node.getData();
        }
        return null;
    }
    /**
     * Elimina una lección específica del árbol de un curso.
     * 
     * @param courseId Identificador del curso.
     * @param lessonId Identificador de la lección que se desea eliminar.
     * @return true si fue eliminada con éxito, false de lo contrario.
     */
    public boolean deleteLesson(String courseId, String lessonId){
        Course course = findCourse(courseId);
        if (course == null) {
            // TODO: Reemplazar por excepción personalizada (ej: CourseNotFoundException)
            System.out.println("Error: El curso no existe.");
            return false;
        }
        TreeNode<EducativeElement> parentNode= findParentNode(course.getRoot(), lessonId);
        if (parentNode!=null) {
            return parentNode.getSons().removeIf(son->son.getData()!=null&&son.getData().getId().equals(lessonId));
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
     * @param type Tipo de {@link EducativeElementType }a evaluar (MODULO o LESSON).
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
     * @param type Tipo de {@link EducativeElementType } filtrado (MODULO, LESSON o COURSE).
     * @return El número entero máximo encontrado entre los IDs del tipo especificado.
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
                    throw new InvalidFortmatException("Error de formato en ID: '" + idStr + "'. Se debe implementar el manejo de excepción correspondiente.");
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
     * @param current Nodo actual en el que está la recursión
     * @param targetId ID del elemento que estamos buscando
     * @return El TreeNode que contiene al elemento buscado, o null si no lo encuentra
     */
    private TreeNode<EducativeElement> findNode(TreeNode<EducativeElement> current,String targetId){
        if (current==null || current.getData()==null) {
            return  null;
        }
        if (current.getData().getId().equals(targetId)) {
            return current;
        }
        
        for (TreeNode<EducativeElement> son : current.getSons()) {
            TreeNode<EducativeElement> found=findNode(son, targetId);
            if (found!=null) {
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
     * @return El TreeNode que actúa como padre del targetId, o null si no lo encuentra.
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
     */
    private void collectPreOrderRecursive(TreeNode<EducativeElement> node, List<EducativeElement> result) {
        if (node == null || node.getData() == null) {
            return;
        }

        // 1. Visita la RAÍZ / PADRE actual
        result.add(node.getData());

        // 2. Visita recursivamente a cada uno de sus HIJOS de izquierda a derecha
        for (TreeNode<EducativeElement> son : node.getSons()) {
            collectPreOrderRecursive(son, result);
        }
    }
    /**
     * Guarda el estado actual de la lista de cursos en el almacenamiento persistente (JSON).
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
}
