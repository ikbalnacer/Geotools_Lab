package org.geotloos.engobe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JProgressWindow;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;


public class SelectionLab {
	String featureID =null;
	private Localisation loc = new Localisation();
	private Localisation loc1 = new Localisation();
    private JMapFrame mapFrame;
    private SimpleFeatureSource featureSource;
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    FileDataStore Dstore =null;
    int i =0;
    MapContent map = new MapContent();
    public static void main(String[] args) throws Exception {
        SelectionLab me = new SelectionLab();

      
        me.displayShapefile();
    }
    
    public void ajouternewFile(File file) throws IOException{
    	
        Dstore = FileDataStoreFinder.getDataStore(file);
        featureSource = Dstore.getFeatureSource();
        create_map(1, featureSource);
    }
    
    public void displayShapefile() throws Exception {
    	
        File file1 = new File("C:\\Users\\honey\\Documents\\yeah01\\constantinePOLY1.shp");
       	File file2 = new File("C:\\Users\\honey\\Documents\\yeah01\\constineLine1.shp");
       	File file = new File("C:\\Users\\honey\\Documents\\yeah01\\Constan1.shp"); 
    	
    	//File file = new File("C:\\Users\\honey\\Downloads\\Compressed\\50m_cultural\\ne_50m_admin_0_countries_lakes.shp");
    	/*File file1 = new File("C:\\Users\\honey\\Documents\\yeah\\natural.shp");
    	File file2 = new File("C:\\Users\\honey\\Documents\\yeah\\places.shp");
        File file3 = new File("C:\\Users\\honey\\Documents\\yeah\\points.shp");*/
      //  File file4 = new File("C:\\Users\\honey\\Documents\\yeah\\railways.shp");
    	//File file5 = new File("C:\\Users\\honey\\Documents\\yeah\\roads.shp");
    //	File file6 = new File("C:\\Users\\honey\\Documents\\yeah\\waterways.shp");
    	
        ajouternewFile(file);
       ajouternewFile(file1);
       ajouternewFile(file2);
     //  ajouternewFile(file3);
        //ajouternewFile(file4);
       // ajouternewFile(file5);
      // ajouternewFile(file6);
        
        mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);
        mapFrame.enableLayerTable(true);
        JToolBar toolBar = mapFrame.getToolBar();
        JButton btn = new JButton("Localiser");
        toolBar.addSeparator();
        toolBar.add(btn);
    
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mapFrame.getMapPane().setCursorTool(
                        new CursorTool() {
                            @Override
                            public void onMouseClicked(MapMouseEvent ev) {
                            	try {
									try {
										LocaliserFeature(ev);
										
									} catch (MalformedURLException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								} catch (SchemaException e) {
									e.printStackTrace();
								}
                            }
                        });
            }
        });
        mapFrame.setSize(600, 600);
        mapFrame.setVisible(true);  
        
    }
    private int countFeatures() throws Exception {
        
        Filter filter = CQL.toFilter("include");
        SimpleFeatureCollection features = featureSource.getFeatures(filter);

        return  features.size();
    }
    
   
    void LocaliserFeature(MapMouseEvent ev) throws SchemaException, IOException {
    	  GeometryBuilder geom = new GeometryBuilder();
          SimpleFeatureStore store = (SimpleFeatureStore) featureSource;
          if(i!=0){
              SimpleFeatureType featureType = store.getSchema();
             
              SimpleFeatureBuilder build = new SimpleFeatureBuilder(featureType);
              List<SimpleFeature> list = new ArrayList<SimpleFeature>();

          	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
              
          	Coordinate[] coords  =
          	 new Coordinate[] {new Coordinate(loc.getLatitude(),loc.getLongitude()), new Coordinate(ev.getMapPosition().getX(),ev.getMapPosition().getY())};

          	LineString line = geometryFactory.createLineString(coords);
          	
              list.add(build.buildFeature("fid3", new Object[]{ line, "helo" }));

               SimpleFeatureCollection collection = new ListFeatureCollection(featureType, list);
              
               Transaction transaction = new DefaultTransaction("Add Example");
               store.setTransaction( transaction );
               try {
                    store.addFeatures( collection );
                    transaction.commit(); // actually writes out the features in one go
               }
               catch( Exception eek){
                    transaction.rollback();
                }
          	
          }else{
          	double latitude = ev.getMapPosition().getX();
              double longitude = ev.getMapPosition().getY();
              loc.setState(true);
              loc.setLongitude(longitude);
              loc.setLatitude(latitude);
              i++;
              SimpleFeatureType featureType = store.getSchema();
               
              SimpleFeatureBuilder build = new SimpleFeatureBuilder(featureType);
               
              List<SimpleFeature> list = new ArrayList<SimpleFeature>();
               
              list.add(build.buildFeature("fid1", new Object[]{ geom.point(latitude,longitude).buffer(0.8), "hello" }));
              
              
              SimpleFeatureCollection collection = new ListFeatureCollection(featureType, list);
             
              Transaction transaction = new DefaultTransaction("Add Example");
              store.setTransaction( transaction );
              try {
                   store.addFeatures( collection );
                   transaction.commit(); // actually writes out the features in one go
              }
              catch( Exception eek){
                   transaction.rollback();
               }
              
             }
         create_map(0, featureSource);
  }
  
    public  void create_map(int i , SimpleFeatureSource simplefeature) throws IOException{
        CachingFeatureSource cache = new CachingFeatureSource(simplefeature);
        Style style =null;
        
        if(i==1)
        {
       	   style =SLD.createPolygonStyle(Color.BLUE,null,0);
       	Layer layer = new FeatureLayer(cache, style);
       	map.addLayer(layer);
        } else{
             style = SLD.createSimpleStyle(simplefeature.getSchema());
             final Layer layer = new FeatureLayer(cache, style);
             map.layers().remove(2);
            
             
             SwingWorker worker = new SwingWorker<String, Object>() {
                 protected String doInBackground() throws Exception {
                     // For shapefiles with many features its nice to display a progress bar
                     map.addLayer(layer);
					return featureID;
                 
                 }
              
             };
             // This statement runs the validation method in a background thread
             worker.execute();
        }
        
      
    }
}