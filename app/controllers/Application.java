package controllers;

import models.User;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {

        User testUser=new User();
        testUser.setName("Juanito");
        testUser.setUser_id("123");
        response().setCookie("user_id","123");

        return ok(index.render(testUser));//"Hybrid recommender system"));
    }

    public static Result results() {
        return ok(recommender.render());//"Hybrid recommender system"));
    }

    public static Result search()
    {
        DynamicForm data = Form.form().bindFromRequest();
        long lat=Long.parseLong(data.get("latitude"));
        long longi=Long.parseLong(data.get("longitude"));
        String hora=data.get("hour");
        Http.Cookie result = request().cookie("user_id");
        //User logged=User


        return ok(search.render(null));
    }
    public static Result evaluation() {
        return ok(evaluation.render());//"Hybrid recommender system"));
    }

}
