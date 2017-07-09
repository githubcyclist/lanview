/**
 * LANView - Simple screen sharing based around Robot.createScreenCapture.
 * This file contains the server side code.
 * @author githubcyclist
 * @version 1.1
*/
package lanview;

import static spark.Spark.*;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.Enumeration;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LANViewServer extends Application {
	
	public static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	public static PrintStream ps = new PrintStream(baos);
    public static PrintStream old = System.out;
	
	public static void main(String[] args) throws Exception {
		System.setErr(ps);
		ipAddress(getIpAddress());
		staticFileLocation("/public");
		port(8080);
		// This GET request returns a Base64 encoded screen capture.
		get("/screen", (req, res) -> {
			/* 
			 * Creates a byte array converted from the BufferedImage
			 * that Robot.createScreenCapture returns
			*/
			// Creates a rectangle the same size as the screen
			Rectangle screenSize = new Rectangle(
				Toolkit.getDefaultToolkit().getScreenSize());
			// Creates the screen capture
			BufferedImage capture = new Robot().createScreenCapture(screenSize);
			// Returns the Base64 encoded byte array from the screen capture
			return imgToBase64String(capture, "jpg");
		});
		get("/screensize", (req, res) -> {
			Dimension proportions = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) (proportions.width / 1.2);
			int y = (int) (proportions.height / 1.2);
			return x + "," + y;
		});
		Application.launch(new String[]{});
	}
	
	@Override
	public void start(Stage st) throws Exception {
		st.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) { 
				int result = JOptionPane.showConfirmDialog(
						null,
						"Are you sure you want to close the window?"
						+ "This will stop the server.",
						"Warning",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if(result == JOptionPane.YES_OPTION) System.exit(0);
				else event.consume();
			}
			
		});
		VBox vb = new VBox(5);
		vb.setPadding(new Insets(10,10,10,10));
		Scene sc = new Scene(vb, 550, 180);
		TextArea ta = new TextArea();
		ta.setPrefHeight(110);
		ta.setEditable(false);
		ta.setWrapText(true);
		// Put things back
	    System.out.flush();
	    System.setOut(old);
		ta.setText(baos.toString());
		Label infoLabel = new Label("Server started! Your ID is "
				+ getIpAddress() + ".");
		HBox optionsBox = new HBox(5);
		Button whatNextBtn = new Button("What next?");
		whatNextBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				JOptionPane.showMessageDialog(null, "Enter your ID (" + getIpAddress() 
				+ ") into the client and press Connect to view the screen.");	
			}
			
		});
		Button stopServerBtn = new Button("Stop server");
		stopServerBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
				(e) -> System.exit(0));
		optionsBox.getChildren().addAll(whatNextBtn, stopServerBtn);
		vb.getChildren().addAll(ta, infoLabel, optionsBox);
		st.setScene(sc);
		st.setTitle("LANView - Server");
		st.setResizable(false);
		st.show();
	}
	
	public static String getIpAddress() { 
        try {
            for (Enumeration<?> en = NetworkInterface.getNetworkInterfaces();
            		en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<?> enumIpAddr = intf.getInetAddresses();
                		enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        String ipAddress=inetAddress.getHostAddress().toString();
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null; 
	}

	
	/**
	 * Method to convert any image to a Base64 string.
	 * @param img - The image to convert
	 * @param formatName - The format of the finished image
	*/
	public static String imgToBase64String(final RenderedImage img, final String formatName)
	{
	  // Initializes a ByteArrayOutputStream
	  final ByteArrayOutputStream os = new ByteArrayOutputStream();

	  try
	  {
		// Writes the image to the output stream
	    ImageIO.write(img, formatName, os);
	    // Returns the encoded Base64
	    return java.util.Base64.getEncoder().encodeToString(os.toByteArray());
	  }
	  catch (final IOException ioe)
	  {
		// If an exception is encountered, re-throw an UncheckedIOException
	    throw new UncheckedIOException(ioe);
	  }
	}
	
}