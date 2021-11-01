package edu.byu.cs.tweeter.model.net.request;

import javax.swing.text.html.ImageView;

/**
 * Contains all the information needed to make a login request.
 */
public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private ImageView image;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private RegisterRequest() {}

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public RegisterRequest(String firstName, String lastName, String username, String password, ImageView image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.image = image;
    }

    /**
     * Returns the first name of the user to be logged in by this request.
     *
     * @return the first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user to be logged in by this request.
     *
     * @return the last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the username of the user to be logged in by this request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of the user to be logged in by this request.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the image of the user to be logged in by this request.
     *
     * @return the image.
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * Sets the image.
     *
     * @param image the imgae.
     */
    public void setImage(ImageView image) {
        this.image = image;
    }
}
