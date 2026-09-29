package co.edu.uptc.persistence;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import co.edu.uptc.interfaces.EducativeElement;
import co.edu.uptc.model.Course;
import co.edu.uptc.model.Lessons;
import co.edu.uptc.model.Module;
import co.edu.uptc.model.TreeNode;

final class CourseJsonAdapter {
    private CourseJsonAdapter() {
    }

    static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Course.class, (JsonSerializer<Course>) CourseJsonAdapter::serializeCourse)
                .registerTypeAdapter(EducativeElement.class,
                        (JsonDeserializer<EducativeElement>) CourseJsonAdapter::deserializeElement)
                .create();
    }

    private static JsonElement serializeCourse(
            Course course, Type type, JsonSerializationContext context) {
        JsonObject result = serializeCourseFields(course);
        if (course.getRoot() != null) {
            result.add("root", serializeNode(course.getRoot(), course, context));
        }
        return result;
    }

    private static JsonObject serializeCourseFields(Course course) {
        JsonObject result = new JsonObject();
        result.addProperty("id", course.getId());
        result.addProperty("title", course.getTitle());
        result.addProperty("description", course.getDescription());
        result.addProperty("_type", "COURSE");
        return result;
    }

    private static JsonElement serializeNode(
            TreeNode<EducativeElement> node, Course rootCourse, JsonSerializationContext context) {
        if (node == null) {
            return null;
        }
        JsonObject result = new JsonObject();
        EducativeElement element = node.getData();
        if (element == rootCourse) {
            result.add("data", serializeCourseFields(rootCourse));
        } else if (element != null) {
            JsonObject serializedElement = context.serialize(element, element.getClass()).getAsJsonObject();
            serializedElement.addProperty("_type", element instanceof Course
                    ? "COURSE" : element instanceof Lessons ? "LESSON" : "MODULE");
            result.add("data", serializedElement);
        } else {
            result.add("data", null);
        }

        com.google.gson.JsonArray sons = new com.google.gson.JsonArray();
        if (node.getSons() != null) {
            for (TreeNode<EducativeElement> son : node.getSons()) {
                sons.add(serializeNode(son, rootCourse, context));
            }
        }
        result.add("sons", sons);
        return result;
    }

    private static EducativeElement deserializeElement(
            JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        JsonElement kind = object.remove("_type");
        Class<? extends EducativeElement> elementClass;
        if (kind != null && "COURSE".equals(kind.getAsString())
                || kind == null && object.has("root")) {
            elementClass = Course.class;
        } else if (kind != null && "LESSON".equals(kind.getAsString())
                || kind == null && object.has("duration")) {
            elementClass = Lessons.class;
        } else {
            elementClass = Module.class;
        }
        return context.deserialize(object, elementClass);
    }
}
