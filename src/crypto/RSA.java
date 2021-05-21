package crypto;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import java.util.Base64;

public class RSA {
    private final BigInteger N;
    private final BigInteger e;
    private final BigInteger d;
    private final int bitlength = 256;

    public RSA() {
        Random r = new Random();
        BigInteger p = BigInteger.probablePrime(bitlength, r);
        BigInteger q = BigInteger.probablePrime(bitlength, r);
        N = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitlength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    public RSA(BigInteger e, BigInteger d, BigInteger N) {
        this.e = e;
        this.d = d;
        this.N = N;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean end = false;
        String message, encryptedMessage, decryptedMessage, publicKey, privateKey;
        RSA rsa = null;
        do {
            System.out.println("Select Operation: \n\t1 -> Key Generator\n\t2 -> Cryptography\n\t3 -> Exit");
            String selection = scanner.nextLine();
            switch (selection){
                case "1":
                    rsa = new RSA();
                    System.out.println("Public Key: "+rsa.getPublicKeyString());
                    System.out.println("Private Key: "+rsa.getPrivateKeyString());
                    break;
                case "2":
                    System.out.println("Select Operation: \n\t1 -> Encrypt\n\t2 -> Decrypt\n\t3 -> Exit");
                    selection = scanner.nextLine();
                    switch (selection){
                        case "1":
                            System.out.println("Enter PublicKey: ");
                            publicKey = scanner.nextLine();
                            System.out.println("Enter Message: ");
                            message = scanner.nextLine();
                            encryptedMessage = RSA.encrypt(message, publicKey);
                            System.out.println("Encrypted Message: "+encryptedMessage);
                            break;
                        case "2":
                            System.out.println("Enter PrivateKey: ");
                            privateKey = scanner.nextLine();
                            System.out.println("Enter EncryptedMessage: ");
                            encryptedMessage = scanner.nextLine();
                            decryptedMessage = RSA.decrypt(encryptedMessage, privateKey);
                            System.out.println("Original Message: "+decryptedMessage);
                            break;
                        case "3":
                            end = true;
                            break;
                        default:
                            System.out.println("Invalid selection...Try again");
                            break;
                    }
                    break;
                case "3":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid selection...Try again");
                    break;
            }
        }while (!end);









//        RSA rsa = new RSA();
//        Scanner scanner = new Scanner(System.in);
//        String teststring;
//        System.out.println("Enter the plain text:");
//        teststring = scanner.nextLine();
//        System.out.println("original Message: " + teststring);
//        System.out.println();
//
//        // Public Key
//        System.out.println("Public Key: " + rsa.e + " , " + rsa.N);
//
//        // Private Key
//        System.out.println("Private Key: " + rsa.d + " , " + rsa.N);
//
//        // encrypt
//        byte[] encrypted = rsa.encrypt(teststring.getBytes());
//        String encryptedString = Base64.getEncoder().encodeToString(encrypted);
//        System.out.println("Encrypted Message: " + encryptedString);
//        System.out.println();
//
//        // decrypt
//        byte[] decrypted = rsa.decrypt(Base64.getDecoder().decode(encryptedString.getBytes()));
//        System.out.println("Decrypted String: " + new String(decrypted));
//        System.out.println();
//        scanner.close();
    }

    // Encrypt message
    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message) {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }

    public String getPublicKeyString() {
        return e.toString() + " , " + N.toString();
    }

    public String getPrivateKeyString() {
        return d.toString() + " , " + N.toString();
    }

    public static String encrypt(String message, String publicKey) {
        String[] arr = publicKey.split(" , ");
        BigInteger e = new BigInteger(arr[0]);
        BigInteger N = new BigInteger(arr[1]);
        RSA rsa = new RSA(e, new BigInteger("0"), N);
        byte[] encrypted = rsa.encrypt(message.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedMessage, String publicKey) {
        String[] arr = publicKey.split(" , ");
        BigInteger d = new BigInteger(arr[0]);
        BigInteger N = new BigInteger(arr[1]);
        RSA rsa = new RSA(new BigInteger("0"), d, N);
        byte[] decrypted = rsa.decrypt(Base64.getDecoder().decode(encryptedMessage.getBytes()));
        return new String(decrypted);
    }
}
