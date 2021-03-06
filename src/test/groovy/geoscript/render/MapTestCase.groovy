package geoscript.render

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.*
import geoscript.proj.Projection
import geoscript.geom.Bounds
import geoscript.style.Stroke
import geoscript.style.Fill

/**
 * The Map UnitTest
 * @author Jared Erickson
 */
class MapTestCase {

    @Test void proj() {
        Map map = new Map();
        map.proj = new Projection("EPSG:2927")
        assertEquals("EPSG:2927", map.proj.id)
        map.proj = "EPSG:4326"
        assertEquals("EPSG:4326", map.proj.id)
    }

    @Test void layer() {
        Map map = new Map()
        assertEquals(0, map.layers.size())
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        map.addLayer(shp)
        assertEquals(1, map.layers.size())
        map.layers = [shp]
        assertEquals(1, map.layers.size())
        map.close()
    }

    @Test void renderToImage() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = File.createTempFile("map",".png")
        println("renderToImage: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToImageWithMapNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.renderToImage()
        assertNotNull(image)

        File out = File.createTempFile("map",".png")
        println("renderToImageWithMapNoProjection: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToImageWithMapBoundsNoProjection() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.addLayer(shp)
        map.bounds = new Bounds(-126, 45.315, -116, 50.356)
        def image = map.renderToImage()
        assertNotNull(image)

        File out = File.createTempFile("map",".png")
        println("renderToImageWithMapBoundsNoProjection: ${out}")
        javax.imageio.ImageIO.write(image, "png", out);
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToFile() {

        File out = File.createTempFile("map",".png")
        println("renderToFile: ${out}")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.render(out)
        assertTrue(out.exists())
        map.close()
    }

    @Test void renderToOutputStream() {
        File f = File.createTempFile("map",".png")
        println("renderToOutputStream: ${f}")
        FileOutputStream out = new FileOutputStream(f)

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        def image = map.render(out)
        out.close()
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToPdf() {
        File f = File.createTempFile("map",".pdf")
        println("renderToPdf: ${f}")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"pdf", layers:[shp])
        map.addLayer(shp)
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToSvg() {
        File f = File.createTempFile("map",".svg")
        println("renderToSvg: ${f}")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"svg", layers:[shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToJpeg() {
        File f = File.createTempFile("map",".jpeg")
        println("renderToJpeg: ${f}")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"jpeg", layers: [shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void renderToGif() {
        File f = File.createTempFile("map",".gif")
        println("renderToGif: ${f}")

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        shp.style = new Fill("white") + new Stroke("#CCCCCC", 0.1)

        Map map = new Map(type:"gif", layers: [shp])
        def image = map.render(f)
        assertTrue(f.exists())
        map.close()
    }

    @Test void getScaleDenominator() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        assertEquals(38273743.41534821, map.scaleDenominator, 0.01)
    }

    @Test void getBounds() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)

        Map map = new Map()
        map.proj = new Projection("EPSG:2927")
        map.addLayer(shp)
        map.bounds = shp.bounds
        assertEquals(shp.bounds.minX, map.bounds.minX, 0.01)
        assertEquals(shp.bounds.maxX, map.bounds.maxX, 0.01)
        assertEquals(shp.bounds.minY, map.bounds.minY, 0.01)
        assertEquals(shp.bounds.maxY, map.bounds.maxY, 0.01)
        assertEquals(shp.bounds.proj.id, map.bounds.proj.id)
    }
}