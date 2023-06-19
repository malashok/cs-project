package db;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypt {
        public static final String encryption_algorithm = "MD5";

        public static String encrypt_password(String password) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance(encryption_algorithm);
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        }
}
