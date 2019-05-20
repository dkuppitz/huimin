package com.stackoverflow.huimin;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Column;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.constant;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

public class App {

    public static void main(final String... args) {

        final GraphTraversalSource g = TinkerGraph.open().traversal();

        final List<String> names = new ArrayList<String>() {{
            add("marko");
            add("vadas");
        }};

        // add 2 vertices here
        g.addV("person")
                .property("name", "marko")
                .property("lang", "java")
         .addV("person")
                .property("name", "vadas")
                .property("lang", "c++").iterate();

        // property map
        final Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>()
        {{
            put("marko", new HashMap<String, String>(){{
                put("newProp", "a");
                put("lang", "ruby");
            }});
            put("vadas", new HashMap<String, String>(){{
                put("newProp", "b");
                put("lang", "python");
            }});
        }};

        g.V().has("person", "name", P.within(names)).as("v").
                flatMap(constant(maps).unfold().
                        where(P.eq("v")).
                          by(Column.keys).
                          by("name").
                        select(Column.values).
                        unfold()).as("kv").
                select("v").
                    property(select("kv").by(Column.keys), select("kv").by(Column.values)).
                iterate();

        g.V().valueMap().forEachRemaining(System.out::println);
    }
}
