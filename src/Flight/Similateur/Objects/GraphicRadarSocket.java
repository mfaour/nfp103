/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Flight.Similateur.Objects;

import Flight.Similateur.UI.VolFrame;
import Flight.Similateur.Common.Avion;
import Flight.Similateur.Common.Coordonnees;
import Flight.Similateur.Common.Message;
import Flight.Similateur.UI.GraphRadarFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.GraphicsConfiguration;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.DefaultListModel;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author mfaour
 */
public class GraphicRadarSocket extends Thread {

    int colorIndex = 0;
    int PORT = 9876;
    String HOST = "PC";
    boolean _keepRunning = true;
    private String _radarName;
    private GraphRadarFrame _radarFrame;
    private ObjectInputStream _inStream;
    private ObjectOutputStream _outStream;
    private Socket _clientSocket;
    public List<Avion> _detectedFlights;
    public List<BranchGroup> _branchGroupList;

    public GraphicRadarSocket(GraphRadarFrame radarFrame, int port, String host) {
        _radarFrame = radarFrame;
        PORT = port;
        HOST = host;
        try {
            if (!ouvrir_communication()) {
                _radarFrame.txtInfo.append("Erreur lors de la connexion au serveur..");
                return;
            }

            _radarFrame.txtInfo.append("\nRadar est connecte'");
            _outStream = new ObjectOutputStream(_clientSocket.getOutputStream());
            _outStream.flush();
            _inStream = new ObjectInputStream(_clientSocket.getInputStream());
            _radarName = "Radar " + _clientSocket.getLocalPort();
            _radarFrame.setTitle(_radarName);
            Message msg = new Message("Info", "radar", _radarName, null, "SACA");
            SendObject(msg);
            this.start();

        } catch (IOException ex) {
            Logger.getLogger(GraphicRadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void FillFlightsList(Object lstClients) {

        if (lstClients != null) {
            _detectedFlights = (ArrayList) lstClients;
            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < _detectedFlights.size(); i++) {
                BranchGroup bg = createVolGraph(_detectedFlights.get(i));
                _radarFrame._universe.addBranchGraph(bg);

                //listModel.addElement(_detectedFlights.get(i).getInfo());
            }
            //_radarFrame.lstFlights.setModel(listModel);
        }
    }

    private Canvas3D createUniverse() {
        // Get the preferred graphics configuration for the default screen
        GraphicsConfiguration config
                = SimpleUniverse.getPreferredConfiguration();

        // Create a Canvas3D using the preferred configuration
        Canvas3D c = new Canvas3D(config);
        // Create simple universe with view branch
        _radarFrame._universe = new SimpleUniverse(c);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        _radarFrame._universe.getViewingPlatform().setNominalViewingTransform();

        // Ensure at least 5 msec per frame (i.e., < 200Hz)
        _radarFrame._universe.getViewer().getView().setMinimumFrameCycleTime(5);

        return c;
    }

    public BranchGroup createVolGraph1(Avion info) {
        BranchGroup lineGroup = new BranchGroup();
        Appearance app = new Appearance();
        Random random = new Random();
        float R = (float) (Math.random() * 256);
        float G = (float) (Math.random() * 256);
        float B = (float) (Math.random() * 256);

        ColoringAttributes ca = new ColoringAttributes(info.getVolColor(), ColoringAttributes.SHADE_FLAT);
        app.setColoringAttributes(ca);

        Point3f[] plaPts = new Point3f[1];
        Coordonnees pos = info.getPosition();
        Point3f point = new Point3f(pos.getX() / 10000f, pos.getY() / 10000f, .0f);

        PointArray pla = new PointArray(4, GeometryArray.COORDINATES);
        plaPts[0] = point;
        pla.setCoordinates(0, plaPts);
        //between here!
        PointAttributes a_point_just_bigger = new PointAttributes();
        a_point_just_bigger.setPointSize(10.0f);//10 pixel-wide point
        a_point_just_bigger.setPointAntialiasingEnable(true);//now points are sphere-like(not a cube)
        app.setPointAttributes(a_point_just_bigger);
        //and here! sets the point-attributes so it is easily seen.
        Shape3D plShape = new Shape3D(pla, app);
        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.addChild(plShape);

        lineGroup.addChild(objRotate);

        return lineGroup;
    }

    private BranchGroup createVolGraph(Avion info) {

        if (_branchGroupList == null || _branchGroupList.isEmpty()) {
            _branchGroupList = new ArrayList();
        }

        BranchGroup branchGroup = new BranchGroup();
        branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
        Sphere sphere = new Sphere(0.01f);
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();
        Coordonnees pos = info.getPosition();

        Vector3f vector = new Vector3f(pos.getX() / 10000f, pos.getY() / 10000f, .0f);
        // Vector3f vector = new Vector3f(.0f, .0f, .0f);
        transform.setTranslation(vector);
        tg.setTransform(transform);
        tg.addChild(sphere);
        branchGroup.addChild(tg);

        sphere.setName(info.getFlightName());
        //Color3f light1Color = new Color3f(.1f, 1.4f, .1f); // green light
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(info.getVolColor(),
                light1Direction);
        light1.setInfluencingBounds(bounds);
        branchGroup.addChild(light1);

        return branchGroup;
    }

    private void SendObject(Message message) {
        try {
            _outStream.reset();
            _outStream.writeObject(message);
            _outStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(GraphicRadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object ReceiveFlightsList() {
        try {
            return _inStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(GraphicRadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GraphicRadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void ReceiveObject() {
        try {
            if (_clientSocket == null || _clientSocket.isClosed() || !_keepRunning) {
                return;
            }
            Object obj = _inStream.readObject();
            //_radarFrame.txtInfo.append("\n[SACA]>>[ME]: New message received..");
            handleMessage(obj);
        } catch (IOException ex) {
            //Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GraphicRadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleMessage(Object message) {
        if (message == null) {
            _radarFrame.txtInfo.append("\nInvalid message ..");
            return;
        }
        if (message instanceof Message) {
            Message msg = Message.class.cast(message);
            if (msg.command.contains("Info")) {
                _radarFrame.txtInfo.append("\n[" + msg.sender + "] >> [ME]: Caracteristiques de l'avion sont modifiee.. ");
                UpdateFlight(msg);
            } else {
                RemoveFlight(msg);
                _radarFrame.txtInfo.append("\n[" + msg.sender + "] >> [ME]: " + msg.command);
            }
        }
        /*else {
            FillFlightsList(message);
            _radarFrame.txtInfo.append("\n[SACA] >> [ME]: Update of available flights  ");
        }*/
    }

    private void RemoveFlight(Message msg) {

        if (_detectedFlights == null) {
            _detectedFlights = new ArrayList();
        }
        Optional<Avion> optionalFlight = _detectedFlights.stream().filter(f -> f.getFlightId() == msg.content.getFlightId()).findFirst();
        if (optionalFlight != null && optionalFlight.isPresent()) {
            _detectedFlights.remove(optionalFlight.get());

        }
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < _detectedFlights.size(); i++) {
            listModel.addElement(_detectedFlights.get(i).getInfo());
        }
        // _radarFrame._universe.setModel(listModel);
    }

    private Avion UpdateFlight(Message msg) {

        if (_detectedFlights == null) {
            _detectedFlights = new ArrayList();
        }
        Avion updatedAv = null;
        Optional<Avion> optionalFlight = _detectedFlights.stream().filter(f -> f.getFlightId() == msg.content.getFlightId()).findFirst();

        if (optionalFlight != null && optionalFlight.isPresent()) {
            updatedAv = optionalFlight.get();
            updatedAv.setDeplacement(msg.content.getDeplacement());
            updatedAv.setPosition(msg.content.getPosition());

        } else {
            _detectedFlights.add(msg.content);
            updatedAv = msg.content;
        }
        DefaultListModel listModel = new DefaultListModel();
        try {
            Enumeration children = _radarFrame._universe.getViewingPlatform().getViewPlatformTransform().getAllChildren();
            while (children.hasMoreElements()) {

                BranchGroup param = (BranchGroup)children.nextElement();
               
            }
        } catch (Exception e) {
        }

        for (int i = 0; i < _detectedFlights.size(); i++) {
            BranchGroup bg = createVolGraph(_detectedFlights.get(i));
            _radarFrame._universe.addBranchGraph(bg);
        }
        //_radarFrame.lstFlights.setModel(listModel);
        return updatedAv;
    }

    private Boolean ouvrir_communication() {

        try {
            _clientSocket = new Socket(HOST, PORT);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(VolFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void sendClosingObject() {
        try {
            _outStream.reset();
            Object objid = _clientSocket.getLocalPort();
            Message msg = new Message("Connection closed..", "controller", _radarName, null, objid.toString());
            _outStream.writeObject(msg);
            _outStream.flush();

        } catch (IOException ex) {
            Logger.getLogger(VolSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fermer_communication() {
        try {
            // fonction qui permet de fermer la communication
            // avec le gestionnaire de vols
            _radarFrame.txtInfo.append("\nRadar is closed");
            _keepRunning = false;
            sendClosingObject();
            _inStream.close();
            _clientSocket.close();
            this.interrupt();
        } catch (IOException ex) {
            Logger.getLogger(VolFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (_keepRunning) {

            ReceiveObject();
        }
    }
}
