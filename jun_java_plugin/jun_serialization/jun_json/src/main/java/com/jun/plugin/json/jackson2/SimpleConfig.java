package com.jun.plugin.json.jackson2;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jun.plugin.json.jackson2.pojo.EmptyObjet;
import com.jun.plugin.json.jackson2.pojo.MyValue;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by luozhen on 2018/6/22.
 */
public class SimpleConfig {


    public static class Point {
        public int x, y;

        protected Point() { } // for deser
        public Point(int x0, int y0) {
            x = x0;
            y = y0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point other = (Point) o;
            return (other.x == x) && (other.y == y);
        }

        @Override
        public String toString() {
            return String.format("[x=%d, y=%d]", x, y);
        }
    }

    static class GeneralEmpty<T> {
        // 09-Feb-2017, tatu: Should only need annotation either for field OR setter, not both:
        // @JsonSetter(nulls=JsonSetter.Nulls.AS_EMPTY)
        T value;

        @JsonSetter(nulls= Nulls.AS_EMPTY)
        public void setValue(T v) {
            value = v;
        }
    }



    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void log(String msg1, Object obj) {
        System.out.println(msg1);
        System.out.println(obj);
    }

    public static void nullObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        EmptyObjet obj = new EmptyObjet();
        //?????????????????????????????????????????????????????????
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        String json = mapper.writeValueAsString(obj);
        log("????????????Json??? " + json);

        //?????????????????????
        // ?????????????????? ("") ???????????????:
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);//?????????????????????
        GeneralEmpty<Point> result = mapper.readerFor(new TypeReference<GeneralEmpty<Point>>() { })
                                           .readValue("{\"value\":\"\"}");
        Point p = result.value;//p???null
        log("?????????????????????Point:", p);
    }

    public static void allowComments() throws Exception {
        final String JSON = "[ /* foo */ 7 ]";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        ObjectReader reader = mapper.readerFor(int[].class);
        int[] value = reader.readValue(JSON); //value=7
        log("????????????JSON?????????" + JSON, value[0]);
    }

    /**
     * ??????????????????JSON???????????????????????????????????????
     * @throws Exception
     */
    public static void unquiFiledNames() throws Exception {
        final JsonFactory f = new JsonFactory();
        f.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        String JSON = "{a: 1, _foo:true, $:\"money!\", \" \":null }";
        //????????????json: ?????????????????????????????????
        JsonParser p = f.createParser(JSON.getBytes());
        JsonToken token = p.nextToken();
        log(token.name());
        while(null != (token = p.nextToken())) {
            log(token.name());
            if (token.name().toString().equals("FIELD_NAME")) {
                log(p.getCurrentName());
            }
        }
    }


    /**
     *   unicode???????????????
     *   ["chars: [??]/[???]"]
         {"fun:??:???":true}
         ??????JsonGenerator.Feature.ESCAPE_NON_ASCII??????
         ["chars: [\u00A0]/[\u1234]"]
         {"fun:\u0088:\u3456":true}
     *
     * @throws Exception
     */
    public static void escapeNonAscii() throws Exception {

        final String VALUE = "\u5f20\u4e09";
        final String KEY = "\u59d3\u540d";
        final JsonFactory f = new JsonFactory();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JsonGenerator g;

        g = f.createGenerator(bytes, JsonEncoding.UTF8);
        g.writeStartObject();
        g.writeFieldName(KEY);
        g.writeString(VALUE);
        g.writeEndObject();
        g.close();
        String json = bytes.toString("UTF-8");
        log(json);//????????? {"??????":"??????"}


        bytes = new ByteArrayOutputStream();

        g = f.createGenerator(bytes, JsonEncoding.UTF8);
        g.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        g.writeStartObject();
        g.writeFieldName(KEY);
        g.writeString(VALUE);
        g.writeEndObject();
        g.close();
        json = bytes.toString("UTF-8");
        log(json);//????????? {"\u59D3\u540D":"\u5F20\u4E09"}
    }


    public static void preetyPrint(ObjectMapper mapper) throws Exception {
        MyValue value = new MyValue();
        value.setName("?????????");
        value.setAge(100);
        String json = mapper.writeValueAsString(value);
        log("?????????JSON??? ", json);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        json = mapper.writeValueAsString(value);
        log("???????????????JSON: ", json);
    }

    public static void dateType(ObjectMapper mapper) throws Exception{
        Date date = new Date();
        log("?????????????????????" + System.currentTimeMillis());
        String json = mapper.writeValueAsString(date);
        log("?????????Date???JSON???", json);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(date);
        log("??????WRITE_DATES_AS_TIMESTAMPS???Date???JSON???", json);
    }


    public static void unknowProperi(ObjectMapper mapper) throws Exception{
        // ???????????????????????????????????????:
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        preetyPrint(mapper);
        nullObject();
        dateType(mapper);
        allowComments();
        unquiFiledNames();
        escapeNonAscii();
    }

}
