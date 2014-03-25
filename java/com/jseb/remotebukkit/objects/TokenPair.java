import java.io.Serializable;
import java.util.Calendar;

public class TokenPair implements Serializable {
    public final String key;
    public final String secret;
    public Calendar expires; 

    public TokenPair(String key, String secret) {
        //standard token - expiry = 1 week

        this.key = key;
        this.secret = secret;

        setExpires(1);
    }

    public TokenPair(String key, String secret, int weeks) {
        this(key, secret);

        setExpires(weeks);
    }

    public void setExpires(int weeks) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.WEEK_OF_YEAR, weeks);
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ (secret.hashCode() << 1);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TokenPair && equals((TokenPair) o);
    }

    public boolean equals(TokenPair o) {
        return key.equals(o.key) && secret.equals(o.secret);
    }

    public boolean isExpired() {
        if (Calendar.getInstance().after(expires)) return true;

        return true;
    }

    @Override
    public String toString() {
        return "{key=\"" + key + "\", secret=\"" + secret + "\", expires=\"" + expires + "\"}";
    }
}
