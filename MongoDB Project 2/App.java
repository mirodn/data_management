package ch.zhaw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App {
        public static void main(String[] args) {

                // disable logging
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                lc.getLogger("org.mongodb.driver").setLevel(Level.OFF);

                ConnectionString connectionString = new ConnectionString(
                                "mongodb+srv://dumanmir:data4Collector@cluster0.qa6kysp.mongodb.net/?retryWrites=true&w=majority");
                MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(connectionString)
                                .serverApi(ServerApi.builder()
                                                .version(ServerApiVersion.V1)
                                                .build())
                                .build();
                MongoClient mongoClient = MongoClients.create(settings);
                MongoDatabase database = mongoClient.getDatabase("HS22");
                MongoCollection<Document> col = database.getCollection("food");

                Scanner keyScan = new Scanner(System.in);
                int punkte = 0;
                int antwortNr;
                String name;

                // start quiz
                System.out.println("Willkommen im FoodQuiz. Bitte gib deinen Namen ein.");
                System.out.print("> ");
                name = keyScan.nextLine();

                System.out.println("\n Wähle eine Kategorie");

                // list of categories
                List<String> liste = Arrays.asList("Kalorien", "Fett", "Protein", "Salz", "Eisen");
                for (int i = 0; i < liste.size(); i++) {
                        System.out.println((i + 1) + ") " + liste.get(i));
                }
                int kategorieSelected = keyScan.nextInt() - 1;
                // start timer
                long startTime = System.currentTimeMillis();
                String kategorie = liste.get(kategorieSelected);

                // five questions
                for (int w = 0; w < 5; w++) {

                        System.out.println("Frage " + (w + 1) + " von 5: Welches Lebensmittel hat den höchsten "
                                        + kategorie
                                        + "anteil?");

                        // data for food 1
                        AggregateIterable<Document> food1 = col.aggregate(Arrays.asList(new Document(
                                        "$match",
                                        new Document(kategorie,
                                                        new Document("$not",
                                                                        new Document("$eq", 0L)))),
                                        new Document("$sample",
                                                        new Document("size", 1L))));
                        Document food = food1.first();

                        // half the value
                        double value = Double.parseDouble(food.get(kategorie).toString());
                        double value50 = value * 0.5;
                        // pro 100g oder 100ml
                        String Bezugseinheit = food.get("Bezugseinheit").toString();

                        // data for food 2 & 3 with HALF THE VALUE
                        AggregateIterable<Document> twoMoreFoods = col.aggregate(Arrays.asList(new Document("$match",
                                        new Document("Bezugseinheit",
                                                        new Document("$eq", Bezugseinheit))
                                                        .append(kategorie,
                                                                        new Document("$not",
                                                                                        new Document("$size", 0L))
                                                                                        .append("$ne", 0L)
                                                                                        .append("$lt", value50))),
                                        new Document("$sample",
                                                        new Document("size", 2L))));

                        ArrayList<Document> threeFoods = twoMoreFoods.into(new ArrayList<Document>());
                        threeFoods.add(food);
                        String correctAnswer = threeFoods.get(2).get("Name").toString();

                        // print food
                        Collections.shuffle(threeFoods);
                        for (int i = 0; i < threeFoods.size(); i++) {
                                System.out.println(
                                                (i + 1) + ": " + threeFoods.get(i).get("Name"));
                        }

                        // enter answer
                        System.out.print("> ");
                        antwortNr = keyScan.nextInt();
                        String antwort = threeFoods.get(antwortNr - 1).get("Name").toString();

                        // check answer
                        if (antwort.equals(correctAnswer)) {
                                System.out.print("Korrekt. ");
                                punkte++;
                        } else {
                                System.out.println("Leider falsch. ");
                        }
                        System.out.println(
                                        "Die Produkte enthalten pro 100g essbarer Anteil die folgende Menge Fett: ");
                        for (int i = 0; i < threeFoods.size(); i++) {
                                System.out.println((i + 1) + ": " + threeFoods.get(i).get(kategorie));
                        }
                }

                // end timer
                long stopTime = System.currentTimeMillis();
                long time = (stopTime - startTime);

                // print players result
                System.out.println(name + ", du hast " + punkte + " Punkte erreicht und " + time
                                + " ms benötigt.");
                System.out.println("\n");

                // save results in ranking
                Bestenliste ranking = new Bestenliste(name, punkte, time, kategorie);
                Gson gson = new GsonBuilder().create();

                // open collection
                MongoCollection<Document> co = database.getCollection("bestenliste");

                // insert result
                String rankingJson = gson.toJson(ranking); // object to json
                Document rankingDoc = Document.parse(rankingJson); // json to bson
                InsertOneResult result = co.insertOne(rankingDoc);
                System.out.println(result.toString() + "\n");

                

                AggregateIterable<Document> threeBestRankings = co.aggregate(Arrays.asList(
                                new Document("$sort",
                                                new Document("punkte", -1L)
                                                                .append("timer", 1L)),
                                new Document("$limit", 3L)));

                // print best list
                System.out.println("Bestenliste:\n");
                for (Document d : threeBestRankings) {
                        System.out.print(d.get("name") + ": (" + d.get("punkte") + ") Punkte.");
                        System.out.println(
                                        " Zeit: " + d.get("timer") + " (Kategorie " + d.get("kategorie")
                                                        + ")");
                }
                keyScan.close();

        }
}
