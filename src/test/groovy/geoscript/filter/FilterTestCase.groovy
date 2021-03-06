package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*
import geoscript.layer.*
import geoscript.feature.Feature

class FilterTestCase {
    
    @Test void constructors() {
        Filter f1 = new Filter("name='foobar'")
        assertEquals "[ name = foobar ]", f1.toString()

        Filter f2 = new Filter("<filter><PropertyIsEqualTo><PropertyName>name</PropertyName><Literal>foobar</Literal></PropertyIsEqualTo></filter>")
        assertEquals "[ name = foobar ]", f2.toString()

        Filter f3 = new Filter(f1)
        assertEquals f1.toString(), f3.toString()

        Filter f4 = new Filter("CARPOOL/PERSON > 0.06")
        println f4.toString()
        assertEquals "[ (CARPOOL/PERSON) > 0.06 ]", f4.toString()
    }
    
    @Test void getCql() {
        Filter f = new Filter("name='foobar'")
        assertEquals "name = 'foobar'", f.cql
    }
    
    @Test void stringRepresentation() {
        Filter f = new Filter("name='foobar'")
        assertEquals "[ name = foobar ]", f.toString()
    }

    @Test void getXml() {
        Filter f = new Filter("name='foobar'")
        String actual = f.xml
        String expected = """<ogc:Filter xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
    <ogc:PropertyIsEqualTo>
        <ogc:PropertyName>name</ogc:PropertyName>
        <ogc:Literal>foobar</ogc:Literal>
    </ogc:PropertyIsEqualTo>
</ogc:Filter>
"""
        assertEquals expected, actual
    }

    @Test void bbox() {

        Filter f1 = Filter.bbox(new Bounds(10,20,30,40))
        assertEquals "BBOX(the_geom, 10.0,20.0,30.0,40.0)", f1.cql

        Filter f2 = Filter.bbox(new Bounds(-102, 43.5, -100, 47.5))
        assertEquals "BBOX(the_geom, -102.0,43.5,-100.0,47.5)", f2.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(f2)
        assertEquals 2, features.size()
        assertEquals 2, layer.getFeatures("BBOX(the_geom, -102.0,43.5,-100.0,47.5)").size()
    }

    @Test void contains() {
        Filter f1 = Filter.contains(Geometry.fromWKT("POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45))"))
        assertEquals "CONTAINS(the_geom, POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45)))", f1.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(Filter.contains(Geometry.fromWKT("POINT (-100 47)")))
        assertEquals 1, features.size()
        assertEquals "ND", features[0].get("STATE_ABBR")
    }

    @Test void dwithin() {
        Filter f1 = Filter.dwithin("the_geom", Geometry.fromWKT("POINT (-100 47)"), 10.2, "feet")
        assertEquals "DWITHIN(the_geom, POINT (-100 47), 10.2, feet)", f1.cql
    }

    @Test void crosses() {
        Filter f1 = Filter.crosses("the_geom", Geometry.fromWKT("LINESTRING (-104 45, -95 45)"))
        assertEquals "CROSSES(the_geom, LINESTRING (-104 45, -95 45))", f1.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(Filter.crosses(Geometry.fromWKT("LINESTRING (-108 47.1005, -102 47.5421, -95.1251 46.7851)")))
        assertEquals 3, features.size()
        def abbreviations = features.collect{it.get("STATE_ABBR")}
        assertTrue(abbreviations.contains("MT"))
        assertTrue(abbreviations.contains("ND"))
        assertTrue(abbreviations.contains("MN"))
    }

    @Test void intersects() {
        Filter f1 = Filter.intersects(Geometry.fromWKT("POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45))"))
        assertEquals "INTERSECTS(the_geom, POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45)))", f1.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(Filter.intersects(Geometry.fromWKT("POLYGON ((-108 47.1005, -102 47.5421, -95.1251 46.7851,  -95.125 44.45, -107 43.5, -108 47.1005))")))
        assertEquals 5, features.size()
        def abbreviations = features.collect{it.get("STATE_ABBR")}
        assertTrue(abbreviations.contains("MT"))
        assertTrue(abbreviations.contains("ND"))
        assertTrue(abbreviations.contains("MN"))
        assertTrue(abbreviations.contains("SD"))
        assertTrue(abbreviations.contains("WY"))
    }

    @Test void evaluate() {
        Filter filter = new Filter("name='foobar'")
        Feature feature1 = new Feature(['name':'foobar'], 'f1')
        assertTrue filter.evaluate(feature1)
        Feature feature2 = new Feature(['name':'test'], 'f2')
        assertFalse filter.evaluate(feature2)
    }

    @Test void equals() {
        Filter f1 = new Filter("name='foobar'")
        Filter f2 = new Filter("name='foobar'")
        Filter f3 = new Filter("name='test'")
        assertTrue f1 == f2
        assertFalse f1 == f3
        assertFalse f2 == f3
    }

    @Test void hashCodeTest() {
        Filter f1 = new Filter("name='foobar'")
        Filter f2 = new Filter("name='foobar'")
        Filter f3 = new Filter("name='test'")
        assertTrue f1.hashCode() == f2.hashCode()
        assertFalse f1.hashCode() == f3.hashCode()
        assertFalse f2.hashCode() == f3.hashCode()
    }

    @Test void plus() {
        Filter f1 = new Filter("name='foo'")
        Filter f2 = new Filter("name='bar'")
        Filter f3 = Filter.PASS
        assertEquals("[[ name = foo ] AND [ name = bar ]]", (f1 + f2).toString())
        assertEquals("[ name = foo ]", (f3 + f1).toString())
        // @TODO [[[ name = foo ] AND Filter.INCLUDE]]
        // assertEquals("[ name = foo ]", (f1 + f3).toString())
    }

    @Test void and() {
        Filter f1 = new Filter("name='foo'")
        Filter f2 = new Filter("name='bar'")
        Filter f3 = Filter.PASS
        assertEquals("[[ name = foo ] AND [ name = bar ]]", (f1.and(f2)).toString())
        assertEquals("[ name = foo ]", (f3.and(f1)).toString())
    }

    @Test void or() {
        Filter f1 = new Filter("name='foo'")
        Filter f2 = new Filter("name='bar'")
        assertEquals("[[ name = foo ] OR [ name = bar ]]", (f1.or(f2)).toString())
    }

    @Test void getNot() {
        Filter f1 = new Filter("name='foo'")
        assertEquals new Filter("name <> 'foo'"), f1.not
    }
}

