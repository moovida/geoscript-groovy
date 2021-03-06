package geoscript.feature

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*

/**
 * The Schema UniTest
 */
class SchemaTestCase {

    @Test void constructors() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets geom: Point, name: String, price: Float", s1.toString()
        assertEquals "http://geoscript.org/feature", s1.uri

        Schema s2 = new Schema("widgets", [["geom","Point"], ["name","string"], ["price","float"]])
        assertEquals "widgets geom: Point, name: String, price: Float", s2.toString()
        assertEquals "http://geoscript.org/feature", s2.uri

        Schema s3 = new Schema("widgets", [[name: "geom",type: "Point"], [name: "name", type: "string"], [name: "price", type: "float"]])
        assertEquals "widgets geom: Point, name: String, price: Float", s3.toString()
        assertEquals "http://geoscript.org/feature", s3.uri

        Schema s4 = new Schema("widgets", "geom:Point:srid=4326,name:String,price:float")
        assertEquals "widgets geom: Point(EPSG:4326), name: String, price: Float", s4.toString()
        assertEquals "http://geoscript.org/feature", s4.uri

        Schema s5 = new Schema("widgets", "geom:Point:srid=4326,name:String,price:float", "http://geotools.org/feature")
        assertEquals "widgets geom: Point(EPSG:4326), name: String, price: Float", s5.toString()
        assertEquals "http://geotools.org/feature", s5.uri

        Schema s6 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")], "http://geotools.org/feature")
        assertEquals "widgets geom: Point, name: String, price: Float", s6.toString()
        assertEquals "http://geotools.org/feature", s6.uri
    }

    @Test void getName() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets", s1.name
    }

    @Test void getGeom() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Field field = s1.geom
        assertEquals "geom: Point", field.toString()

        Schema s2 = new Schema("widgets", [new Field("name","string"), new Field("price","float")])
        assertNull s2.geom
    }

    @Test void getProj() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        assertEquals "EPSG:4326", s1.proj.id

        Schema s2 = new Schema("widgets", [new Field("name","string"), new Field("price","float")])
        assertNull s2.proj
    }

    @Test void field() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "geom: Point", s1.field("geom").toString()
        assertEquals "name: String", s1.field("name").toString()
        assertEquals "price: Float", s1.field("price").toString()
    }

    @Test void get() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "geom: Point", s1.get("geom").toString()
        assertEquals "name: String", s1.get("name").toString()
        assertEquals "price: Float", s1.get("price").toString()
    }

    @Test void getFields() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        List<Field> fields = s1.fields
        assertEquals 3, fields.size()
        assertEquals "geom: Point", fields[0].toString()
        assertEquals "name: String", fields[1].toString()
        assertEquals "price: Float", fields[2].toString()
    }

    @Test void feature() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = s1.feature([new Point(111,-47), "House", 12.5],"house1")
        assertNotNull(f1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f1.toString()

        Feature f2 = s1.feature(["geom": new Point(111,-47), "name": "House", "price": 12.5],"house1")
        assertNotNull(f2)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f2.toString()
    }

    @Test void toStringTest() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets geom: Point, name: String, price: Float", s1.toString()
    }

    @Test void reproject() {
        Schema s1 = new Schema("houses", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        assertEquals "houses geom: Point(EPSG:2927), name: String, price: Float", s1.toString()
        Schema s2 = s1.reproject("EPSG:4326","houses in lat/lon")
        assertEquals "houses in lat/lon geom: Point(EPSG:4326), name: String, price: Float", s2.toString()
    }

    @Test void changeGeometryType() {
        Schema s1 = new Schema("houses", [new Field("geom","Polygon", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        assertEquals "houses geom: Polygon(EPSG:2927), name: String, price: Float", s1.toString()
        Schema s2 = s1.changeGeometryType("Polygon","houses buffered")
        assertEquals "houses buffered geom: Polygon(EPSG:2927), name: String, price: Float", s2.toString()
    }

}

