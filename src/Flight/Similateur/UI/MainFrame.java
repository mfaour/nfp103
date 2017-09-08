/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Flight.Similateur.UI;

import javax.swing.JFrame;

/**
 *
 * @author mfaour
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.setTitle("Accov-flight simulator...");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnServer = new javax.swing.JButton();
        btnVol = new javax.swing.JButton();
        btnRadar = new javax.swing.JButton();
        btnController = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        txtRefreshTime = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnGraphicRadar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        btnServer.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnServer.setText("Lancer le Serveur");
        btnServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnServerActionPerformed(evt);
            }
        });
        getContentPane().add(btnServer);
        btnServer.setBounds(30, 210, 400, 37);
        btnServer.getAccessibleContext().setAccessibleName("btnServer");

        btnVol.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnVol.setText("Nouveau Vol");
        btnVol.setEnabled(false);
        btnVol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolActionPerformed(evt);
            }
        });
        getContentPane().add(btnVol);
        btnVol.setBounds(30, 260, 400, 37);
        btnVol.getAccessibleContext().setAccessibleName("Nouveau vol");

        btnRadar.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnRadar.setText("Nouveau Radar");
        btnRadar.setEnabled(false);
        btnRadar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRadarActionPerformed(evt);
            }
        });
        getContentPane().add(btnRadar);
        btnRadar.setBounds(30, 310, 400, 37);

        btnController.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnController.setText("Nouveau controlleur");
        btnController.setEnabled(false);
        btnController.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnControllerActionPerformed(evt);
            }
        });
        getContentPane().add(btnController);
        btnController.setBounds(30, 360, 400, 40);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Host name:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(30, 50, 160, 29);

        txtHost.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtHost.setText("PC");
        getContentPane().add(txtHost);
        txtHost.setBounds(240, 40, 190, 35);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Port:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 100, 160, 29);

        txtPort.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPort.setText("9876");
        getContentPane().add(txtPort);
        txtPort.setBounds(240, 90, 190, 35);

        txtRefreshTime.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtRefreshTime.setText("5");
        getContentPane().add(txtRefreshTime);
        txtRefreshTime.setBounds(240, 140, 190, 35);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Refresh time:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(30, 140, 160, 29);

        btnGraphicRadar.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnGraphicRadar.setText("Nouveau Graphic Radar");
        btnGraphicRadar.setEnabled(false);
        btnGraphicRadar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGraphicRadarActionPerformed(evt);
            }
        });
        getContentPane().add(btnGraphicRadar);
        btnGraphicRadar.setBounds(30, 410, 400, 37);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolActionPerformed
        int port = Integer.parseInt(txtPort.getText());   
        int refreshTime = Integer.parseInt(txtRefreshTime.getText());

        VolFrame vf = new VolFrame(port, txtHost.getText(),refreshTime);
         vf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        vf.setVisible(true);

    }//GEN-LAST:event_btnVolActionPerformed

    private void btnServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnServerActionPerformed
        int port = Integer.parseInt(txtPort.getText());
        btnServer.setEnabled(false);
        btnRadar.setEnabled(true);
        btnGraphicRadar.setEnabled(true);
        btnVol.setEnabled(true);
        btnController.setEnabled(true);
        txtHost.setEnabled(false);
        txtPort.setEnabled(false);
        new SACAFrame(port).setVisible(true);
    }//GEN-LAST:event_btnServerActionPerformed

    private void btnRadarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRadarActionPerformed
        int port = Integer.parseInt(txtPort.getText());
        RadarFrame rf = new RadarFrame(port, txtHost.getText());
        rf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rf.setVisible(true);
    }//GEN-LAST:event_btnRadarActionPerformed

    private void btnControllerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnControllerActionPerformed
        int port = Integer.parseInt(txtPort.getText());
        ControllerFrame cf = new ControllerFrame(port, txtHost.getText());
        cf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cf.setVisible(true);
    }//GEN-LAST:event_btnControllerActionPerformed

    private void btnGraphicRadarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGraphicRadarActionPerformed
        int port = Integer.parseInt(txtPort.getText());
        GraphRadarFrame gf = new GraphRadarFrame(port, txtHost.getText());
        gf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gf.setVisible(true);
    }//GEN-LAST:event_btnGraphicRadarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnController;
    private javax.swing.JButton btnGraphicRadar;
    private javax.swing.JButton btnRadar;
    private javax.swing.JButton btnServer;
    private javax.swing.JButton btnVol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtRefreshTime;
    // End of variables declaration//GEN-END:variables
}
