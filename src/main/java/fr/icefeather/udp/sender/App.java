package fr.icefeather.udp.sender;

import fr.icefeather.udp.sender.utils.UdpSender;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App {
    private JTextField HostTextField;
    private JTextField PortTextField;
    private JPanel PanelApp;
    private JButton EnvoyerButton;
    private JEditorPane MessageEditorPane;
    private JScrollPane MessageScrollPane;

    public App() {
        final AppForm appForm = new AppForm();
        final AppTextField hostForm = new AppTextField(HostTextField, "Destination host (ex: 127.0.0.1)", null, true);
        appForm.formFields.add(hostForm);
        final AppTextField portForm = new AppTextField(PortTextField, "5060", "5060", true);
        appForm.formFields.add(portForm);
        final AppTextField messageEditorForm = new AppTextField(MessageEditorPane, "Message à envoyer", null, true);
        appForm.formFields.add(messageEditorForm);

        EnvoyerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (appForm.valider()) {
                    try {
                        UdpSender.send(hostForm.getText(), Integer.parseInt(portForm.getText()), MessageEditorPane.getText().getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e1) {
                        JOptionPane d = new JOptionPane();
                        d.showMessageDialog( PanelApp.getParent(),
                                e1.getMessage(),
                                "Erreur lors de l'envoi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        JFrame frame = new JFrame("udp-sender");
        JPanel panel = new App().PanelApp;
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    class AppTextField implements AppFormField {

        public JTextComponent textField;
        public Boolean isPlaceholder = false;
        public String placeholder;
        public String defaultText;
        public Boolean notnull;

        public AppTextField(JTextComponent jTextField, String placeholder, String defaultText, Boolean notnull) {
            this.textField = jTextField;
            this.placeholder = placeholder;
            this.defaultText = defaultText;
            this.notnull = notnull;
            if (defaultText == null || defaultText.isEmpty()) {
                showPlaceholder();
            } else {
                showDefaultText();
            }
            listeners();
        }

        public String getText() {
            if (!isPlaceholder) {
                return textField.getText();
            }
            return null;
        }

        private void showDefaultText() {
            isPlaceholder = false;
            textField.setForeground(Color.BLACK);
            textField.setFont(new Font(textField.getFont().getFamily(), Font.PLAIN, textField.getFont().getSize()));
            textField.setText(defaultText);
        }

        private void showPlaceholder() {
            isPlaceholder = true;
            textField.setText(placeholder);
            textField.setForeground(Color.GRAY);
            textField.setFont(new Font(textField.getFont().getFamily(), Font.ITALIC, textField.getFont().getSize()));
        }

        private void listeners() {
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (isPlaceholder) {
                        showDefaultText();
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (textField.getText().isEmpty()) {
                        showPlaceholder();
                    }
                }
            });
        }

        @Override
        public Boolean valider() {
            if (notnull) {
                if (getText() == null || getText().isEmpty()) {
                    textField.setForeground(Color.RED);
                    return false;
                }
            }
            return true;
        }
    }


    interface AppFormField {
        Boolean valider();
    }


    class AppForm {

        public List<AppFormField> formFields = new ArrayList<>();

        public Boolean valider(){
            boolean valide = true;
            for (AppFormField formField : formFields){
                if(!formField.valider()){
                    valide = false;
                }
            }
            return valide;
        }

    }
}
