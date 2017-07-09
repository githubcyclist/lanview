/**
 * LANView - Simple screen sharing based around Robot.createScreenCapture.
 * This file contains the client side code.
 * @author githubcyclist
 * @version 1.0
*/
package lanview;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class LANViewClient extends Application implements EventHandler<ActionEvent> {
	
	public static String appArgs[] = null;
	
	public static final String CWD =
			Paths.get(".").toAbsolutePath().normalize().toString();
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		appArgs = args;
		try {
			launch(args);
		} catch(Exception e) {
			//dispose of exception
		}
	}

	Button connectBtn;

	@Override
	public void handle(ActionEvent event) {
		if(event.getSource().equals(connectBtn)) System.out.println("Connect!");
	}

	@Override
	public void start(Stage st) throws Exception {
		StackPane sp = new StackPane();
		Scene sc = new Scene(sp, 350, 100);
		TextField ipField = new TextField();
		ipField.setPromptText("Enter ID");
		ipField.setStyle("-fx-pref-width: 150px;");
		connectBtn = new Button("Connect");
		connectBtn.setStyle
			(
					"-fx-pref-width: 100px;"
					+ "-fx-pref-height: 10px;");
		Button exitSessionButton = new Button("Exit Session");
		exitSessionButton.getStyleClass().add("button-raised");
		connectBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {
			
            public void handle(MouseEvent me) {
     		StackPane sp = new StackPane();
     		Scene sc = new Scene(sp, 350, 100);
             
     		
            	String targetIP = ipField.getText();
            	Stage primaryStage = new Stage();
            	exitSessionButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
        				new EventHandler<MouseEvent>() {

        			@Override
        			public void handle(MouseEvent me1) {
        				primaryStage.hide();
                		st.show();
        			}
        			
        		});
        		BorderPane sp1 = new BorderPane();
        		int width = 1152, height = 864;
        		try {
					String[] size = 
							stringFromURL("http://" + targetIP + ":8080/screensize")
							.split(",");
					width = Integer.parseInt(size[0]);
					height = Integer.parseInt(size[1]);
				} catch (IOException e1) {
					int option = JOptionPane.showConfirmDialog(
							null,
							"Error connecting to " + targetIP + ".\n"
							+ "Failed to retrieve screen size."
							+ "Retry?",
							"Error",
							JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.NO_OPTION) System.exit(0);
					e1.printStackTrace();
				}
        		Scene sc1 = new Scene(sp1, width, height);
        		final Canvas canvas = new Canvas(width, height);
        		sp1.setCenter(canvas);
        		HBox upperBar = new HBox(5);
        		sp1.setTop(upperBar);
        		upperBar.getChildren().add(exitSessionButton);
        		GraphicsContext gc = canvas.getGraphicsContext2D();
        		primaryStage.setScene(sc1);
        		primaryStage.setTitle("LANView - " + targetIP);
        		primaryStage.show();
        		st.hide();
        		st.setOnCloseRequest(new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {
						System.exit(0);
					}
        			
        		});
        		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent event) {
						System.exit(0);
					}
        			
        		});
        		new Thread(()->{
        			while(true) {
            			try {
            				int width1 = 1152, height1 = 864;
            					String[] size = 
            							stringFromURL("http://" + targetIP + ":8080/screensize")
            							.split(",");
            					width1 = Integer.parseInt(size[0]);
            					height1 = Integer.parseInt(size[1]);
    						String screen = stringFromURL("http://" + targetIP +
    								":8080/screen");
    						BufferedImage decoded = ImageIO.read(
    								new ByteArrayInputStream(
    										java.util.Base64.getDecoder()
    										.decode(screen.getBytes())));
    						decoded = convertToBufferedImage(decoded.getScaledInstance(
    								width1, height1, java.awt.Image.SCALE_DEFAULT
    						));
    						Image img = 
    								javafx.embed.swing.SwingFXUtils.toFXImage(decoded,
    										null);
    						gc.drawImage(img, 0, 0);
    						Thread.sleep(1000);
    					} catch (Exception e) {
    						int option = JOptionPane.showConfirmDialog(
    								null,
    								"Error connecting to " + targetIP + "."
    								+ "Retry?",
    								"Error",
    								JOptionPane.YES_NO_OPTION);
    						if(option == JOptionPane.NO_OPTION) System.exit(0);
    						e.printStackTrace();
    					}
            		}
        		}).start();
            }
            
        });
		
		HBox ipBoxTop = new HBox(10);
		HBox.setHgrow(ipField, Priority.NEVER);
		VBox ipBoxContainer = new VBox();
		VBox.setVgrow(ipBoxTop, Priority.ALWAYS);
		ipBoxContainer.getChildren().add(ipBoxTop);
		ipBoxTop.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(ipBoxTop, Pos.TOP_CENTER);
		ipBoxTop.getChildren().addAll(ipField, connectBtn);
		sp.getChildren().add(ipBoxContainer);
		st.setTitle("LANView - Connect");
		st.setResizable(false);
		st.setScene(sc);
		st.show();
	}
	
	public static BufferedImage convertToBufferedImage(java.awt.Image image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	public String stringFromURL(String url) throws IOException {
		URL url1 = new URL(url);
		Scanner scan = new Scanner(url1.openStream());
		return scan.nextLine();
	}

}