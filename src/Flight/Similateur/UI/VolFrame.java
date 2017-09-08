/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Flight.Similateur.UI;

import Flight.Similateur.Objects.VolSocket;
import javax.swing.UIManager;

/**
 *
 * @author mfaour
 */
public class VolFrame extends javax.swing.JFrame {

    VolSocket _volSocket = null;

    public VolFrame(int port, String host,int refreshTime) {
        initComponents();
        this.setSize(1500, 800);
        this.setTitle("Vol...");
        this.setLocationRelativeTo(null);
        _volSocket = new VolSocket(this,port,host,refreshTime);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        txtInfo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        txtInfo.setEditable(false);
        txtInfo.setColumns(20);
        txtInfo.setFont(new java.awt.Font("Monospaced", 1, 26)); // NOI18N
        txtInfo.setRows(5);
        jScrollPane2.setViewportView(txtInfo);

        getContentPane().add(jScrollPane2, "card2");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (_volSocket != null) {
            _volSocket.fermer_communication();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Look & Feel exception");
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VolFrame().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTextArea txtInfo;
    // End of variables declaration//GEN-END:variables
}
