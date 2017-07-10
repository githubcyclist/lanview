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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
	
	 public static final byte[] logo = 
	    		javax.xml.bind.DatatypeConverter.parseBase64Binary("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");
	
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
		st.getIcons().add(SwingFXUtils.toFXImage(
				ImageIO.read(new ByteArrayInputStream(logo)), null));
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