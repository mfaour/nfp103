/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Flight.Similateur.Objects;

import Flight.Similateur.UI.ControllerFrame;
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
public class ControllerSocket extends Thread {

    int PORT = 9876;
    String HOST = "PC";
    boolean _keeprunning = true;
    private String _controllerName;
    private ControllerFrame _controllerFrame;
    private ObjectInputStream _inStream;
    private ObjectOutputStream _outStream;
    private Socket _clientSocket;
    public static List<Avion> _detectedFlights;

    public  void UpdateFlightCap(int volId, int cap) throws InterruptedException {
        if (_detectedFlights == null) {
            return;
        }
        Optional<Avion> optionalFlight = _detectedFlights.stream().filter(f -> f.getFlightId() == volId).findFirst();

        if (optionalFlight == null) {
            return;
        }
        Avion selectedFlight = optionalFlight.get();

        selectedFlight.Lock();
        _controllerFrame.txtInfo.append("\n[ME]>>[" + selectedFlight.getFlightName() + "]: Changement du cap...");
        selectedFlight.getDeplacement().setCap(cap);
        Message msg = new Message("changer_cap", "controller", _controllerName, selectedFlight, selectedFlight.getFlightName());
        SendObject(msg);
    }

    public void UpdateFlightAltitude(int volId, int altitude) throws InterruptedException {
        if (_detectedFlights == null) {
            return;
        }
        Optional<Avion> optionalFlight = _detectedFlights.stream().filter(f -> f.getFlightId() == volId).findFirst();

        if (optionalFlight == null) {
            return;
        }

        Avion selectedFlight = optionalFlight.get();

        selectedFlight.Lock();
        _controllerFrame.txtInfo.append("\n[ME]>>[" + selectedFlight.getFlightName() + "]: Changement d'altitude...");
        selectedFlight.getPosition().setAltitude(altitude);
        Message msg = new Message("changer_altitude", "controller", _controllerName, selectedFlight, selectedFlight.getFlightName());
        SendObject(msg);
    }

    public void UpdateFlightVitesse(int volId, int vitesse) throws InterruptedException {
        if (_detectedFlights == null) {
            return;
        }
        Optional<Avion> optionalFlight = _detectedFlights.stream().filter(f -> f.getFlightId() == volId).findFirst();

        if (optionalFlight == null) {
            return;
        }
        Avion selectedFlight = optionalFlight.get();

        selectedFlight.Lock();
        _controllerFrame.txtInfo.append("\n[ME]>>[" + selectedFlight.getFlightName() + "]: Changement de vitesse...");
        selectedFlight.getDeplacement().setVitesse(vitesse);
        Message msg = new Message("changer_vitesse", "controller", _controllerName, selectedFlight, selectedFlight.getFlightName());
        SendObject(msg);
    }

    public ControllerSocket(ControllerFrame controllerFrame,int port,String host) {
        _controllerFrame = controllerFrame;
        PORT = port;
        HOST = host;
        try {
            if (!ouvrir_communication()) {
                _controllerFrame.txtInfo.append("Erreur lors de la connexion au SACA..");
                return;
            }
            _controllerFrame.txtInfo.append("\nController est connecte'");
            _outStream = new ObjectOutputStream(_clientSocket.getOutputStream());
            _outStream.flush();
            _inStream = new ObjectInputStream(_clientSocket.getInputStream());
            _controllerName = "Controller " + _clientSocket.getLocalPort();
            _controllerFrame.setTitle(_controllerName);
            List<Avion> dd = _detectedFlights;
            Message msg = new Message("Info", "Controller", _controllerName, null, "SACA");
            SendObject(msg);
            this.start();

        } catch (IOException ex) {
            Logger.getLogger(ControllerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   /* public void FillFlightsList(Object lstClients) {
        _detectedFlights = new ArrayList();

        if (lstClients != null) {
            _detectedFlights = (ArrayList) lstClients;
            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < _detectedFlights.size(); i++) {
                listModel.addElement(_detectedFlights.get(i).getInfo());
                //_detectedFlights.add(_detectedFlights.get(i));
            }
            _controllerFrame.lstFlights.setModel(listModel);
        }
    }*/

    public void SendObject(Message message) {
        try {
            _outStream.reset();
            _outStream.writeObject(message);
            _outStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ControllerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ReceiveObject() {
        try {
            if (_clientSocket == null || _clientSocket.isClosed()) {
                return;
            }
            Object obj = _inStream.readObject();
           // _controllerFrame.txtInfo.append("\n[SACA]>>[ME]: New message received..");
            handleMessage(obj);
        } catch (IOException ex) {
           // Logger.getLogger(ControllerSocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ControllerSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleMessage(Object message) {
        if (message == null) {
            _controllerFrame.txtInfo.append("\nInvalid message ..");
            return;
        }
        if (message instanceof Message) {
            Message msg = Message.class.cast(message);
            if (msg.command.contains("Info")) {
                _controllerFrame.txtInfo.append("\n[" + msg.sender + "] >> [ME]: Caracteristiques de l'avion sont modifiee.. ");
                UpdateFlight(msg);

            } else if (msg.command.contains("Updated")) {
                _controllerFrame.txtInfo.append("\n[" + msg.sender + "] >> [ME]: " + msg.command);
                Avion updatedAvion = UpdateFlight(msg);
                if (updatedAvion != null) {
                    updatedAvion.Unlock();
                    
                }
            } else {
                // avion crashed or landed
                RemoveFlight(msg);
                _controllerFrame.txtInfo.append("\n[" + msg.sender + "] >> [ME]: " + msg.command);
            }
        }
        /*else {
            FillFlightsList(message);
            _controllerFrame.txtInfo.append("\n[SACA] >> [ME]: Update of available flights  ");
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
        _controllerFrame.lstFlights.setModel(listModel);
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
        _controllerFrame.lstFlights.setModel(listModel);
        return updatedAv;
    }

    private Boolean ouvrir_communication() {

        try {
            _clientSocket = new Socket(HOST, PORT);
            return true;

        } catch (IOException ex) {
            Logger.getLogger(VolFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void fermer_communication() {
        try {
            // fonction qui permet de fermer la communication
            // avec le gestionnaire de vols
            _controllerFrame.txtInfo.append("\nController closed");
            _keeprunning = false;
            sendClosingObject();
            if (_clientSocket != null) {
                _clientSocket.close();
            }
            if (_inStream != null) {
                _inStream.close();
            }
            if (_outStream != null) {
                _outStream.close();
            }
            this.interrupt();

        } catch (IOException ex) {
            Logger.getLogger(VolFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendClosingObject() {
        try {
            _outStream.reset();
            Object objid = _clientSocket.getLocalPort();
            Message msg = new Message("Connection closed..", "controller", _controllerName, null, objid.toString());
            _outStream.writeObject(msg);
            _outStream.flush();

        } catch (IOException ex) {
            Logger.getLogger(VolSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (_keeprunning) {
            ReceiveObject();
        }
    }
}
