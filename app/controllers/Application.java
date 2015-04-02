package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());//"Hybrid recommender system"));
    }

    public static Result results() {
        return ok(recommender.render());//"Hybrid recommender system"));
    }

    public static Result evaluation() {
        return ok(evaluation.render());//"Hybrid recommender system"));
    }

}
