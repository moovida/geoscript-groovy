package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The GeoJSONWriter UnitTest
 * @author Jared Erickson
 */
class GeoJSONWriterTestCase {

    @Test void write() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        String expected = ""

        // Write the Layer to a GeoJSON String
        GeoJSONWriter writer = new GeoJSONWriter()
        String geojson = writer.write(layer)
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")

        // Write Layer as GeoJSON to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        geojson = out.toString()
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")

        // Write Layer as GeoJSON to a File
        File file = File.createTempFile("layer",".json")
        writer.write(layer, file)
        geojson = file.text
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")
    }

}
