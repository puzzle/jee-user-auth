package ch.puzzle.jee.userauth.mappers.boundary;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

public class JsonMapperTest {
    private DummyMapper mapper;

    @Before
    public void setup() {
        mapper = new DummyMapper();
    }

    @Test
    public void shouldMapListToJsonArray() throws Exception {
        // given
        List<DummyEntity> list = Arrays.asList(new DummyEntity("Foo"), new DummyEntity("Bar"));

        // when
        JsonArray jsonArray = mapper.map(list);

        // then
        assertThat(jsonArray.getJsonObject(0).getString("name"), is("Foo"));
        assertThat(jsonArray.getJsonObject(1).getString("name"), is("Bar"));
    }

    @Test
    public void shouldMapJsonArrayToList() throws Exception {
        // given
        JsonObject foo = Json.createObjectBuilder().add("name", "Foo").build();
        JsonObject bar = Json.createObjectBuilder().add("name", "Bar").build();

        // when
        Collection<DummyEntity> list = mapper.map(Json.createArrayBuilder().add(foo).add(bar).build());

        // then
        assertThat(list, contains(hasProperty("name", is("Foo")), hasProperty("name", is("Bar"))));
    }

    public static class DummyMapper extends JsonMapper<DummyEntity> {
        @Override
        public JsonObject map(DummyEntity entity) {
            return Json.createObjectBuilder().add("name", entity.name).build();
        }

        @Override
        public DummyEntity map(JsonObject jsonObject) {
            return new DummyEntity(jsonObject.getString("name"));
        }
    }

    public static class DummyEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public DummyEntity(String name) {
            this.name = name;
        }
    }
}