package edu.byu.cs.tweeter.model.net.request;

/**
 * Contains all the information needed to make a login request.
 */
public class RegisterRequest extends AuthenticateRequest {

    private String firstName;
    private String lastName;
    private String image;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private RegisterRequest() {
        super(null, null);
    }

    /**
     * Creates an instance.
     *
     * @param username the username of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public RegisterRequest(String firstName, String lastName, String username, String password, String image) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
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
     * Returns the image of the user to be logged in by this request.
     *
     * @return the image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image.
     *
     * @param image the imgae.
     */
    public void setImage(String image) {
        this.image = image;
    }
}
