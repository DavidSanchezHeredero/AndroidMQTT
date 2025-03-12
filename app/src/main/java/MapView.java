package maestro.androide;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.reader.MapFile;

import java.io.File;

public class MapView extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Mapsforge
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);

        // Configurar el MapView
        mapView.setClickable(true);
        mapView.getModel().mapViewPosition.setCenter(new LatLong(40.4168, -3.7038)); // Coordenadas de Madrid
        mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

        // Crear TileCache
        TileCache tileCache = AndroidUtil.createTileCache(this,
                "mapCache",
                mapView.getModel().displayModel.getTileSize(),
                1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        // Cargar archivo de mapa (.map) desde la carpeta assets
        File mapFile = new File(getExternalFilesDir(null), "madrid.map");

        // Verificar si el archivo existe
        if (mapFile.exists()) {
            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
                    new MapFile(mapFile),
                    mapView.getModel().mapViewPosition,
                    AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

            // Añadir la capa de renderizado al MapView
            mapView.getLayerManager().getLayers().add(tileRendererLayer);
        } else {
            Toast.makeText(this, "Archivo de mapa no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
    }
}