package geoscript.workspace

import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Layer
import org.geotools.data.DataStore
import org.geotools.feature.FeatureIterator
import org.geotools.feature.FeatureCollection
import org.opengis.feature.simple.SimpleFeatureType
import org.geotools.data.collection.ListFeatureCollection
import org.geotools.data.DataStoreFinder

/**
 * A Workspace is a container of Layers.
 * @author Jared Erickson
 */
class Workspace {

    /**
     * The GeoTools DataStore
     */
    DataStore ds

    /**
     * Create a new Workspace wrapping a GeoTools DataStore
     * @param The GeoTools DataStore
     */
    Workspace(DataStore ds) {
        this.ds = ds
    }

    /**
     * Create a new Workspace with an in Memory Workspace
     */
    Workspace() {
        this(new Memory().ds)
    }

    /**
     * Create a new Workspace from a Map of parameters.
     * @param params The Map of parameters
     */
    Workspace(Map params) {
        this(DataStoreFinder.getDataStore(params))
    }

    /**
     * Create a new Workspace from a parameter string.  The parameter string is space delimited collection of key=value
     * parameters.  If the key or value contains spaces they must be single quoted.
     * @param paramString The parameter string.
     */
    Workspace(String paramString) {
        this(getParametersFromString(paramString))
    }

    /**
     * Get the format
     * @return The Workspace format name
     */
    String getFormat() {
        ds.getClass().getName()
    }

    /**
     * Get a List of Layer names
     * @return A List of Layer names
     */
    List<String> getNames() {
        ds.typeNames.collect{it.toString()}
    }

    /**
     * Get a List of Layers
     * @return A List of Layers
     */
    List<Layer> getLayers() {
        getNames().collect{name -> get(name)}
    }
    
    /**
     * Get a Layer by name
     * @param The Layer name
     * @return A Layer
     */
    Layer get(String name) {
        new Layer(this, ds.getFeatureSource(name))
    }
    
    
    /**
     * Another way to get a Layer by name.
     * <p><code>Layer layer = workspace["hospitals"]</code><p>
     * @param The Layer name
     * @return A Layer
     */
    Layer getAt(String name) {
        get(name)
    }

    /**
     * Create a Layer with a List of Fields
     * @param name The new Layer name
     * @param fields A List of Fields (defaults to a "geom", "Geometry" Field)
     * @return A new Layer
     */
    Layer create(String name, List<Field> fields = [new Field("geom","Geometry")]) {
        create(new Schema(name, fields))
    }

    /**
     * Create a Layer with a Schema
     * @param schema The Schema (defaults to a Schema with a single Geometry Field
     * named "geom"
     * @return A new Layer
     */
    Layer create(Schema schema = new Schema([new Field("geom","Geometry")])) {
        ds.createSchema(schema.featureType)
        get(schema.name)
    }

    /**
     * Add a Layer to the Workspace
     * @param layer The Layer to add
     * @return The newly added Layer
     */
    Layer add(Layer layer) {
        add(layer, layer.name)
    }

    /**
     * Add a Layer as a name to the Workspace
     * @param layer The Layer to add
     * @param name The new name of the Layer
     * @param chunk The number of Features to add in one batch
     * @return The newly added Layer
     */
    Layer add(Layer layer, String name, int chunk=1000) {
        List<Field> flds = layer.schema.fields.collect {
            if (it.isGeometry()) {
                return new Field(it.name, it.typ, layer.proj)
            }
            else {
                return new Field(it.name, it.typ)
            }
        }
        Layer l = create(name, flds)
        FeatureIterator it = layer.fs.getFeatures().features()
        try {
            while(true) {
                def features = readFeatures(it, layer.fs.schema, chunk)
                if (features.isEmpty()) break
                l.fs.addFeatures(features)
            }
        }
        finally {
            it.close()
        }
        l
    }

    /**
     * Read Features from a FeatureIterator in batches
     * @param it The FeatureIterator
     * @param type The SimpleFeatureType
     * @param chunk The number of Features to read in one batch
     * @return A FeatureCollection
     */
    private FeatureCollection readFeatures(FeatureIterator it, SimpleFeatureType type, int chunk) {
        int i = 0
        def features = new ListFeatureCollection(type)
        while (it.hasNext() && i < chunk) {
            features.add(it.next())
            i++
        }
        features
    }

    /**
     * Closes the Workspace by disposing of any resources.
     */
    void close() {
        ds.dispose()
    }

    /**
     * Get a Map from a parameter string: "dbtype=h2 database=roads.db"
     * @param str The parameter string is a space delimited collection of key=value parameters.  Use single
     * quotes around key or values with internal spaces.
     * @return A Map of parameters
     */
    private static Map getParametersFromString(String str) {
        Map params = [:]
        if (str.indexOf("=") == -1) {
            if (str.endsWith(".shp")) {
                params.put("url", new File(str).toURL())
            } else {
                throw new IllegalArgumentException("Unknown Workspace parameter string: ${str}")
            }
        }
        else {
            str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
                def parts = it.split("=")
                def key = parts[0].trim()
                if ((key.startsWith("'") && key.endsWith("'")) ||
                        (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1)
                }
                def value = parts[1].trim()
                if ((value.startsWith("'") && value.endsWith("'")) ||
                        (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1)
                }
                params.put(key, value)
            }
        }
        return params
    }

    /**
     * Get a List of available GeoTools workspaces (aka DataStores)
     * @return A List of available GeoTools workspace
     */
    static List getWorkspaceNames() {
        DataStoreFinder.availableDataStores.collect{ds ->
            ds.displayName
        }
    }

    /**
     * Get the list of connection parameters for the given workspace
     * @param name The workspace name
     * @return A List of parameters which are represented as a Map with key, type, required keys
     */
    static List getWorkspaceParameters(String name) {
        def ds = DataStoreFinder.availableDataStores.find{ds ->
            if (ds.displayName.equalsIgnoreCase(name)) {
                return ds
            }
        }
        ds.parametersInfo.collect{param ->
            [key: param.name, type: param.type.name, required: param.required]
        }
    }
}
