/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Flight.Similateur.Objects;

import Flight.Similateur.UI.RadarFrame;
import Flight.Similateur.UI.VolFrame;
import Flight.Similateur.Common.Avion;
import Flight.Similateur.Common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author mfaour
 */
public class RadarSocket extends Thread {

    int PORT = 9876;
    String HOST = "PC";
    boolean _keepRunning = true;
    private String _radarName;
    private RadarFrame _radarFrame;
    private ObjectInputStream _inStream;
    private ObjectOutputStream _outStream;
    private Socket _clientSocket;
    public List<Avion> _detectedFlights;

    public RadarSocket(RadarFrame radarFrame) {
        _radarFrame = radarFrame;
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
            Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void FillFlightsList(Object lstClients) {

        if (lstClients != null) {
            _detectedFlights = (ArrayList) lstClients;
            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < _detectedFlights.size(); i++) {
                listModel.addElement(_detectedFlights.get(i).getInfo());

            }
            _radarFrame.lstFlights.setModel(listModel);
        }
    }

    private void SendObject(Message message) {
        try {
            _outStream.reset();
            _outStream.writeObject(message);
            _outStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object ReceiveFlightsList() {
        try {
            return _inStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RadarSocket.class.getName()).log(Level.SEVERE, null, ex);
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
        _radarFrame.lstFlights.setModel(listModel);
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

        for (int i = 0; i < _detectedFlights.size(); i++) {
            listModel.addElement(_detectedFlights.get(i).getInfo());
        }
        _radarFrame.lstFlights.setModel(listModel);
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
