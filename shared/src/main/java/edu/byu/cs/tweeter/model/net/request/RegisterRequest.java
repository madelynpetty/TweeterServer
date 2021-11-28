package edu.byu.cs.tweeter.model.net.request;

/**
 * Contains all the information needed to make a login request.
 */
public class RegisterRequest extends AuthenticateRequest {

    private String firstName;
    private String lastName;
    private String imageUrl;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private RegisterRequest() {
        super(null, null);
    }

    /**
     * Creates an instance.
     *
     * @param alias the username of the user to be logged in.
     * @param password the password of the user to be logged in.
     */
    public RegisterRequest(String firstName, String lastName, String alias, String password, String imageUrl) {
        super(alias, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
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
     * @return the imageUrl.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image.
     *
     * @param imageUrl the imageUrl.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
