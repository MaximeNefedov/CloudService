package ru.netology;

import lombok.val;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        val in1 = new BufferedInputStream(new FileInputStream("3.jpeg"));
        val in2 = new BufferedInputStream(new FileInputStream("2.jpeg"));
        val out1 = new FileOutputStream("image1.txt");
        val out2 = new FileOutputStream("image1(1).txt");

        val bytes1 = in1.readAllBytes();
        val bytes2 = in2.readAllBytes();

        System.out.println(bytes1.length);
        System.out.println(bytes2.length);

//        val encodedByteArray = Base64.getEncoder().encode(bytes1);
//        final byte[] decode = Base64.getDecoder().decode(bytes1);
//        System.out.println(encodedByteArray.length);
//        System.out.println(bytes2.length);

//        if (encodedByteArray.length == bytes2.length) {
//            val length = encodedByteArray.length;
//            int counter = 0;
//            while (counter < length) {
//                if (encodedByteArray[counter] != bytes2[counter]) {
//                    System.out.println("Массивы не равны");
//                    System.out.println("Символ " + encodedByteArray[counter] + " не равен символу " + bytes2[counter] + " номер элемента: " + counter);
//                    break;
//                }
//                else counter++;
//                if (counter == length) System.out.println("Массивы равны");
//            }
//        }

        if (bytes1.length == bytes2.length) {
            val length = bytes1.length;
            int counter = 0;
            while (counter < length) {
                if (bytes1[counter] != bytes2[counter]) {
                    System.out.println("Массивы не равны");
                    System.out.println("Символ " + bytes1[counter] + " не равен символу " + bytes2[counter] + " номер элемента: " + counter);
                    break;
                }
                else counter++;
            }
        }

        out1.write(bytes1);
        out2.write(bytes2);
        out1.flush();
        out2.flush();
        in1.close();
        in2.close();
        out1.close();
        out2.close();
    }
}
