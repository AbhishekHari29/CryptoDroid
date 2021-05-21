package stegano;

import java.io.File;
import java.util.Scanner;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class LSB {

    public static boolean encode(String path, String original, String ext1, String stegan, String message) {

        String file_name = image_path(path, original, ext1);
        BufferedImage image_orig = getImage(file_name);

        // user space is not necessary for Encrypting
        BufferedImage image = user_space(image_orig);
        image = add_text(image, message);
        return (setImage(image, new File(image_path(path, stegan, "png")), "png"));
    }

    public static String decode(String path, String name) {
        byte[] decode;
        try {
            // user space is necessary for decrypting
            BufferedImage image = user_space(getImage(image_path(path, name, "png")));
            decode = decode_text(get_byte_data(image));
            String decodedMessage = new String(decode);
            return decodedMessage;
        } catch (Exception e) {
            System.out.println("There is no hidden message in this image!");
            e.printStackTrace();
            return "";
        }
    }

    private static String image_path(String path, String name, String ext) {
        return path + "/" + name + "." + ext;
    }

    private static BufferedImage getImage(String f) {
        BufferedImage image = null;
        File file = new File(f);

        try {
            image = ImageIO.read(file);
        } catch (Exception ex) {
            System.out.println("Image could not be read!");
            ex.printStackTrace();
        }
        return image;
    }

    private static boolean setImage(BufferedImage image, File file, String ext) {
        try {
            file.delete(); // delete resources used by the File
            ImageIO.write(image, ext, file);
            return true;
        } catch (Exception e) {
            System.out.println("File could not be saved!");
            e.printStackTrace();
            return false;
        }
    }

    private static BufferedImage add_text(BufferedImage image, String text) {
        // convert all items to byte arrays: image, message, message length
        byte img[] = get_byte_data(image);
        byte msg[] = text.getBytes();
        byte len[] = bit_conversion(msg.length);
        try {
            encode_text(img, len, 0); // 0 first positiong
            encode_text(img, msg, 32); // 4 bytes of space for length: 4bytes*8bit = 32 bits
        } catch (Exception e) {
            System.out.println("Target File cannot hold message!");
            e.printStackTrace();
        }
        return image;
    }

    private static BufferedImage user_space(BufferedImage image) {
        // create new_img with the attributes of image
        BufferedImage new_img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = new_img.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); // release all allocated memory for this image
        return new_img;
    }

    private static byte[] get_byte_data(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    private static byte[] bit_conversion(int i) {
        // only using 4 bytes
        byte byte3 = (byte) ((i & 0xFF000000) >>> 24); // 0
        byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); // 0
        byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); // 0
        byte byte0 = (byte) ((i & 0x000000FF));
        // {0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return (new byte[] { byte3, byte2, byte1, byte0 });
    }

    private static byte[] encode_text(byte[] image, byte[] addition, int offset) {
        // check that the data + offset will fit in the image
        if (addition.length + offset > image.length) {
            throw new IllegalArgumentException("File not long enough!");
        }
        // loop through each addition byte
        for (int i = 0; i < addition.length; ++i) {
            // loop through the 8 bits of each byte
            int add = addition[i];
            for (int bit = 7; bit >= 0; --bit, ++offset) // ensure the new offset value carries on through both loops
            {
                // assign an integer to b, shifted by bit spaces AND 1
                // a single bit of the current byte
                int b = (add >>> bit) & 1;
                // assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
                // changes the last bit of the byte in the image to be the bit of addition
                image[offset] = (byte) ((image[offset] & 0xFE) | b);
            }
        }
        return image;
    }

    private static byte[] decode_text(byte[] image) {
        int length = 0;
        int offset = 32;
        // loop through 32 bytes of data to determine text length
        for (int i = 0; i < 32; ++i) // i=24 will also work, as only the 4th byte contains real data
        {
            length = (length << 1) | (image[i] & 1);
        }

        byte[] result = new byte[length];

        // loop through each byte of text
        for (int b = 0; b < result.length; ++b) {
            // loop through each bit within a byte of text
            for (int i = 0; i < 8; ++i, ++offset) {
                // assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
                result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
            }
        }
        return result;
    }
}
