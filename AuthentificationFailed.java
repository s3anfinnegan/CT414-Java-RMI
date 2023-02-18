//authentification failure
class AuthentificationFailed extends Exception {
    //exceptions means authentification failure (incorrect token or timeout)
    public AuthentificationFailed(String message) {
        super(message);
    }
}