package geoscript.style.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.style.*

/**
 * The SLDWriter UnitTest
 * @author Jared Erickson
 */
class SLDWriterTestCase {

    private String NEW_LINE = System.getProperty("line.separator")

    private String expectedSld = """<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
  <sld:Name>Default Styler</sld:Name>
  <sld:Title/>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Rule>
      <sld:PolygonSymbolizer>
        <sld:Fill>
          <sld:CssParameter name="fill">#f5deb3</sld:CssParameter>
        </sld:Fill>
      </sld:PolygonSymbolizer>
      <sld:LineSymbolizer>
        <sld:Stroke>
          <sld:CssParameter name="stroke">#a52a2a</sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
</sld:UserStyle>""".trim().replaceAll("\n","")

    @Test void writeToOutputStream() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        SLDWriter writer = new SLDWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(sym, out)
        String sld = out.toString().trim().replaceAll(NEW_LINE,"")
        assertNotNull sld
        assertTrue sld.length() > 0
        assertEquals expectedSld, sld
    }

    @Test void writeToFile() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        SLDWriter writer = new SLDWriter();
        File file = File.createTempFile("simple",".sld")
        writer.write(sym, file)
        String sld = file.text.trim().replaceAll(NEW_LINE,"")
        assertNotNull sld
        assertTrue sld.length() > 0
        assertEquals expectedSld, sld
    }

    @Test void writeToString() {
        Symbolizer sym = new Fill("wheat") + new Stroke("brown")
        SLDWriter writer = new SLDWriter();
        String sld = writer.write(sym).trim().replaceAll(NEW_LINE,"")
        assertNotNull sld
        assertTrue sld.length() > 0
        assertEquals expectedSld, sld
    }
}
