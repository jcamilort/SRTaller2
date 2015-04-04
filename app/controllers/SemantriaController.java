package controllers;

import com.semantria.SentimentAnalizer;
import com.semantria.Session;
import com.semantria.interfaces.ISerializer;
import com.semantria.mapping.Document;
import com.semantria.mapping.configuration.Configuration;
import com.semantria.mapping.output.DocAnalyticData;
import com.semantria.serializer.JsonSerializer;
import org.json.simple.parser.ParseException;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.evaluation;
import views.html.index;
import views.html.recommender;
import static java.lang.System.out;
import recommender.DataLoader;

import java.io.IOException;

public class SemantriaController extends Controller {


    public static Result index() {

        SentimentAnalizer sa=SentimentAnalizer.getInstance();
        sa.analizeText();
        return ok(index.render());
    }

    public static Result sentiment() {

        SentimentAnalizer sa=SentimentAnalizer.getInstance();
        String t=request().getQueryString("text");
        if(t==null||t.isEmpty())
            t="all good";
        sa.getSentiment(t);

            DataLoader.cargarTips();

        return ok(index.render());
    }

}
