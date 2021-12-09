package SharedClasses;

import java.io.Serializable;

public class RegisterMessageTCP implements Serializable {

    public static final long serialVersionID = 8;
    private String nome;
    private String username;
    private String password;
    private boolean registered;

    public RegisterMessageTCP(String nome, String username, String password){
        this.nome = nome;
        this.username = username;
        this.password = password;
        registered = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegistered(boolean connected) { this.registered = connected; }

    public String getNome() { return nome; }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getRegisteredStatus() {
        return registered;
    }
}
