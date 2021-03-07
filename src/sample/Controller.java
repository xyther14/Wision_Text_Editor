package sample;


import application.TextToSpeech;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import marytts.modules.synthesis.Voice;

public class Controller implements Initializable {
    @FXML
    Button newBtn,openBtn,copyAll,saveBtn,findBtn,nextBtn,cutBtn,deleteBtn,compressBtn,replaceBtn,deleteFile,translateBtn,
            readBtn,saveExtBtn,webBtn;
    @FXML
    TextArea noteArea;
    @FXML
    Circle exitBtn;
    @FXML
    TextField searchBox,findText,replaceText;
    @FXML
    Pane utilPane,statusPane,searchPane,compressPane;
    @FXML
    ImageView statusImg,closePane;
    @FXML
    Label statusLbl,fileName,compressStatus;
    @FXML
    Slider fontSize;


    int index;
    boolean saveStatus=false;
    boolean translateFlag=true;
    File selectedFile,openFile;

    Translator translate = new Translator();





    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public String handleButtonEvent(ActionEvent event) throws Exception {
        if(event.getSource()==newBtn){
            searchBox.clear();
            noteArea.clear();
            compressStatus.setVisible(false);
            compressBtn.setDisable(false);
            fileName.setText("untitled.txt");
            saveExtBtn.setDisable(true);
            statusPane.setVisible(false);
            utilPane.setVisible(true);
            noteArea.setVisible(true);
            findText.clear();
            replaceText.clear();
            searchBox.setVisible(true);
            findBtn.setVisible(true);
            searchPane.setVisible(true);


        }

        if(event.getSource()==translateBtn) {
            if(translateFlag){
                readBtn.setDisable(true);
                StringBuilder sb = new StringBuilder();
                sb.append(translate.translate("en","kn",noteArea.getText()));
                noteArea.clear();
                noteArea.setText(sb.toString());
                translateFlag=false;
            }else {
                readBtn.setDisable(false);
                StringBuilder sb1 = new StringBuilder();
                sb1.append(translate.translate("kn","en",noteArea.getText()));
                noteArea.clear();
                noteArea.setText(sb1.toString());
                translateFlag=true;
            }
        }
        if(event.getSource()==readBtn){
            if(!noteArea.getText().isEmpty() && !noteArea.getSelectedText().isEmpty()) {
                String text = noteArea.getSelectedText();
                TextToSpeech tts = new TextToSpeech();
//            Voice.getAvailableVoices().stream().forEach(System.out::println);
                tts.setVoice("cmu-rms-hsmm");
                tts.speak(text, 1.5f, false, true);
            }
        }
        if(event.getSource()==webBtn && !noteArea.getSelectedText().isEmpty()){
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("https://www.google.com/search?q="+noteArea.getSelectedText().replaceAll("\\s","")));
            }
        }

        if(event.getSource()==copyAll){
            String myText = noteArea.getSelectedText();
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(myText);
            clipboard.setContent(content);

        }
        if(event.getSource()==cutBtn){
            String myText = noteArea.getSelectedText();
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(myText);
            clipboard.setContent(content);
            noteArea.setText(noteArea.getText().replace(noteArea.getSelectedText(),""));

        }
        if(event.getSource()==deleteBtn){
            noteArea.setText(noteArea.getText().replace(noteArea.getSelectedText(),""));
        }
        if(event.getSource()==saveExtBtn){
            File file = new File(selectedFile.getAbsolutePath().replace("\\","\\\\"));
            FileWriter fw = new FileWriter(file);
            fw.write(noteArea.getText());
            saveStatus = true;
            fw.close();
            if(saveStatus){
                saveExtBtn.setDisable(true);
            }else{
                saveExtBtn.setDisable(false);
            }
        }


        if(event.getSource()==openBtn){
            searchBox.clear();
            noteArea.setVisible(true);
            findText.clear();
            replaceText.clear();
            compressStatus.setVisible(false);
            searchBox.setVisible(true);
            compressBtn.setDisable(false);
            statusPane.setVisible(false);
            findBtn.setVisible(true);
            utilPane.setVisible(true);
            searchPane.setVisible(true);
            Stage stage = new Stage();
            try {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open existing file");
                selectedFile = chooser.showOpenDialog(stage);
                openFile = new File(selectedFile.getAbsolutePath());
                FileReader fr = new FileReader(openFile);
                BufferedReader br = new BufferedReader(fr);
                StringBuilder sb = new StringBuilder();
                String myText = "";
                while ((myText = br.readLine()) != null) {
                    sb.append(myText + "\n");
                }
                noteArea.setText(sb.toString());
                fileName.setText(selectedFile.getName());
                saveStatus = true;
                saveExtBtn.setDisable(false);
                deleteFile.setDisable(false);
                fr.close();
            }catch (Exception e){
                noteArea.setVisible(false);
                statusPane.setVisible(true);
                utilPane.setVisible(false);
                compressBtn.setDisable(true);
                searchPane.setVisible(false);
            }

        }
        if(event.getSource()==saveBtn){
            Stage stage = new Stage();
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
            chooser.setInitialFileName("*.txt");
            selectedFile = chooser.showSaveDialog(stage);
            if (selectedFile != null) {
                if (selectedFile.getName().endsWith(".txt")) {
                    FileWriter fw = new FileWriter(selectedFile);
                    fw.write(noteArea.getText());
                    saveStatus = true;
                    fw.close();
                    if(saveStatus){
                        fileName.setText(selectedFile.getName());
                        saveExtBtn.setDisable(false);
                        deleteFile.setDisable(false);
                    }
                } else {
                    fileName.setText("untitled.txt");
                    saveStatus = false;
                }
            }

        }
        if(event.getSource()==findBtn && !searchBox.getText().isEmpty()){
             index = noteArea.getText().indexOf(searchBox.getText());
            System.out.println(index);
            if (index == -1) {
                System.out.println("not found");
            } else {
                //  errorText.setText("Found");
                noteArea.selectRange(index, index + searchBox.getLength());
            }

        }
        if(event.getSource()==deleteFile){
            File file = new File(selectedFile.getAbsolutePath().replace("\\","\\\\"));
            if(file.delete()){
                noteArea.clear();
                noteArea.setVisible(false);
                utilPane.setVisible(false);
                searchPane.setVisible(false);
                statusPane.setVisible(true);
                Image image = new Image("dust2.gif");
                statusImg.setImage(image);
                statusLbl.setText("File deleted successfully");
                deleteBtn.setDisable(true);
                fileName.setText("");
                deleteFile.setDisable(true);
                compressBtn.setDisable(true);
            }else{
                System.out.println("File can't be deleted.");
            }
        }
        if(event.getSource()==compressBtn){
            noteArea.toBack();
            compressPane.setVisible(true);
        }
        if(event.getSource()==replaceBtn){
            Pattern pattern = Pattern.compile(findText.getText());
            Matcher matcher = pattern.matcher(noteArea.getText());
            String result = matcher.replaceAll(replaceText.getText());
            noteArea.setText(result);
            findText.clear();
            replaceText.clear();
        }
        if(event.getSource()==nextBtn){
            System.out.println(index);
            int i = noteArea.getText().indexOf(searchBox.getText(),index+1);
            System.out.println(i);
            index = i;
            if(i==-1){
                System.out.println("not found");
            } else {
                noteArea.selectRange(i,i+searchBox.getLength());
            }
        }

        return null;
    }

    public void handleMouseEvent(MouseEvent event) {
        if(event.getSource()==closePane){
            compressPane.setVisible(false);
        }
        if(event.getSource()==exitBtn){
            System.exit(1);
        }
        if(event.getSource()==fontSize && !noteArea.getText().isEmpty()){
            noteArea.setFont(Font.font("Segoe UI Light",fontSize.getValue()));
        }
    }

}

