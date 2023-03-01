package com.studentapp.studentinfo;

import com.studentapp.constants.EndPoints;
import com.studentapp.model.StudentPojo;
import com.studentapp.testbase.TestBase;
import com.studentapp.utils.TestUtils;
import io.restassured.http.ContentType;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Title;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasValue;


@RunWith(SerenityRunner.class)
public class StudentCURDTest extends TestBase {
    static String firstName = "priarl" + TestUtils.getRandomValue();
    static String lastName = "patel" + TestUtils.getRandomValue();
    static String programme = "Mechanical Engineering";
    static String email = "priarlpat" + TestUtils.getRandomValue() + "@yahoo.com";
    static Object studentId;

    @Title("This will create a new student")
    @Test
    public void test001() {

        List<String> courses = new ArrayList<>();
        courses.add("testing");
        courses.add("accounting");
        courses.add("finance");

        StudentPojo pojo = new StudentPojo();
        pojo.setFirstName(firstName);
        pojo.setLastName(lastName);
        pojo.setEmail(email);
        pojo.setProgramme(programme);
        pojo.setCourses(courses);

        SerenityRest.given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(pojo)
                .when()
                .post()
                .then().statusCode(201);

    }

    @Title("Verify if student is created")
    @Test
    public void test002() {
        String part1 = "findAll{it.lastName=='";
        String part2 = "'}.get(0)";

        HashMap<String, ?> studentMapData = SerenityRest.given()
                .log().all()
                .when()
                .get(EndPoints.GET_ALL_STUDENT)
                .then().statusCode(200)
                .extract().path(part1 + lastName + part2);
        Assert.assertThat(studentMapData, hasValue(lastName));
        studentId = studentMapData.get("id");
        System.out.println(studentId);
    }

    @Title("Update the user and verify the updated information")
    @Test
    public void test003() {
        lastName = lastName + "princesses";


        List<String> courses = new ArrayList<>();
        courses.add("testing");
        courses.add("accounting");
        courses.add("finance");

        StudentPojo pojo = new StudentPojo();
        pojo.setFirstName(firstName);
        pojo.setLastName(lastName);
        pojo.setEmail(email);
        pojo.setProgramme(programme);
        pojo.setCourses(courses);


        SerenityRest.given()
                .log().all()
                .header("Content-Type", "application/json")
                .pathParam("studentID", studentId)
                .body(pojo)
                .when()
                .put(EndPoints.UPDATE_STUDENT_BY_ID)
                .then().statusCode(200);

        String part1 = "findAll{it.lastName=='";
        String part2 = "'}.get(0)";

        HashMap<String, ?> studentMapData = SerenityRest.given()
                .when()
                .get(EndPoints.GET_ALL_STUDENT)
                .then().statusCode(200)
                .extract().path(part1 + lastName + part2);
        Assert.assertThat(studentMapData, hasValue(lastName));

    }

    @Title("Delete the student and verify if the student is deleted")
    @Test
    public void test004() {

        SerenityRest.given()
                .pathParam("studentID", studentId)
                .when()
                .delete(EndPoints.DELETE_STUDENT_BY_ID)
                .then().log().all().statusCode(204);

        SerenityRest.given()
                .pathParam("studentID", studentId)
                .when()
                .get(EndPoints.GET_SINGLE_STUDENT_BY_ID)
                .then().log().all().statusCode(404);

    }


}
