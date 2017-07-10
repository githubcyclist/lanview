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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.Collection;
import java.util.Enumeration;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LANViewServer extends Application {
	
	public static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	public static PrintStream ps = new PrintStream(baos);
    public static PrintStream old = System.out;
    
    public static final byte[] logo = 
    		javax.xml.bind.DatatypeConverter.parseBase64Binary("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");
	
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
	    System.err.flush();
	    System.setErr(old);
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
		st.getIcons().add(SwingFXUtils.toFXImage(
				ImageIO.read(new ByteArrayInputStream(logo)), null));
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